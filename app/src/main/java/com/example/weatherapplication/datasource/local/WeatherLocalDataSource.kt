package com.example.weatherapplication.datasource.local

import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val locationDAO: LocationDAO) : IWeatherLocalDataSource {
    override suspend fun getAllLocations(): Flow<List<LocationData>> {
        return locationDAO.getAllLocations()
    }

    override suspend fun insertLocation(locationData: LocationData) {
        return locationDAO.insertLocation(locationData)
    }

    override suspend fun deleteLocation(lat: Double, lng: Double) {
        return locationDAO.deleteLocation(lat, lng)
    }

    override suspend fun insertAlert(alertData: AlertData) {
        return locationDAO.insertAlert(alertData)


    }

    override suspend fun getAllAlerts(): Flow<List<AlertData>> {
        return locationDAO.getAllAlert()
    }

    override suspend fun deleteAlert(alertData: AlertData) {
        return locationDAO.deleteAlert(alertData)
    }

}