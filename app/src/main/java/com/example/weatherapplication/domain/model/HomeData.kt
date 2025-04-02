package com.example.weatherapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HomeData")
data class HomeData(
    @PrimaryKey val id: Int = 1,
    val currentWeather: CurrentWeather,
    val forecast: Forecast
)