package com.example.weatherapplication.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapplication.domain.model.LocationData
import com.google.gson.Gson
import showNotification

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val locationJson = inputData.getString("location") ?: return Result.failure()
        val location = Gson().fromJson(locationJson, LocationData::class.java)
        showNotification(applicationContext, location)
        return Result.success()
    }
}

