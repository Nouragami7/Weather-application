package com.example.weatherapplication.ui.screen.homescreen

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
import com.example.weatherapplication.utils.convertToHour
import com.example.weatherapplication.utils.getCurrentDate


@Composable
fun HomeScreen(modifier: Modifier, location: Location) {
    val TAG = "tag"
    val context = LocalContext.current

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
        viewModel.message.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(location) {
        viewModel.fetchWeatherAndForecastData(
            lat = 31.20663675,
            lon = 29.907445625,
            apiKey = Constants.API_KEY
        )
    }

    Log.d(TAG, "Current weatherState: $weatherState")

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
            if (weatherState is ResponseState.Loading || forecastState is ResponseState.Loading) {
                Log.i(TAG, "Loading...")
                LoadingIndicator()
            } else if (weatherState is ResponseState.Failure || forecastState is ResponseState.Failure) {
                val errorMessage = when {
                    weatherState is ResponseState.Failure -> (weatherState as ResponseState.Failure).message.message
                    forecastState is ResponseState.Failure -> (forecastState as ResponseState.Failure).message.message
                    else -> "Unknown error"
                } ?: "Unknown error"
                Log.e(TAG, "Failure: $errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            } else if (weatherState is ResponseState.Success<*> && forecastState is ResponseState.Success<*>) {
                val weather = (weatherState as ResponseState.Success<CurrentWeather>).data
                val forecast = (forecastState as ResponseState.Success<Forecast>).data
                HomeContent(weather, forecast)
            }
        }}
}
@Composable
private fun HomeContent(weather: CurrentWeather?, forecast: Forecast) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (weather != null) {
                LocationInfo(location = (weather.name))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (weather != null) {
            val (date, time) = convertToEgyptTime(weather.dt.toLong())
            DailyWeatherCard(
                modifier = Modifier.padding(bottom = 24.dp),
                forecast = weather.weather.firstOrNull()?.description ?: "Unknown",
                feelsLike="${weather.main.feels_like}",
                pressure = "${weather.main.pressure} hPa",
                clouds = "${weather.clouds.all}%",
                windSpeed = "${weather.wind.speed} km/h",
                humidity = "${weather.main.humidity}%",
                currentTemperature = "${weather.main.temp}°C",
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
