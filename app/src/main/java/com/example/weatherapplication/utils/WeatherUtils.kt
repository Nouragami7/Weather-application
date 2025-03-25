package com.example.weatherapplication.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.weatherapplication.ui.theme.RainNightEnd
import com.example.weatherapplication.ui.theme.RainNightStart
import com.example.weatherapplication.ui.theme.SnowEnd
import com.example.weatherapplication.ui.theme.SnowStart
import com.example.weatherapplication.ui.theme.ThunderstormEnd
import com.example.weatherapplication.ui.theme.ThunderstormStart


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
    return when (description.lowercase()) {
        "clear sky" -> Brush.verticalGradient(listOf(ClearSkyDayStart, ClearSkyDayEnd))
        "few clouds", "scattered clouds" -> Brush.verticalGradient(listOf(FewCloudsStart, FewCloudsEnd))
        "broken clouds", "overcast clouds" -> Brush.verticalGradient(listOf(BrokenCloudsStart, BrokenCloudsEnd))
        "shower rain", "light rain", "moderate rain", "heavy rain" -> Brush.verticalGradient(listOf(RainDayStart, RainDayEnd))
        "rain" -> Brush.verticalGradient(listOf(RainNightStart, RainNightEnd))
        "thunderstorm" -> Brush.verticalGradient(listOf(ThunderstormStart, ThunderstormEnd))
        "snow", "light snow", "heavy snow" -> Brush.verticalGradient(listOf(SnowStart, SnowEnd))
        "mist", "fog", "haze" -> Brush.verticalGradient(listOf(MistStart, MistEnd))
        else -> Brush.verticalGradient(listOf(Color.White,Color.White))
    }
}

