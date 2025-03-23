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
}