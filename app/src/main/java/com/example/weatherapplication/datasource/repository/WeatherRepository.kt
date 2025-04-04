package com.example.weatherapplication.datasource.repository

import com.example.weatherapplication.datasource.local.IWeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.IWeatherRemoteDataSource
import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.domain.model.HomeData
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
    ): Flow<CurrentWeather> {
       return remoteDataSource.getCurrentWeather(lat, lon,lang,unit, apiKey)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String,
        apiKey: String
    ): Flow<Forecast> {
        return remoteDataSource.getForecast(lat, lon, lang, unit, apiKey)
    }

    override suspend fun getAllLocations(): Flow<List<LocationData>> {
      return localDataSource.getAllLocations()
    }

    override suspend fun insertLocation(locationData: LocationData) {
        return localDataSource.insertLocation(locationData)
    }

    override suspend fun deleteLocation(lat: Double, lng: Double) {
       return localDataSource.deleteLocation(lat, lng)
    }

    override suspend fun insertAlert(alertData: AlertData): Long {
        return localDataSource.insertAlert(alertData)

    }

    override suspend fun getAllAlerts(): Flow<List<AlertData>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun deleteAlert(alertData: AlertData) {
        return localDataSource.deleteAlert(alertData)
    }

    override suspend fun insertHomeData(homeData: HomeData) {
        localDataSource.insertHomeData(homeData)
    }

    override suspend fun getHomeData(): Flow<HomeData> {
        return localDataSource.getHomeData()
    }

    companion object {
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