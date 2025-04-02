package com.example.weatherapplication.ui.screen.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapplication.ui.screen.notification.worker.NotificationWorker
import java.util.concurrent.TimeUnit

fun scheduleNotification(context: Context, delayInMillis: Long, alertId: Int) {

    val inputData = Data.Builder()
        .putInt(NotificationWorker.ALERT_ID_KEY, alertId)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        NotificationWorker.getWorkName(alertId),
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

fun cancelNotification(context: Context, alertId: Int) {
    WorkManager.getInstance(context).cancelUniqueWork(
        NotificationWorker.getWorkName(alertId)
    )
}