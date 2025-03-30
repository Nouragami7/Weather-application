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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.DeepRed
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.Orange
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.Yellow
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.utils.convertUnixToTime
import com.example.weatherapplication.utils.formatNumberBasedOnLanguage
import com.example.weatherapplication.utils.getWeatherIcon

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
    currentTime: String = "",
    icon: String = "",
    sunrise: Long = 0L,
    sunset: Long = 0L

) {
    val weatherIcon = getWeatherIcon(icon)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(weatherIcon))
    val progress by animateLottieCompositionAsState(
        composition = composition, iterations = LottieConstants.IterateForever
    )


    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        val (forecastImage, forecastValue, sunCase, title, date, background, detailsCard) = createRefs()

        CardBackground(modifier = Modifier.constrainAs(background) {
            linkTo(
                start = parent.start,
                end = parent.end,
                top = parent.top,
                bottom = title.bottom,
                topMargin = 24.dp
            )
            height = Dimension.value(220.dp)
        })

        LottieAnimation(composition = composition,
            progress = { progress },
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(175.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start)
                    end.linkTo(forecastValue.start)
                    top.linkTo(parent.top)
                })

        Text(text = forecast,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom)
            })

        ForecastValue(
            modifier = Modifier.constrainAs(forecastValue) {
                end.linkTo(anchor = parent.end, margin = 24.dp)
                top.linkTo(forecastImage.top)
                bottom.linkTo(forecastImage.bottom)
            }, degree = currentTemperature, feelsLike = formatNumberBasedOnLanguage(feelsLike)
        )

        DateTimeDisplay(date = currentDate,
            time = currentTime,
            modifier = Modifier.constrainAs(date) {
                top.linkTo(forecastValue.bottom, margin = 16.dp)
                start.linkTo(forecastValue.start)
                end.linkTo(forecastValue.end)
            })

        SunCase(sunrise = sunrise, sunset = sunset, modifier = Modifier.constrainAs(sunCase) {
            top.linkTo(background.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        WeatherDetailsCard(pressure = pressure,
            clouds = clouds,
            windSpeed = windSpeed,
            humidity = humidity,
            modifier = Modifier.constrainAs(detailsCard) {
                top.linkTo(sunCase.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
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
                    0f to inversePrimaryDarkHighContrast, 1f to SkyBlue, 1f to LightBlue
                ), shape = RoundedCornerShape(32.dp)
            )
    )
}

@Composable
private fun ForecastValue(
    modifier: Modifier = Modifier, degree: String = "21", feelsLike: String = "feels like 26"
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree, letterSpacing = 0.sp, style = TextStyle(
                    brush = Brush.verticalGradient(
                        0f to Color.White, 1f to Color.White.copy(alpha = 0.3f)
                    ), fontSize = 40.sp, fontWeight = FontWeight.Black
                ), modifier = Modifier.padding(top = 16.dp)
            )
        }
        Text(
            text = "${stringResource(R.string.feels_like)} $feelsLike",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

    }


}

@Composable
fun SunCase(
    sunrise: Long = 0L, sunset: Long = 0L, modifier: Modifier = Modifier
) {
    val timeOfSunrise = convertUnixToTime(sunrise)
    val timeOfSunset = convertUnixToTime(sunset)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Yellow, Orange)
                    ), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                )
                .padding(8.dp), contentAlignment = Alignment.Center
        ) {
            SunInfoItem(
                iconRes = R.drawable.sunrise,
                label = stringResource(R.string.sunrise),
                time = timeOfSunrise
            )
        }
        SunPathDivider()
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Orange, DeepRed)
                    ), shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                )
                .padding(8.dp), contentAlignment = Alignment.Center
        ) {
            SunInfoItem(
                iconRes = R.drawable.sunset,
                label = stringResource(R.string.sunset),
                time = timeOfSunset
            )
        }
    }
}


@Composable
private fun SunInfoItem(iconRes: Int, label: String, time: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun SunPathDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(2.dp)
            .background(Color.White.copy(alpha = 0.5f))
    )
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
                    0f to LightBlue, 1f to SkyBlue
                ), shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem(R.drawable.pressure, stringResource(R.string.pressure), pressure)
            WeatherDetailItem(
                R.drawable.ic_air_quality_header, stringResource(R.string.clouds), clouds
            )
            WeatherDetailItem(R.drawable.ic_wind, stringResource(R.string.wind), windSpeed)
            WeatherDetailItem(R.drawable.humidity, stringResource(R.string.humidity), humidity)
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


@Composable
fun DateTimeDisplay(
    date: String, time: String, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date, style = TextStyle(
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = time, style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}






