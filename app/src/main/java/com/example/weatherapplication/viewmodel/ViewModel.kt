package com.example.weatherapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val TAG = "tag"
    private val mutableWeatherData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val weatherData =
        mutableWeatherData.asStateFlow() // converts the mutable flow into an immutable one

    private val mutableForecastData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val forecastData = mutableForecastData.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    fun fetchWeatherAndForecastData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try{
                mutableWeatherData.value = ResponseState.Loading
                mutableForecastData.value = ResponseState.Loading

               coroutineScope {
                   launch {
                       fetchWeatherData(lat, lon, apiKey)
                   }
                   launch {
                       fetchForecastData(lat, lon, apiKey)
                   }
               }

            }catch (e:Exception){
                mutableWeatherData.value = ResponseState.Failure(e)
                mutableForecastData.value = ResponseState.Failure(e)
                mutableMessage.emit("Error fetching weather and forecast data: ${e.message}")
            }
        }
    }


    fun fetchWeatherData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherData = repository.getCurrentWeather(lat, lon, apiKey)
                weatherData.collect {
                    mutableWeatherData.value = ResponseState.Success(it)
                    fetchForecastData(lat, lon, apiKey)
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

    fun fetchForecastData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val forecastData = repository.getForecast(lat, lon, apiKey)
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

    class WeatherFactory(
        private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}