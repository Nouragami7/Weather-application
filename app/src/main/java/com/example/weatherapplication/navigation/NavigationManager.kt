package com.example.weatherapplication.navigation
import androidx.navigation.NavHostController

object NavigationManager {
    lateinit var navController: NavHostController

    fun navigateTo(route: String) {
        navController.navigate(route)
    }
}