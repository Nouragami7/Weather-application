package com.example.weatherapplication.navigation

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.ui.screen.FavouriteScreen
import com.example.weatherapplication.ui.screen.homescreen.HomeScreen
import com.example.weatherapplication.ui.screen.SearchScreen
import com.example.weatherapplication.ui.screen.SettingsScreen

@Composable
fun SetupNavHost(
    modifier: Modifier = Modifier,
    location: Location
) {
    val navController = rememberNavController()
    NavigationManager.navController = navController
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.HomeScreen.route
    ) {
        composable(ScreensRoute.HomeScreen.route) {
           HomeScreen(modifier, location)
        }
        composable(ScreensRoute.SearchScreen.route) {
            SearchScreen()
        }
        composable(ScreensRoute.SettingsScreen.route) {
            SettingsScreen()
        }
        composable(ScreensRoute.FavouriteScreen.route) {
            FavouriteScreen()
        }
    }
}