package com.example.weatherapplication.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.TimeZone



fun convertToEgyptTime(timestamp: Long): Pair<String, String> {
    val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    dateFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo")
    timeFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo")

    val date = dateFormat.format(Date(timestamp * 1000))
    val time = timeFormat.format(Date(timestamp * 1000))

    return Pair(date, time)
}


fun getCurrentDate(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Cairo"))
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun convertUnixToTime(unixTime: Long): String {
    val date = Date(unixTime * 1000) // Convert seconds to milliseconds
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format as "06:30 AM"
    format.timeZone = TimeZone.getDefault() // Set to local timezone
    return format.format(date)
}

fun convertToHour(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val date = inputFormat.parse(dateTime) ?: return "Invalid Time"
        outputFormat.format(date)
    } catch (e: Exception) {
        "Invalid Time"
    }
}

fun getDayNameFromDate(dateStr: String): String {
    val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}



