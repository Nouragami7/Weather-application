package com.example.weatherapplication.ui.screen.settings.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.screen.settings.viewmodel.SettingsViewModel
import com.example.weatherapplication.ui.theme.IceBlue
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SoftSkyBlue
import com.example.weatherapplication.utils.PreferenceConstants

@Composable
fun SettingsScreen( location: MutableState<Location>) {

   val factory = SettingsViewModel.SettingsViewModelFactory(LocalContext.current)
    val viewModel: SettingsViewModel = viewModel(factory = factory)

    val languageOptions = mapOf(
        PreferenceConstants.LANGUAGE_ENGLISH to stringResource(R.string.english),
        PreferenceConstants.LANGUAGE_ARABIC to stringResource(R.string.arabic)
    )

    val tempUnitOptions = mapOf(
        PreferenceConstants.TEMP_UNIT_CELSIUS to stringResource(R.string.celsius_c),
        PreferenceConstants.TEMP_UNIT_KELVIN to stringResource(R.string.kelvin_k),
        PreferenceConstants.TEMP_UNIT_FAHRENHEIT to stringResource(R.string.fahrenheit_f)
    )

    val locationOptions = mapOf(
        PreferenceConstants.LOCATION_GPS to stringResource(R.string.gps),
        PreferenceConstants.LOCATION_MAP to stringResource(R.string.map)
    )

    val windSpeedOptions = mapOf(
        PreferenceConstants.WIND_SPEED_METER_SEC to stringResource(R.string.meter_sec),
        PreferenceConstants.WIND_SPEED_MILE_HOUR to stringResource(R.string.mile_hour)
    )

    Column(modifier = Modifier.fillMaxSize().background(IceBlue).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
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

        SettingsCard(
            title = stringResource(R.string.language),
            options = languageOptions.values.toList(),
            selectedOption = languageOptions[viewModel.selectedLanguage.value] ?: "",
            onSelectedOption = { displayedOption ->
                val newLanguage = languageOptions.entries.find { it.value == displayedOption }?.key
                    ?: PreferenceConstants.LANGUAGE_ENGLISH
                viewModel.updateLanguage(newLanguage)
            },
            iconRes = R.drawable.language
        )

        SettingsCard(
            title = stringResource(R.string.temp_unit),
            options = tempUnitOptions.values.toList(),
            selectedOption = tempUnitOptions[viewModel.selectedTempUnit.value] ?: "",
            onSelectedOption = { displayedOption ->
                val newTempUnit = tempUnitOptions.entries.find { it.value == displayedOption }?.key
                    ?: PreferenceConstants.TEMP_UNIT_CELSIUS
                viewModel.updateTempUnit(newTempUnit)
            },
            iconRes = R.drawable.thermometer
        )

        SettingsCard(
            title = stringResource(R.string.location),
            options = locationOptions.values.toList(),
            selectedOption = locationOptions[viewModel.selectedLocation.value] ?: "",
            onSelectedOption = { displayedOption ->
                val newLocation = locationOptions.entries.find { it.value == displayedOption }?.key
                    ?: PreferenceConstants.LOCATION_GPS
                viewModel.updateLocation(newLocation, location)
            },
            iconRes = R.drawable.location
        )

        SettingsCard(
            title = stringResource(R.string.wind_speed_unit),
            options = windSpeedOptions.values.toList(),
            selectedOption = windSpeedOptions[viewModel.selectedWindSpeedUnit.value] ?: "",
            onSelectedOption = { displayedOption ->
                val newWindSpeedUnit = windSpeedOptions.entries.find { it.value == displayedOption }?.key
                    ?: PreferenceConstants.WIND_SPEED_METER_SEC
                viewModel.updateWindSpeedUnit(newWindSpeedUnit)
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
                options.forEach { displayText ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (displayText == selectedOption),
                                onClick = { onSelectedOption(displayText) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (displayText == selectedOption),
                            onClick = { onSelectedOption(displayText) },
                            colors = RadioButtonDefaults.colors(selectedColor = SoftSkyBlue)
                        )
                        Text(displayText, color = SoftSkyBlue,
                            modifier = Modifier.padding(start = 1.dp),
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

