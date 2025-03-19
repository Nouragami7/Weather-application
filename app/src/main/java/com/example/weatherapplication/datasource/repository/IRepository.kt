package com.example.weatherapplication.datasource.repository

import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, apiKey: String): Flow<CurrentWeather?>
    suspend fun getForecast(lat: Double, lon: Double, apiKey: String): Flow<Forecast?>
}