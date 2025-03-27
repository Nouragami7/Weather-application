package com.example.weatherapplication.datasource.remote

import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double,lang: String,unit: String, apiKey: String): Flow<CurrentWeather>
    suspend fun getForecast(lat: Double, lon: Double,lang: String,unit: String, apiKey: String): Flow<Forecast>
}