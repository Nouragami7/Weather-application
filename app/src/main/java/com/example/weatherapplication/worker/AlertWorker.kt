package com.example.weatherapplication.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapplication.datasource.local.WeatherDatabase
import com.example.weatherapplication.datasource.local.WeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.ApiService
import com.example.weatherapplication.datasource.remote.RetrofitHelper
import com.example.weatherapplication.datasource.remote.WeatherRemoteDataSource
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import showNotification

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {

        val repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource(
                RetrofitHelper.retrofitInstance.create(ApiService::class.java)
            ), WeatherLocalDataSource(WeatherDatabase.getDatabase(applicationContext).locationDao())
        )

        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = SharedPreference()
                val result = repository.getCurrentWeather(
                    sharedPreferences.getFromSharedPreference(applicationContext,"CurrentLatitude")!!.toDouble(),
                    sharedPreferences.getFromSharedPreference(applicationContext,"CurrentLongitude")!!.toDouble(),
                    sharedPreferences.getFromSharedPreference(applicationContext, "tempUnit")
                        ?: "Celsius °C",
                    sharedPreferences.getFromSharedPreference(applicationContext, "windSpeedUnit")
                        ?: "meter/sec",
                    Constants.API_KEY
                ).first()

                showNotification(applicationContext, result)
            }

        return Result.success()
    }
}

