package com.example.weatherapplication.utils

import android.content.Context
import android.util.Log
import com.example.weatherapplication.utils.Constants.Companion.PREF_NAME

class SharedPreference {
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


}


