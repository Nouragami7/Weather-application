package com.example.weatherapplication.datasource.local

import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun getAllLocations():Flow<List<LocationData>>
    suspend fun insertLocation(locationData: LocationData)
    suspend fun deleteLocation(lat: Double, lng: Double)
}