package com.example.weatherapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.ui.graphics.Brush
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.BrokenCloudsEnd
import com.example.weatherapplication.ui.theme.BrokenCloudsStart
import com.example.weatherapplication.ui.theme.ClearSkyDayEnd
import com.example.weatherapplication.ui.theme.ClearSkyDayStart
import com.example.weatherapplication.ui.theme.FewCloudsEnd
import com.example.weatherapplication.ui.theme.FewCloudsStart
import com.example.weatherapplication.ui.theme.MistEnd
import com.example.weatherapplication.ui.theme.MistStart
import com.example.weatherapplication.ui.theme.RainDayEnd
import com.example.weatherapplication.ui.theme.RainDayStart
import com.example.weatherapplication.ui.theme.SnowEnd
import com.example.weatherapplication.ui.theme.SnowStart
import com.example.weatherapplication.ui.theme.ThunderstormEnd
import com.example.weatherapplication.ui.theme.ThunderstormStart
import com.example.weatherapplication.utils.Constants.Companion.PREF_NAME
import java.util.Locale


fun getWeatherIcon(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.raw.sunrise
        "01n" -> R.raw.sky
        "02d" -> R.raw.few_clouds
        "03d", "03n" -> R.raw.few_clouds
        "04d", "04n" -> R.raw.broken_clouds
        "09d", "09n" -> R.raw.rain
        "10d", "10n" -> R.raw.rain
        "11d", "11n" -> R.raw.thunderstorm
        "13d", "13n" -> R.raw.snow
        "50d", "50n" -> R.raw.fog
        else -> R.raw.clear_sky
    }
}

fun getWeatherGradient(description: String): Brush {
    val englishDescription = mapWeatherDescriptionToEnglish(description)
    return when (englishDescription.lowercase()) {
        "clear sky" -> Brush.verticalGradient(listOf(ClearSkyDayStart, ClearSkyDayEnd))
        "few clouds", "scattered clouds" -> Brush.verticalGradient(listOf(FewCloudsStart, FewCloudsEnd))
        "broken clouds", "overcast clouds" -> Brush.verticalGradient(listOf(BrokenCloudsStart, BrokenCloudsEnd))
        "shower rain", "light rain", "moderate rain", "heavy rain", "rain" ->
            Brush.verticalGradient(listOf(RainDayStart, RainDayEnd))
        "thunderstorm" -> Brush.verticalGradient(listOf(ThunderstormStart, ThunderstormEnd))
        "snow", "light snow", "heavy snow" -> Brush.verticalGradient(listOf(SnowStart, SnowEnd))
        "mist", "fog", "haze" -> Brush.verticalGradient(listOf(MistStart, MistEnd))
        else -> Brush.verticalGradient(listOf(ClearSkyDayStart, ClearSkyDayEnd))
    }
}
fun mapWeatherDescriptionToEnglish(description: String): String {
    return when (description.lowercase()) {
        "سماء صافية" -> "clear sky"
        "غيوم متفرقة", "بعض الغيوم" -> "few clouds"
        "غيوم متناثرة" -> "scattered clouds"
        "غيوم قاتمة", "غيوم مكسورة" -> "broken clouds"
        "غيوم متكاثفة", "غيوم ملبدة" -> "overcast clouds"
        "أمطار خفيفة" -> "light rain"
        "أمطار معتدلة" -> "moderate rain"
        "أمطار غزيرة" -> "heavy rain"
        "أمطار" -> "rain"
        "عاصفة رعدية" -> "thunderstorm"
        "ثلج" -> "snow"
        "ثلوج خفيفة" -> "light snow"
        "ثلوج كثيفة" -> "heavy snow"
        "ضباب", "شبورة" -> "mist"
        "غبار", "عوالق" -> "haze"
        else -> description.lowercase()
    }
}

fun abbreviationTempUnit(tempUnit: String): String = when (tempUnit) {
    "Celsius °C" -> "°C"
    "Kelvin °K" -> "°K"
    "Fahrenheit °F" -> "°F"
    else -> "metric"
}

fun checkForInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}



fun setLocale(context: Context, language: String) {
    val locale = getLanguageCode(language)

    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit()
        .putString("language", language)
        .apply()

}

fun getLanguageCode(language: String) : Locale{
    return when (language) {
        "English" -> Locale("en")
        "Arabic" -> Locale("ar")
        else -> Locale.getDefault()
    }
}


fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun formatNumberBasedOnLanguage(number: String): String {
    val language = Locale.getDefault().language
    return if (language == "ar") convertToArabicNumbers(number) else number
}

fun formatTemperatureUnitBasedOnLanguage(unit: String): String {
    val language = Locale.getDefault().language
    if (language == "ar") {
        return when (unit) {
            "°C" -> "°س"
            "°F" -> "°ف"
            "°K" -> "°ك"
            else -> "°س"
        }
    }
    return unit
}

fun formatWindSpeedBasedOnLanguage(unit: String): String {
    val language = Locale.getDefault().language
    return if (language == "ar") {
        when (unit) {
            "mile/hour" -> "م/س"
            "meter/sec" -> "م/ث"
            else -> unit
        }
    } else {
        unit
    }
}


