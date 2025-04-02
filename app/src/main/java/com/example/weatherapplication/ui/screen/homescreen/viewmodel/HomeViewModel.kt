package com.example.weatherapplication.ui.screen.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.IRepository
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.HomeData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: IRepository) : ViewModel() {
    private val TAG = "tag"
    private val mutableWeatherData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val weatherData = mutableWeatherData.asStateFlow()

    private val mutableForecastData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val forecastData = mutableForecastData.asStateFlow()

    private val mutableHomeData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val homeData = mutableHomeData.asStateFlow()


    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun fetchWeatherData(lat: Double, lon: Double, lang: String, unit: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherData = repository.getCurrentWeather(lat, lon, lang, unit, apiKey)
                weatherData.collect {
                    mutableWeatherData.value = ResponseState.Success(it)
                    Log.i(TAG, "fetchWeatherData from api: $it")
                    mutableMessage.emit("Weather data fetched successfully")
                }
            } catch (e: Exception) {
                mutableWeatherData.value = ResponseState.Failure(e)
                mutableMessage.emit("Error fetching weather data: ${e.message}")
                Log.e(TAG, "fetchWeatherData: ${e.message}")
            }
        }
    }

    fun fetchForecastData(lat: Double, lon: Double, lang: String, unit: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val forecastData = repository.getForecast(lat, lon, lang, unit, apiKey)
                forecastData.collect {
                    mutableForecastData.value = ResponseState.Success(it)
                    Log.i(TAG, "fetchForecastData from forecast: $it")
                    mutableMessage.emit("Forecast data fetched successfully")
                }
            } catch (e: Exception) {
                mutableForecastData.value = ResponseState.Failure(e)
                mutableMessage.emit("Error fetching forecast data: ${e.message}")
                Log.e(TAG, "fetchForecastData: ${e.message}")
            }
        }

    }


    fun insertHomeDate(homeData: HomeData) {
        viewModelScope.launch {
            repository.insertHomeData(homeData)
        }
    }

    fun getHomeData() {
        viewModelScope.launch {
            repository.getHomeData().collect {
                mutableHomeData.value = ResponseState.Success(it)
                Log.i(TAG, "getHomeData from db: $it")

            }
        }
    }

    class WeatherFactory(
        private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}