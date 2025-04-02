package com.example.weatherapplication.ui.screen.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stopNotificationSound

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        stopNotificationSound()

        val notificationId = intent.getIntExtra("notification_id", 1)

        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }

        CoroutineScope(Dispatchers.IO).launch {
            scheduleNotification(context, 60000, notificationId)
        }
    }
}