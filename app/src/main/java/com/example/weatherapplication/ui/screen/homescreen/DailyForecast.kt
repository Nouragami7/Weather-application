package com.example.weatherapplication.ui.screen.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.ui.theme.primaryDarkMediumContrast

@Composable
fun DailyWeatherCard(
    modifier: Modifier = Modifier,
    forecast: String = "Rain showers",
    feelsLike: String = "Feels like 26",
    pressure: String = "1013 hPa",
    clouds: String = "75%",
    windSpeed: String = "12 km/h",
    humidity: String = "60%",
    currentTemperature: String = "21°C",
    currentDate: String = " ",
    currentTime: String = ""
) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        val (forecastImage, forecastValue, windImage, title, description, time, background, detailsCard) = createRefs()

        CardBackground(
            modifier = Modifier.constrainAs(background) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = description.bottom,
                    topMargin = 24.dp
                )
                height = Dimension.value(250.dp)
            }
        )

        Image(
            painter = painterResource(id = R.drawable.img_sub_rain),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(175.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start, margin = 4.dp)
                    top.linkTo(parent.top)
                }
        )



        Text(
            text = forecast,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom)
            }
        )
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.constrainAs(description) {
                top.linkTo(anchor = title.bottom)
                start.linkTo(title.start)
            }
        )
        Text(
            text = currentTime,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.constrainAs(time) {
                top.linkTo(description.bottom)
                start.linkTo(title.start)
            }
        )
        ForecastValue(
            modifier = Modifier.constrainAs(forecastValue) {
                end.linkTo(anchor = parent.end, margin = 24.dp)
                top.linkTo(forecastImage.top)
                bottom.linkTo(forecastImage.bottom)
            },
            degree = currentTemperature,
            feelsLike = feelsLike
        )

        WindForecastImage(
            modifier = Modifier.constrainAs(windImage) {
                linkTo(
                    top = title.top,
                    bottom = title.bottom
                )
                end.linkTo(anchor = parent.end, margin = 24.dp)
            }
        )
        WeatherDetailsCard(
            pressure = pressure,
            clouds = clouds,
            windSpeed = windSpeed,
            humidity = humidity,
            modifier = Modifier.constrainAs(detailsCard) {
                top.linkTo(background.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}


@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    0f to inversePrimaryDarkHighContrast,
                    1f to SkyBlue,
                    1f to LightBlue
                ),
                shape = RoundedCornerShape(32.dp)
            )
    )
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier,
    degree: String = "21",
    feelsLike: String = "feels like 26"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree,
                letterSpacing = 0.sp,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Text(
            text = ("Feels like $feelsLike"),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }


}

@Composable
private fun WindForecastImage(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_frosty),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = primaryDarkMediumContrast
        )
        Icon(
            painter = painterResource(R.drawable.ic_wind),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = primaryDarkMediumContrast
        )
    }
}

@Composable
fun WeatherDetailsCard(
    modifier: Modifier = Modifier,
    pressure: String,
    clouds: String,
    windSpeed: String,
    humidity: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    0f to LightBlue,
                    1f to SkyBlue
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem(R.drawable.pressure, "Pressure", pressure)
            WeatherDetailItem(R.drawable.ic_air_quality_header, "Clouds", clouds)
            WeatherDetailItem(R.drawable.ic_wind, "Wind", windSpeed)
            WeatherDetailItem(R.drawable.humidity, "Humidity", humidity)
        }
    }
}

@Composable
fun WeatherDetailItem(iconRes: Int, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}






