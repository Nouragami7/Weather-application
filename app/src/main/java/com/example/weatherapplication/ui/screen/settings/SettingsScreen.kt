package com.example.weatherapplication.ui.screen.settings

import android.app.Activity
import android.location.Location
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.R
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.ui.theme.IceBlue
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SoftSkyBlue
import com.example.weatherapplication.utils.LocationHelper
import com.example.weatherapplication.utils.PermissionUtils
import com.example.weatherapplication.utils.SharedPreference

@Composable
fun SettingsScreen(location: MutableState<Location>) {
    val sharedPreference = SharedPreference()
    val context = LocalContext.current

    var selectedLanguage by remember { mutableStateOf(sharedPreference.getFromSharedPreference(context, "language") ?: "English") }
    var selectedTempUnit by remember { mutableStateOf(sharedPreference.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C") }
    var selectedLocation by remember { mutableStateOf(sharedPreference.getFromSharedPreference(context, "location") ?: "GPS") }
    var selectedWindSpeedUnit by remember { mutableStateOf(sharedPreference.getFromSharedPreference(context, "windSpeedUnit") ?: "meter/sec") }

    LaunchedEffect(selectedTempUnit, selectedWindSpeedUnit) {
        if (selectedTempUnit == "Fahrenheit °F" && selectedWindSpeedUnit != "mile/hour") {
            selectedWindSpeedUnit = "mile/hour"
            sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit)
        } else if ((selectedTempUnit == "Celsius °C" || selectedTempUnit == "Kelvin °K") && selectedWindSpeedUnit == "mile/hour") {
            selectedWindSpeedUnit = "meter/sec"
            sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(IceBlue)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.8f)
                    quadraticBezierTo(size.width * 0.5f, size.height * 1.2f, size.width, size.height * 0.8f)
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                clipPath(path) {
                    drawRoundRect(LightBlue, size = size, cornerRadius = CornerRadius(40f, 40f))
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.settings), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(stringResource(R.string.language), listOf(stringResource(R.string.arabic), stringResource(R.string.english)), selectedLanguage, {
            selectedLanguage = it
            sharedPreference.saveToSharedPreference(context, "language", it)
        }, R.drawable.language)

        SettingsCard(stringResource(R.string.temp_unit), listOf(stringResource(R.string.celsius_c), stringResource(R.string.kelvin_k), stringResource(R.string.fahrenheit_f)), selectedTempUnit, {
            selectedTempUnit = it
            sharedPreference.saveToSharedPreference(context, "tempUnit", it)
        }, R.drawable.thermometer)

        SettingsCard(stringResource(R.string.location), listOf(stringResource(R.string.gps), stringResource(R.string.map)), selectedLocation, {
            selectedLocation = it
            sharedPreference.saveToSharedPreference(context, "location", it)
            if (it == "Map") {
                NavigationManager.navigateTo(ScreensRoute.MapScreen(isFavourite = false))
            } else {
                sharedPreference.deleteSharedPreference(context, "latitude")
                sharedPreference.deleteSharedPreference(context, "longitude")
                if (!PermissionUtils.isLocationEnabled(context)) {
                    PermissionUtils.enableLocationServices(context as Activity)
                } else {
                    LocationHelper(context) { newLocation ->
                        location.value = newLocation
                    }.getLastKnownLocation()
                }
            }
        }, R.drawable.location)

        SettingsCard(stringResource(R.string.wind_speed_unit), listOf(stringResource(R.string.meter_sec), stringResource(R.string.mile_hour)), selectedWindSpeedUnit, {
            selectedWindSpeedUnit = it
            sharedPreference.saveToSharedPreference(context, "windSpeedUnit", it)
        }, R.drawable.ic_wind)
    }
}

@Composable
fun SettingsCard(title: String, options: List<String>, selectedOption: String, onSelectedOption: (String) -> Unit, iconRes: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = SoftSkyBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SoftSkyBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                options.forEach { text ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onSelectedOption(text) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { onSelectedOption(text) },
                            colors = RadioButtonDefaults.colors(selectedColor = SoftSkyBlue)
                        )
                        Text(text, color = SoftSkyBlue, modifier = Modifier.padding(start = 1.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}