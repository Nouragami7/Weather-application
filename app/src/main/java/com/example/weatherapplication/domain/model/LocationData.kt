package com.example.weatherapplication.domain.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.weatherapplication.utils.Converters

@Entity(tableName = "Weather", primaryKeys = ["latitude", "longitude"])
@TypeConverters(Converters::class)
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val currentWeather: CurrentWeather,
    val forecast: Forecast,
    val country: String ,
    val city: String
)


