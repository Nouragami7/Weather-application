package com.example.weatherapplication.ui.screen.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherapplication.MainActivity
import stopNotificationSound

class StopSoundReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        stopNotificationSound()
        if (intent.getBooleanExtra("open_main", false)) {
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(mainIntent)
        }
    }
}