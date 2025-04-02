package com.example.weatherapplication.ui.screen.detailscreen.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.datasource.local.WeatherDatabase
import com.example.weatherapplication.datasource.local.WeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.ApiService
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.remote.RetrofitHelper
import com.example.weatherapplication.datasource.remote.WeatherRemoteDataSource
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.ui.screen.detailscreen.viewmodel.DetailsViewModel
import com.example.weatherapplication.ui.screen.homescreen.view.HomeContent
import com.example.weatherapplication.ui.screen.homescreen.view.LoadingIndicator
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.SharedPreference
import com.example.weatherapplication.utils.getLanguageCode
import com.example.weatherapplication.utils.getUnit

@Composable
fun DetailsScreen(
    LocationData: LocationData
) {
    Log.i("TAG", "DetailsScreen:  $LocationData ")
    val context = LocalContext.current
    val sharedPreferences = SharedPreference()

    var lang by remember {
        mutableStateOf(
            sharedPreferences.getFromSharedPreference(
                context,
                "language"
            ) ?: "en"
        )
    }
    var tempUnit by remember {
        mutableStateOf(
            sharedPreferences.getFromSharedPreference(
                context,
                "tempUnit"
            ) ?: "Celsius °C"
        )
    }
    var windSpeedUnit by remember {
        mutableStateOf(
            sharedPreferences.getFromSharedPreference(
                context,
                "windSpeedUnit"
            ) ?: "meter/sec"
        )
    }

    val factory = DetailsViewModel.DetailsFactory(
        WeatherRepository.getInstance(
            WeatherRemoteDataSource(
                RetrofitHelper.retrofitInstance.create(ApiService::class.java)
            ), WeatherLocalDataSource(WeatherDatabase.getDatabase(context).locationDao())
        )
    )
    val viewModel: DetailsViewModel = viewModel(factory = factory)

    val weatherState by viewModel.weatherData.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        lang = sharedPreferences.getFromSharedPreference(context, "language") ?: "en"
        tempUnit = sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C"
        windSpeedUnit =
            sharedPreferences.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec"
    }

    LaunchedEffect(LocationData.latitude, LocationData.longitude, lang, tempUnit) {
        viewModel.fetchWeatherData(
            LocationData.latitude,
            LocationData.longitude,
            getLanguageCode(lang).language,
            getUnit(tempUnit),
            Constants.API_KEY
        )
        viewModel.fetchForecastData(
            LocationData.latitude,
            LocationData.longitude,
            getLanguageCode(lang).language,
            getUnit(tempUnit),
            Constants.API_KEY
        )
    }

    when {
        weatherState is ResponseState.Loading || forecastState is ResponseState.Loading -> {
            LoadingIndicator()
        }

        weatherState is ResponseState.Failure || forecastState is ResponseState.Failure -> {
                HomeContent(
                    LocationData.currentWeather,
                    LocationData.forecast,
                    tempUnit,
                    windSpeedUnit
                )

        }

        weatherState is ResponseState.Success<*> && forecastState is ResponseState.Success<*> -> {
            val weather = (weatherState as ResponseState.Success<CurrentWeather>).data
            val forecast = (forecastState as ResponseState.Success<Forecast>).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HomeContent(weather, forecast, tempUnit, windSpeedUnit)
            }

        }
    }
}