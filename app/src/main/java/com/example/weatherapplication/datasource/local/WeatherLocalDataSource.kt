package com.example.weatherapplication.datasource.local

import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource (private val locationDAO: LocationDAO):IWeatherLocalDataSource{
    override  fun getAllLocations(): Flow<List<LocationData>> {
        return locationDAO.getAllLocations()
    }

    override suspend fun insertLocation(locationData: LocationData) {
        return locationDAO.insertLocation(locationData)
    }

    override suspend fun deleteLocation(lat: Double, lng: Double) {
        return locationDAO.deleteLocation(lat, lng)
    }

}