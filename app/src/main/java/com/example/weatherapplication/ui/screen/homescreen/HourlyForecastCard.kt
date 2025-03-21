package com.example.weatherapplication.ui.screen.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapplication.R
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.ui.theme.BabyBlue
import com.example.weatherapplication.ui.theme.IceBlue
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.PaleSkyBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.SoftSkyBlue
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.ui.viewmodel.WeatherViewModel
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.convertToHour
import com.example.weatherapplication.utils.getCurrentDate

data class HourlyForecast(
    val hour: String,
    val temperature: String,
    val icon: Int,
    val backgroundColor: Color
)

fun getRandomColor(): Color {
    val colors = listOf(SkyBlue, LightSkyBlue, SoftSkyBlue, PaleSkyBlue, LightBlue)
    return colors.random()
}

@Composable
fun HourlyForecastCard(
    forecast: Forecast,
    modifier: Modifier = Modifier
) {

    /* LaunchedEffect(Unit) {
        viewModel.fetchForecastData(
            lat = 31.20663675,
            lon = 29.907445625,
            apiKey = Constants.API_KEY
        )
    }*/

    /* when (forecastState) {
        is ResponseState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResponseState.Failure -> {
            val errorMessage = (forecastState as ResponseState.Failure).message.message ?: "Unknown error"
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $errorMessage", color = Color.Red)
            }
        }

        is ResponseState.Success<*> -> {
            val forecast = (forecastState as ResponseState.Success<Forecast>).data
            val currentDate = getCurrentDate().substring(0, 10)
            val todayForecastList = forecast.list
                .filter { it.dt_txt.startsWith(currentDate) }
                .map { item ->
                    HourlyForecast(
                        hour = convertToHour(item.dt_txt),
                        temperature = "${item.main.temp.toInt()}",
                        icon = getWeatherIcon(item.weather.firstOrNull()?.icon ?: ""),
                        backgroundColor = getRandomColor(),
                    )
                }

            Column(modifier = Modifier.fillMaxWidth()) {
                LazyRow(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todayForecastList) { forecast ->
                        HourlyForecastItem(
                            hour = forecast.hour,
                            temperature = forecast.temperature,
                            icon = forecast.icon,
                            backgroundColor = forecast.backgroundColor
                        )
                    }
                }
                DaysForeCastContent(forecast)
            }
        }
    }
}
*/

    val currentDate = getCurrentDate().substring(0, 10)
    val todayForecastList = forecast.list
        .filter { it.dt_txt.startsWith(currentDate) }
        .map { item ->
            HourlyForecast(
                hour = convertToHour(item.dt_txt),
                temperature = "${item.main.temp.toInt()}",
                icon = getWeatherIcon(item.weather.firstOrNull()?.icon ?: ""),
                backgroundColor = getRandomColor(),
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todayForecastList) { forecast ->
                HourlyForecastItem(
                    hour = forecast.hour,
                    temperature = forecast.temperature,
                    icon = forecast.icon,
                    backgroundColor = forecast.backgroundColor
                )
            }
        }
        DaysForeCastContent(forecast)
    }
}

@Composable
fun HourlyForecastItem(
    hour: String,
    temperature: String,
    icon: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .width(70.dp)
            .height(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = hour, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "$temperature°",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        )
    }
}

fun getWeatherIcon(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.ic_air_quality_header
        "02d" -> R.drawable.ic_o3
        "03d", "03n" -> R.drawable.img_sun
        "04d", "04n" -> R.drawable.ic_control
        "09d", "09n" -> R.drawable.ic_o3
        "10d", "10n" -> R.drawable.ic_so2
        "11d", "11n" -> R.drawable.ic_frosty
        "13d", "13n" -> R.drawable.img_sun
        "50d", "50n" -> R.drawable.img_sub_rain
        else -> R.drawable.ic_wind
    }
}

