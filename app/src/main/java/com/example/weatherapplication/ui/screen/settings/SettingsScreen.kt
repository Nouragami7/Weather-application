package com.example.weatherapplication.ui.screen.settings

import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.R
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.ui.theme.primaryContainerLight
import com.example.weatherapplication.ui.theme.primaryLight
import com.example.weatherapplication.utils.SharedPreference

@Composable
fun SettingsScreen() {
    val sharedPreference = SharedPreference()
    val context = LocalContext.current

    var selectedLanguage by remember {
        mutableStateOf(
            sharedPreference.getFromSharedPreference(
                context,
                "language"
            ) ?: "English"
        )
    }
    var selectedTempUnit by remember {
        mutableStateOf(
            sharedPreference.getFromSharedPreference(
                context,
                "tempUnit"
            ) ?: "Celsius °C"
        )
    }
    var selectedLocation by remember {
        mutableStateOf(
            sharedPreference.getFromSharedPreference(
                context,
                "location"
            ) ?: "GPS"
        )
    }
    var selectedWindSpeedUnit by remember {
        mutableStateOf(
            sharedPreference.getFromSharedPreference(
                context,
                "windSpeedUnit"
            ) ?: "meter/sec"
        )
    }

    LaunchedEffect(selectedWindSpeedUnit, selectedTempUnit) {
        if (selectedTempUnit == "Fahrenheit °F" && selectedWindSpeedUnit != "mile/hour") {
            selectedWindSpeedUnit = "mile/hour"
            sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit)
        } else if ((selectedTempUnit == "Celsius °C" || selectedTempUnit == "Kelvin °K") &&
            selectedWindSpeedUnit == "mile/hour") {
            selectedWindSpeedUnit = "meter/sec"
            sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit)
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsCard(
            title = "Language",
            options = listOf("Arabic", "English"),
            selectedOption = selectedLanguage,
            onSelectedOption = {
                selectedLanguage = it
                sharedPreference.saveToSharedPreference(context, "language", it)
            },
            iconRes = R.drawable.img_sun
        )

        SettingsCard(
            title = "Temp Unit",
            options = listOf("Celsius °C", "Kelvin °K", "Fahrenheit °F"),
            selectedOption = selectedTempUnit,
            onSelectedOption = {
                selectedTempUnit = it
                sharedPreference.saveToSharedPreference(context, "tempUnit", it)
            },
            iconRes = R.drawable.ic_rain_chance
        )

        SettingsCard(
            title = "Location",
            options = listOf("GPS", "Map"),
            selectedOption = selectedLocation,
            onSelectedOption = {
                selectedLocation = it
                sharedPreference.saveToSharedPreference(context, "location", it)
                if (it == "Map") {
                    NavigationManager.navigateTo(ScreensRoute.MapScreen(isFavourite = false))
                }
            },
            iconRes = R.drawable.ic_air_quality_header
        )

        SettingsCard(
            title = "Wind Speed Unit",
            options = listOf("meter/sec", "mile/hour"),
            selectedOption = selectedWindSpeedUnit,
            onSelectedOption = {
                selectedWindSpeedUnit = it
                sharedPreference.saveToSharedPreference(context, "windSpeedUnit", it)
            },
            iconRes = R.drawable.ic_wind
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelectedOption: (String) -> Unit,
    iconRes: Int
) {
    val enabledColor = primaryLight

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryContainerLight
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = primaryLight,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = enabledColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            options.forEach { text ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
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
                        colors = RadioButtonDefaults.colors(
                            selectedColor = enabledColor,
                            unselectedColor = enabledColor.copy(alpha = 0.6f)
                        )
                    )
                    Text(
                        text = text,
                        color = enabledColor,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
