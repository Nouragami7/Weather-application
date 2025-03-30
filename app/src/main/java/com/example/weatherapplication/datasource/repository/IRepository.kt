package com.example.weatherapplication.datasource.repository

import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.domain.model.HomeData
import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang: String,unit: String, apiKey: String): Flow<CurrentWeather?>
    suspend fun getForecast(lat: Double, lon: Double,lang: String,unit: String, apiKey: String): Flow<Forecast?>
    suspend fun getAllLocations(): Flow<List<LocationData>>
    suspend fun insertLocation(locationData: LocationData)
    suspend fun deleteLocation(lat: Double, lng: Double)
    suspend fun insertAlert(alertData: AlertData):Long
    suspend fun getAllAlerts(): Flow<List<AlertData>>
    suspend fun deleteAlert(alertData: AlertData)
    suspend fun insertHomeData(homeData: HomeData)
    suspend fun getHomeData():Flow<HomeData>

}