package com.example.weatherapplication.utils
import java.util.Calendar

fun calculateDelay(targetHour: Int, targetMinute: Int): Long {
    val now = Calendar.getInstance()
    val targetTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, targetHour)
        set(Calendar.MINUTE, targetMinute)
        set(Calendar.SECOND, 0)
    }

    if (targetTime.before(now)) {
        targetTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    return targetTime.timeInMillis - now.timeInMillis
}
