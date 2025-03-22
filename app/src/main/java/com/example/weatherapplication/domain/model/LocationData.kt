package com.example.weatherapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Weather")
data class LocationData(
    @PrimaryKey var id: Int,
    var latitude: Long,
    var longitude: Long
)



