package com.example.weatherapplication.ui.screen.settings.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.utils.LocationHelper
import com.example.weatherapplication.utils.PermissionUtils
import com.example.weatherapplication.utils.PreferenceConstants
import com.example.weatherapplication.utils.SharedPreference
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val sharedPreference = SharedPreference()
    var selectedLanguage = mutableStateOf(
        sharedPreference.getFromSharedPreference(context, "language")
            ?: PreferenceConstants.LANGUAGE_ENGLISH
    )

    var selectedTempUnit = mutableStateOf(
        sharedPreference.getFromSharedPreference(context, "tempUnit")
            ?: PreferenceConstants.TEMP_UNIT_CELSIUS
    )

    var selectedLocation = mutableStateOf(
        sharedPreference.getFromSharedPreference(context, "location")
            ?: PreferenceConstants.LOCATION_GPS
    )

    var selectedWindSpeedUnit = mutableStateOf(
        sharedPreference.getFromSharedPreference(context, "windSpeedUnit")
            ?: PreferenceConstants.WIND_SPEED_METER_SEC
    )

    fun updateLanguage(newLanguage: String) {
        selectedLanguage.value = newLanguage
        sharedPreference.saveToSharedPreference(context, "language", newLanguage)

        restartApp()
    }

    fun updateTempUnit(newTempUnit: String) {
        selectedTempUnit.value = newTempUnit
        sharedPreference.saveToSharedPreference(context, "tempUnit", newTempUnit)
        adjustWindSpeedUnit()
    }

    fun updateLocation(newLocation: String, location: MutableState<Location>) {
        selectedLocation.value = newLocation
        sharedPreference.saveToSharedPreference(context, "location", newLocation)

        when (newLocation) {
            PreferenceConstants.LOCATION_MAP -> {
                NavigationManager.navigateTo(ScreensRoute.MapScreen(isFavourite = false))
            }

            PreferenceConstants.LOCATION_GPS -> {
                sharedPreference.deleteSharedPreference(context, "latitude")
                sharedPreference.deleteSharedPreference(context, "longitude")

                if (!PermissionUtils.isLocationEnabled(context)) {
                    PermissionUtils.enableLocationServices(context as Activity)
                } else {
                    LocationHelper(context) { newLoc ->
                        location.value = newLoc
                    }.getLastKnownLocation()
                }
            }
        }
    }

    fun updateWindSpeedUnit(newWindSpeedUnit: String) {
        selectedWindSpeedUnit.value = newWindSpeedUnit
        sharedPreference.saveToSharedPreference(context, "windSpeedUnit", newWindSpeedUnit)
    }

    private fun adjustWindSpeedUnit() {
        viewModelScope.launch {
            if (selectedTempUnit.value == PreferenceConstants.TEMP_UNIT_FAHRENHEIT && selectedWindSpeedUnit.value != PreferenceConstants.WIND_SPEED_MILE_HOUR) {
                selectedWindSpeedUnit.value = PreferenceConstants.WIND_SPEED_MILE_HOUR
                sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit.value)
            } else if ((selectedTempUnit.value == PreferenceConstants.TEMP_UNIT_CELSIUS || selectedTempUnit.value == PreferenceConstants.TEMP_UNIT_KELVIN) && selectedWindSpeedUnit.value == PreferenceConstants.WIND_SPEED_MILE_HOUR) {
                selectedWindSpeedUnit.value = PreferenceConstants.WIND_SPEED_METER_SEC
                sharedPreference.saveToSharedPreference(context, "windSpeedUnit", selectedWindSpeedUnit.value)
            }
        }
    }

    private fun restartApp() {
        val activity = context as Activity
        activity.finish()
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

    class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SettingsViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }


    }
}


