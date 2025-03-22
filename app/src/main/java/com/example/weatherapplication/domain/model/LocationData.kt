package com.example.weatherapplication.domain.model

import androidx.room.Entity

@Entity(tableName = "Weather", primaryKeys = ["latitude", "longitude"])
data class LocationData(
    val latitude: Double,
    val longitude: Double
)


