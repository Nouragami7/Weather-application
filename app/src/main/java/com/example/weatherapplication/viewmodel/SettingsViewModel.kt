package com.example.weatherapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val mutableLanguage = MutableStateFlow("English")
    val language: StateFlow<String> get() = mutableLanguage

    private val mutableTempUnit = MutableStateFlow("Celsius °C")
    val tempUnit: StateFlow<String> get() = mutableTempUnit

    private val mutableLocation = MutableStateFlow("GPS")
    val location: StateFlow<String> get() = mutableLocation

    private val mutableWindSpeedUnit = MutableStateFlow("meter/sec")
    val windSpeedUnit: StateFlow<String> get() = mutableWindSpeedUnit


    fun updateLanguage(language: String) {
        viewModelScope.launch {
            mutableLanguage.emit(language)
        }
    }

    fun updateTempUnit(tempUnit: String) {
        viewModelScope.launch {
            mutableTempUnit.emit(tempUnit)
        }
    }

    fun updateLocation(location: String){
        viewModelScope.launch {
            mutableLocation.emit(location)
        }
    }

    fun updateWindSpeedUnit(windSpeedUnit: String){
        viewModelScope.launch {
            mutableWindSpeedUnit.emit(windSpeedUnit)
        }
    }

}