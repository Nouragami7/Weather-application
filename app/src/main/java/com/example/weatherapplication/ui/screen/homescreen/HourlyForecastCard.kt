package com.example.weatherapplication.ui.screen.homescreen

import LottieAnimationView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.utils.convertToHour
import com.example.weatherapplication.utils.getWeatherGradient
import com.example.weatherapplication.utils.getWeatherIcon

data class HourlyForecast(
    val hour: String,
    val temperature: String,
    val icon: Int,
    val backgroundColor: Brush
)
@Composable
fun HourlyForecastCard(
    forecast: Forecast,
    modifier: Modifier = Modifier
) {

    val todayForecastList = forecast.list
        .take(8)
        .map { item ->
            val weatherDescription = item.weather.firstOrNull()?.description ?: ""
            val weatherCode = item.weather.firstOrNull()?.icon ?: ""
            val backgroundColor = if (weatherCode.contains("01d", ignoreCase = true)) {
                Brush.verticalGradient(   listOf(
                    Color(0xFFFFC107),
                    Color(0xFFFFA000),
                    Color(0xFFEE972A)
                ))
            } else {
                getWeatherGradient(weatherDescription)
            }

            HourlyForecast(
                hour = convertToHour(item.dt_txt),
                temperature = "${item.main.temp.toInt()}",
                icon = getWeatherIcon(item.weather.firstOrNull()?.icon ?: ""),
                backgroundColor = backgroundColor,
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .height(170.dp)
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
    backgroundColor: Brush,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(icon))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Loop the animation indefinitely
    )
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
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.height(36.dp).width(36.dp)
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


