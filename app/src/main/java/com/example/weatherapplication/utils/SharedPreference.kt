package com.example.weatherapplication.utils

import android.content.Context
import android.util.Log

class SharedPreference {
     private  val PREF_NAME = "myPref"
    fun saveToSharedPreference(context: Context, key: String, value: String) {
        Log.d("TAG", "saveToSharedPreference: $value")
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putString(key, value)
            apply()
        }
    }


    fun getFromSharedPreference(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    fun deleteSharedPreference(context: Context, key: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            remove(key)
            apply()
        }
    }

    fun getTempUnite(context: Context):String{
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val value = sharedPref.getString("tempUnit", "Celsius °C")
         val tempUnite = when (value) {
             "Celsius °C" -> "°C"
             "Kelvin °K" -> "°K"
             "Fahrenheit °F" -> "°F"
             else -> "metric"
        }
        return tempUnite
    }

    fun getSavedLocation(context: Context): android.location.Location {
        val latitude = getFromSharedPreference(context, "latitude")?.toDouble() ?: 0.0
        val longitude = getFromSharedPreference(context, "longitude")?.toDouble() ?: 0.0
        return android.location.Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
    }

}


