package com.example.weatherapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.ui.screen.alert.isAlertExpired
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _alertData = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val alert = _alertData.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    private val periodicCheckJob = viewModelScope.launch {
        while (true) {
            delay(60 * 1000)
            fetchAlertData()
        }
    }

    init {
        viewModelScope.launch {
            periodicCheckJob.start()
        }
    }

    fun fetchAlertData() {
        viewModelScope.launch {
            try {
                val alertData = repository.getAllAlerts()
                alertData.collect { alerts ->
                    val validAlerts = alerts.filter { alert ->
                        !isAlertExpired(alert.startDate, alert.startTime)
                    }
                    alerts.forEach { alert ->
                        if (isAlertExpired(alert.startDate, alert.startTime)) {
                            repository.deleteAlert(alert)
                        }
                    }
                    _alertData.value = ResponseState.Success(validAlerts)
                    mutableMessage.emit("Alert data fetched successfully")
                }
            } catch (e: Exception) {
                _alertData.value = ResponseState.Failure(e)
                mutableMessage.emit("Error fetching alert data: ${e.message}")
            }
        }
    }


    fun deleteFromAlerts(alertData: AlertData) {
        viewModelScope.launch {
            try {
                repository.deleteAlert(alertData)
            } catch (e: Exception) {
                mutableMessage.emit("Error fetching alert data: ${e.message}")

            }
        }

    }

    fun insertAtAlerts(alertData: AlertData) {
        viewModelScope.launch {
            try {
                repository.insertAlert(alertData)
                mutableMessage.emit("Alert Added")
            } catch (e: Exception) {
                mutableMessage.emit("Error fetching alert data: ${e.message}")

            }
        }

    }

    class AlertFactory(
        private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}