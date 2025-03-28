package com.example.weatherapplication.ui.screen.detailscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.R
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
import com.example.weatherapplication.ui.screen.homescreen.HomeContent
import com.example.weatherapplication.ui.screen.homescreen.LoadingIndicator
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.ui.viewmodel.DetailsViewModel
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.SharedPreference

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

    LaunchedEffect(LocationData.latitude, LocationData.longitude, lang, tempUnit, windSpeedUnit) {
        val unit = when (tempUnit) {
            "Celsius °C" -> "metric"
            "Kelvin °K" -> "standard"
            "Fahrenheit °F" -> "imperial"
            else -> "metric"
        }
        viewModel.fetchWeatherData(
            LocationData.latitude,
            LocationData.longitude,
            lang,
            unit,
            Constants.API_KEY
        )
        viewModel.fetchForecastData(
            LocationData.latitude,
            LocationData.longitude,
            lang,
            unit,
            Constants.API_KEY
        )
    }

    when {
        weatherState is ResponseState.Loading || forecastState is ResponseState.Loading -> {
            LoadingIndicator()
        }

        weatherState is ResponseState.Failure || forecastState is ResponseState.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                0f to inversePrimaryDarkHighContrast,
                                1f to SkyBlue,
                                1f to LightBlue
                            )
                        )
                ) {
                    Text(
                        text = stringResource(R.string.offline_mode),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                HomeContent(
                    LocationData.currentWeather,
                    LocationData.forecast,
                    tempUnit,
                    windSpeedUnit
                )
            }
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