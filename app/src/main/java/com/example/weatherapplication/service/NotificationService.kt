package com.example.weatherapplication.service

/*
class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationJson = intent?.getStringExtra("location") ?: return START_NOT_STICKY
        val location = Gson().fromJson(locationJson, LocationData::class.java)

        val delay = calculateDelay(15, 26)
        scheduleNotification(this, location, delay)

        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}*/
