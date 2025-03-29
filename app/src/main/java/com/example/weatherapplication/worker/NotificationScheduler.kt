package com.example.weatherapplication.worker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapplication.domain.model.LocationData
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

fun scheduleNotification(context: Context, location: LocationData, delayInMillis: Long) {
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
        .setInputData(workDataOf("location" to Gson().toJson(location)))
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
