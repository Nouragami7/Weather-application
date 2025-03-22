package com.example.weatherapplication.datasource.repository

import android.util.Log
import com.example.weatherapplication.datasource.local.IWeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.IWeatherRemoteDataSource
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(
    private val remoteDataSource: IWeatherRemoteDataSource,
    private val localDataSource: IWeatherLocalDataSource
) : IRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String,
        apiKey: String
    ): Flow<CurrentWeather?> {
       return remoteDataSource.getCurrentWeather(lat, lon,lang,unit, apiKey)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String,
        apiKey: String
    ): Flow<Forecast?> {
        return remoteDataSource.getForecast(lat, lon, lang, unit, apiKey)
    }

    override suspend fun getAllLocations(): Flow<List<LocationData>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertLocation(locationData: LocationData) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocation(id: Int) {
        TODO("Not yet implemented")
    }

    companion object { // do not need to make an object of this class to call getInstance fun
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(remoteDataSource: IWeatherRemoteDataSource, localDataSource: IWeatherLocalDataSource): WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(remoteDataSource, localDataSource)
                instance = temp
                temp
            }
        }
    }
}