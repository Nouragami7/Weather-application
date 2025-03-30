package com.example.weatherapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alert")
data class AlertData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,
    val startTime: String
)
