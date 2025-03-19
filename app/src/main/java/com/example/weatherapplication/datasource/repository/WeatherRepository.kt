package com.example.weatherapplication.datasource.repository

import android.util.Log
import com.example.weatherapplication.datasource.remote.IWeatherRemoteDataSource
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(
    private val remoteDataSource: IWeatherRemoteDataSource
) : IRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<CurrentWeather?> {
       return remoteDataSource.getCurrentWeather(lat, lon, apiKey)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<Forecast?> {
        return remoteDataSource.getForecast(lat, lon, apiKey)
    }

    companion object { // do not need to make an object of this class to call getInstance fun
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(remoteDataSource: IWeatherRemoteDataSource): WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(remoteDataSource)
                instance = temp
                temp
            }
        }
    }
}