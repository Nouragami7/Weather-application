package com.example.weatherapplication.ui.screen.homescreen

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.example.weatherapplication.ui.theme.ColorTextPrimary
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.ui.viewmodel.HomeViewModel
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.SharedPreference
import com.example.weatherapplication.utils.abbreviationTempUnit
import com.example.weatherapplication.utils.convertToEgyptTime

@Composable
fun HomeScreen(modifier: Modifier = Modifier, location: MutableState<Location>) {
    val TAG = "HomeScreen"
    val context = LocalContext.current
    val sharedPreferences = SharedPreference()

    var language by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "language") ?: "en") }
    var tempUnit by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C") }
    var windSpeedUnit by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec") }

    val factory = HomeViewModel.WeatherFactory(
        WeatherRepository.getInstance(
            WeatherRemoteDataSource(
                RetrofitHelper.retrofitInstance.create(ApiService::class.java)
            ),WeatherLocalDataSource(WeatherDatabase.getDatabase(context).locationDao())
        )
    )
    val viewModel: HomeViewModel = viewModel(factory = factory)

    val weatherState by viewModel.weatherData.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        language = sharedPreferences.getFromSharedPreference(context, "language") ?: "en"
        tempUnit = sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C"
        windSpeedUnit = sharedPreferences.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec"
    }

    LaunchedEffect(location, language, tempUnit, windSpeedUnit) {
        val unit = when (tempUnit) {
            "Celsius °C" -> "metric"   // Temp: °C, Wind Speed: meter/sec
            "Kelvin °K" -> "standard"  // Temp: K, Wind Speed: meter/sec
            "Fahrenheit °F" -> "imperial"  // Temp: °F, Wind Speed: miles/hour
            else -> "metric"
        }
        val lang = when (language) {
            "Arabic" -> "ar"
            "English" -> "en"
            else -> "en"
        }

        viewModel.fetchWeatherData(sharedPreferences.getFromSharedPreference(context,"latitude")?.toDoubleOrNull() ?: location.value.latitude, sharedPreferences.getFromSharedPreference(context,"longitude")?.toDoubleOrNull() ?: location.value.longitude, lang , unit, Constants.API_KEY)
        viewModel.fetchForecastData(sharedPreferences.getFromSharedPreference(context,"latitude")?.toDoubleOrNull() ?: location.value.latitude, sharedPreferences.getFromSharedPreference(context,"longitude")?.toDoubleOrNull() ?: location.value.longitude, language, unit, Constants.API_KEY)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                weatherState is ResponseState.Loading || forecastState is ResponseState.Loading -> {
                    LoadingIndicator()
                }
                weatherState is ResponseState.Failure || forecastState is ResponseState.Failure -> {
                    val errorMessage = (weatherState as? ResponseState.Failure)?.message?.message
                        ?: (forecastState as? ResponseState.Failure)?.message?.message
                        ?: stringResource(R.string.unknown_error)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                weatherState is ResponseState.Success<*> && forecastState is ResponseState.Success<*> -> {
                    val weather = (weatherState as ResponseState.Success<CurrentWeather>).data
                    val forecast = (forecastState as ResponseState.Success<Forecast>).data
                    HomeContent(weather, forecast, tempUnit, windSpeedUnit)
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    weather: CurrentWeather?,
    forecast: Forecast,
    tempUnit: String,
    windSpeedUnit: String,
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            weather?.let {
                LocationInfo(location = weather.name)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        weather?.let {
            val (date, time) = convertToEgyptTime(weather.dt.toLong())
            DailyWeatherCard(
                modifier = Modifier.padding(bottom = 24.dp),
                forecast = weather.weather.firstOrNull()?.description ?: "Unknown",
                feelsLike = "${weather.main.feels_like} ${abbreviationTempUnit(tempUnit)}",
                pressure = stringResource(R.string.hpa, weather.main.pressure),
                clouds = "${weather.clouds.all}%",
                windSpeed = "${weather.wind.speed} $windSpeedUnit",
                humidity = "${weather.main.humidity}%",
                currentTemperature = "${weather.main.temp.toInt()} ${abbreviationTempUnit(tempUnit)}",
                currentDate = date,
                currentTime = time,
                icon = weather.weather.firstOrNull()?.icon ?: "",
                sunrise = weather.sys.sunrise.toLong(),
                sunset = weather.sys.sunset.toLong()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(R.string.hourly_forecast),
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to primaryContainerDark,
                        1f to onPrimaryDark
                    ),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                ),
                color = onPrimaryDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            HourlyForecastCard(forecast)
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LocationInfo(
    modifier: Modifier = Modifier,
    location: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location_pin),
                contentDescription = null,
                modifier = Modifier.height(18.dp),
                contentScale = ContentScale.FillHeight
            )
            Text(
                text = location,
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
