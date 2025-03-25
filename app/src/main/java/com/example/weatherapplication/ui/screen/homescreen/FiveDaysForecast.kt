package com.example.weatherapplication.ui.screen.homescreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.ui.theme.primaryLight
import com.example.weatherapplication.utils.getDayNameFromDate

@Composable
fun DaysForeCastContent(forecast: Forecast) {
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
                    minTemperature = "${averageMinTemperature.toInt()}°C",
                    maxTemperature = "${averageMaxTemperature.toInt()}°C"
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
) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        0f to SkyBlue,
                        1f to LightBlue
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)

        ) {
            val (dayRef, dateRef, tempRowRef) = createRefs()

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
                    bottom.linkTo(parent.bottom)
                }
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
                /*TemperatureInfo(
                    label = "Max",
                    temperature = maxTemperature,
                    icon = Icons.Filled.KeyboardArrowUp,
                    backgroundColor = Color(0xFFFFA726)
                )
                TemperatureInfo(
                    label = "Min",
                    temperature = minTemperature,
                    icon = Icons.Filled.KeyboardArrowDown,
                    backgroundColor = primaryLight
                )*/

                Text(
                    text = maxTemperature,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = minTemperature,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }


/*
@Composable
fun TemperatureInfo(
    label: String,
    temperature: String,
    icon: ImageVector,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = temperature,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}
*/
