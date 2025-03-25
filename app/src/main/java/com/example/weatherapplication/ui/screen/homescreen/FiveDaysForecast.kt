package com.example.weatherapplication.ui.screen.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.ui.theme.primaryLight
import com.example.weatherapplication.utils.SharedPreference
import com.example.weatherapplication.utils.getDayNameFromDate
import com.example.weatherapplication.utils.getWeatherGradient
import com.example.weatherapplication.utils.getWeatherIcon

@Composable
fun DaysForeCastContent(forecast: Forecast) {
    val sharedPreferences = SharedPreference()
    val context = LocalContext.current
    var tempUnit by remember { mutableStateOf(sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C") }
    tempUnit = sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C"
    var unit= when (tempUnit) {
        "Celsius °C" -> "°C"
        "Kelvin °K" -> "°K"
        "Fahrenheit °F" -> "°F"
        else -> "metric"
    }

    val dates = forecast.list.map { it.dt_txt.substring(0, 10) }.distinct()
    val groupedForecast = forecast.list.groupBy { it.dt_txt.substring(0, 10) }
    val dailyAverages = groupedForecast.map { (date, entry) ->
        val maxTemperature = entry.maxOf { it.main.temp_max }
        val minTemperature = entry.minOf { it.main.temp_min }
        date to Pair(maxTemperature, minTemperature)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "5 Days Forecast",
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
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            dates.forEach { date ->
                val dayName = getDayNameFromDate(date)
                val (averageMaxTemperature, averageMinTemperature) =
                    dailyAverages.find { it.first == date }?.second ?: Pair(0.0, 0.0)
                DaysForecastItem(
                    day = dayName,
                    date = date,
                    minTemperature = "${averageMinTemperature.toInt()}${unit}",
                    maxTemperature = "${averageMaxTemperature.toInt()}${unit}",
                    iconCode = forecast.list.find { it.dt_txt.startsWith(date) }?.weather?.firstOrNull()?.icon ?: "",
                    description = forecast.list.find { it.dt_txt.startsWith(date) }?.weather?.firstOrNull()?.description ?:""
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun DaysForecastItem(
    day: String,
    date: String,
    minTemperature: String,
    maxTemperature: String,
    iconCode: String,
    description: String
) {
    val weatherIcon = getWeatherIcon(iconCode)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(weatherIcon))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(getWeatherGradient(description))
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                val (dayRef, dateRef, tempRowRef, iconRef) = createRefs()

                Text(
                    text = day,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.constrainAs(dayRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                )

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.constrainAs(dateRef) {
                        start.linkTo(dayRef.start)
                        top.linkTo(dayRef.bottom, margin = 4.dp)
                    }
                )

                LottieAnimation(
                    composition = composition,
                    progress = {progress},
                    modifier = Modifier
                        .constrainAs(iconRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(42.dp)
                        .height(42.dp)
                )

                Row(
                    modifier = Modifier.constrainAs(tempRowRef) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = maxTemperature,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = minTemperature,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}