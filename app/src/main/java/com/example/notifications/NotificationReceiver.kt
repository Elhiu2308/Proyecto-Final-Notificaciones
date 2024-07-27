package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver: BroadcastReceiver() {

    companion object {
    const val ACTION_RECIVED = "action_recived"
}
    override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == ACTION_RECIVED){

                Toast.makeText(context, "Conectado exitosamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
