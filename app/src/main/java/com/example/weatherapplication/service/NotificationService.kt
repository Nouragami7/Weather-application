package com.example.weatherapplication.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.utils.calculateDelay
import com.example.weatherapplication.worker.scheduleNotification
import com.google.gson.Gson

class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationJson = intent?.getStringExtra("location") ?: return START_NOT_STICKY
        val location = Gson().fromJson(locationJson, LocationData::class.java)

        val delay = calculateDelay(3, 18)
        scheduleNotification(this, location, delay)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}