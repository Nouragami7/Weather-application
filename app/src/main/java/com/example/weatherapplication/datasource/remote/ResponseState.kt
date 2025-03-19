package com.example.weatherapplication.datasource.remote


sealed class ResponseState {
    data object Loading : ResponseState()
    data class Success<T>(val data: T) : ResponseState()
    data class Failure(val message: Throwable) : ResponseState()
}