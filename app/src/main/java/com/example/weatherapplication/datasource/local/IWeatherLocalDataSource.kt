package com.example.weatherapplication.datasource.local

import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    fun getAllLocations():Flow<List<LocationData>>
    suspend fun insertLocation(locationData: LocationData)
    suspend fun deleteLocation(id: Int)
}