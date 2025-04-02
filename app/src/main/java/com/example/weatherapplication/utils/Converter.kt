package com.example.weatherapplication.utils

import androidx.room.TypeConverter
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeather(currentWeather: CurrentWeather): String {
        return gson.toJson(currentWeather)
    }

    @TypeConverter
    fun toCurrentWeather(data: String): CurrentWeather {
        val type = object : TypeToken<CurrentWeather>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromForecast(forecast: Forecast): String {
        return gson.toJson(forecast)
    }

    @TypeConverter
    fun toForecast(data: String): Forecast {
        val type = object : TypeToken<Forecast>() {}.type
        return gson.fromJson(data, type)
    }
}
