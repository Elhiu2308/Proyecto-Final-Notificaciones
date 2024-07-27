package com.example.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notifications.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging







const val CHANNEL_OTHERS = "others"




class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var hasNotificationPermissionGranted: Boolean =
        false //para solicitar a versiones menores a api 33 y se verifiquen los permisos, de la version tiramisu se solicita permiso para notificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationChannel()
        }

        binding.apply {

            btnNotify.setOnClickListener {
                simpleNotification()
            }


            btnActionNotify.setOnClickListener {
                touchNotification()
            }

            btnNotifyWithBtn.setOnClickListener {
                buttonNotification()
            }

        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Error", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("FCM_TOKEN",token)
            Toast.makeText(baseContext,"FCM token: $token", Toast.LENGTH_SHORT).show()
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) // si es arriba de tiramisu se solicitan notificaciones
        } else {
            hasNotificationPermissionGranted = true  // si es menor que tiramisu sera true
        }

    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> // se pregunta si si tenemos el permiso de notificaciones
            hasNotificationPermissionGranted =
                isGranted                                      // seteamos este valos
            if (!isGranted) {                                                                 // si no hemos dado el permiso, se lanza una notificacion para solicitar el permiso
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            20
                        )
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNotificationChannel() {
        val name = getString(R.string.channel_courses)
        val descriptionText = getString(R.string.courses_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            CHANNEL_OTHERS,
            name,
            importance
        ).apply { description = descriptionText }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun simpleNotification(){

        var notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.triforce) //seteamos el ícono de la push notification
            .setColor(getColor(R.color.triforce)) //definimos el color del ícono y el título de la notificación
            .setContentTitle(getString(R.string.simple_title)) //seteamos el título de la notificación
            .setContentText(getString(R.string.simple_body)) //seteamos el cuerpo de la notificación
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Ponemos una prioridad por defecto
            .build()

        //lanzamos la notificación

//        with(NotificationManagerCompat.from(this)) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

//        NotificationManagerCompat.from(this).notify(20, notification) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

        NotificationManagerCompat.from(this).apply {
            notify(20, notification) //en este caso pusimos un id genérico
        }
    }

        @SuppressLint("MissingPermission")
        private fun touchNotification() {
            val intent =
                Intent(this, NewBeduActivity::class.java).apply { //navergar entre activities
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

            var notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
                .setSmallIcon(R.drawable.bedu_icon) //seteamos el ícono de la push notification
                .setContentTitle(getString(R.string.action_title)) //seteamos el título de la notificación
                .setContentText(getString(R.string.action_body)) //seteamos el cuerpo de la notificación
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Ponemos una prioridad por defecto
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            //lanzamos la notificación

//        with(NotificationManagerCompat.from(this)) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

//        NotificationManagerCompat.from(this).notify(20, notification) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

            NotificationManagerCompat.from(this).apply {
                notify(20, notification) //en este caso pusimos un id genérico
            }
        }


    @SuppressLint("MissingPermission")
    private fun buttonNotification() {
        val intent = Intent(this, NotificationReceiver::class.java).apply { //navergar entre activities
            action = NotificationReceiver.ACTION_RECIVED
            }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        var notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.bedu_icon) //seteamos el ícono de la push notification
            .setContentTitle(getString(R.string.button_title)) //seteamos el título de la notificación
            .setContentText(getString(R.string.button_body)) //seteamos el cuerpo de la notificación
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Ponemos una prioridad por defecto
            .addAction(R.drawable.bedu_icon, getString(R.string.button_text), pendingIntent)
            .setAutoCancel(true)
            .build()

        //lanzamos la notificación

//        with(NotificationManagerCompat.from(this)) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

//        NotificationManagerCompat.from(this).notify(20, notification) {
//            notify(20, builder.build()) //en este caso pusimos un id genérico
//        }

        NotificationManagerCompat.from(this).apply {
            notify(20, notification) //en este caso pusimos un id genérico
        }
    }


    }