package com.example.weatherapplication.datasource.remote

import android.util.Log
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CurrentWeatherRemoteDataSource(private val apiService: ApiService) : IWeatherRemoteDataSource {
    private val TAG = "tag"
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<CurrentWeather?> {
        val response = apiService.getCurrentWeather(lat, lon, apiKey)
        if (response.isSuccessful) {
            Log.i(TAG, "Response successful: ${response.body()}")
        } else {
            Log.i(TAG, "Response not successful: ${response.errorBody()}")
        }
        return flowOf(response.body())
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<Forecast?> {
        val response = apiService.getForecast(lat, lon, apiKey)
        if (response.isSuccessful) {
            Log.i(TAG, "Response successful: ${response.body()}")
        } else {
            Log.i(TAG, "Response not successful: ${response.errorBody()}")
        }
        return flowOf(response.body()!!)


    }


}

