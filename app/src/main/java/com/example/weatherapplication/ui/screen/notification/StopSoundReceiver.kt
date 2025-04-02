package com.example.weatherapplication.ui.screen.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapplication.MainActivity
import stopNotificationSound

class StopSoundReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        stopNotificationSound()
        val notificationId = intent.getIntExtra("notification_id", 1)

        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }

        if (intent.getBooleanExtra("open_main", false)) {
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(mainIntent)
        }
    }
}