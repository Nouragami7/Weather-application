package com.example.weatherapplication.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.primaryLight

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsCard(
            title = "Language",
            options = listOf("Arabic", "English", "Default"),
            iconRes = R.drawable.img_sun
        )

        SettingsCard(
            title = "Temp Unit",
            options = listOf("Celsius °C", "Kelvin °K", "Fahrenheit °F"),
            iconRes = R.drawable.ic_rain_chance
        )

        SettingsCard(
            title = "Location",
            options = listOf("GPS", "Map"),
            iconRes = R.drawable.ic_air_quality_header
        )

        SettingsCard(
            title = "Wind Speed Unit",
            options = listOf("meter/sec", "mile/hour"),
            iconRes = R.drawable.ic_wind
        )
    }
}

@Composable
fun SettingsCard(title: String, options: List<String>, iconRes: Int) {
    var selectedOption by remember { mutableStateOf(options[0]) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
                Text(text = title, fontSize = 20.sp, color = primaryLight,fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            options.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { selectedOption = text },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = { selectedOption = text },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryLight)
                    )
                    Text(text = text, color = primaryLight, modifier = Modifier.padding(start = 8.dp), fontSize = 16.sp)
                }
            }
        }
    }
}
