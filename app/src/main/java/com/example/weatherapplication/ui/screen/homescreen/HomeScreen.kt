package com.example.weatherapplication.ui.screen.homescreen

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.R
import com.example.weatherapplication.datasource.remote.*
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.ui.theme.ColorTextPrimary
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.ui.viewmodel.WeatherViewModel
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.convertToEgyptTime
import com.example.weatherapplication.utils.SharedPreference

@Composable
fun HomeScreen(modifier: Modifier = Modifier, location: Location) {
    val TAG = "HomeScreen"
    val context = LocalContext.current
    val sharedPreferences = SharedPreference()

    var lang by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "language") ?: "en") }
    var tempUnit by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C") }
    var windSpeedUnit by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec") }

    val factory = WeatherViewModel.WeatherFactory(
        WeatherRepository.getInstance(
            CurrentWeatherRemoteDataSource(
                RetrofitHelper.retrofitInstance.create(ApiService::class.java)
            )
        )
    )
    val viewModel: WeatherViewModel = viewModel(factory = factory)

    val weatherState by viewModel.weatherData.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        lang = sharedPreferences.getFromSharedPreference(context, "language") ?: "en"
        tempUnit = sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C"
        windSpeedUnit = sharedPreferences.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec"
    }

    LaunchedEffect(location, lang, tempUnit, windSpeedUnit) {
        val unit = when (tempUnit) {
            "Celsius °C" -> "metric"
            "Kelvin °K" -> "standard"
            "Fahrenheit °F" -> "imperial"
            else -> "metric"
        }

        Log.d(TAG, "Fetching data with lang: $lang, unit: $unit, windSpeed: $windSpeedUnit")

        viewModel.fetchWeatherAndForecastData(
            lat = location.latitude,
            lon = location.longitude,
            lang = lang,
            unit = unit,
            apiKey = Constants.API_KEY
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                weatherState is ResponseState.Loading || forecastState is ResponseState.Loading -> {
                    Log.i(TAG, "Loading...")
                    LoadingIndicator()
                }
                weatherState is ResponseState.Failure || forecastState is ResponseState.Failure -> {
                    val errorMessage = (weatherState as? ResponseState.Failure)?.message?.message
                        ?: (forecastState as? ResponseState.Failure)?.message?.message
                        ?: "Unknown error"

                    Log.e(TAG, "Failure: $errorMessage")
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
private fun HomeContent(weather: CurrentWeather?, forecast: Forecast, tempUnit: String, windSpeedUnit: String) {

    val tempUnitAbbreviation = when (tempUnit) {
        "Celsius °C" -> "°C"
        "Kelvin °K" -> "°K"
        "Fahrenheit °F" -> "°F"
        else -> "metric"
    }

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
                feelsLike = "${weather.main.feels_like} $tempUnitAbbreviation",
                pressure = "${weather.main.pressure} hPa",
                clouds = "${weather.clouds.all}%",
                windSpeed = "${weather.wind.speed} $windSpeedUnit",
                humidity = "${weather.main.humidity}%",
                currentTemperature = "${weather.main.temp} $tempUnitAbbreviation",
                currentDate = date,
                currentTime = time
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                "Hourly Forecast",
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
