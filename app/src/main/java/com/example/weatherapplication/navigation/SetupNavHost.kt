package com.example.weatherapplication.navigation

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.ui.screen.AlertScreen
import com.example.weatherapplication.ui.screen.favourite.FavouriteScreen
import com.example.weatherapplication.ui.screen.favourite.MapScreen
import com.example.weatherapplication.ui.screen.homescreen.HomeScreen
import com.example.weatherapplication.ui.screen.settings.SettingsScreen
import com.example.weatherapplication.viewmodel.SettingsViewModel

@Composable
fun SetupNavHost(
    modifier: Modifier = Modifier,
    location: Location,
    settingsViewModel: SettingsViewModel,
    isBottomNavigationVisible: (visibility:Boolean) -> Unit
) {
    val navController = rememberNavController()
    NavigationManager.navController = navController
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.HomeScreen
    ) {
        composable<ScreensRoute.HomeScreen> {
            isBottomNavigationVisible(true)
            HomeScreen(modifier, location)
        }
        composable<ScreensRoute.SearchScreen> {
            isBottomNavigationVisible(true)
            AlertScreen()
        }
        composable<ScreensRoute.SettingsScreen> {
            isBottomNavigationVisible(true)
            SettingsScreen(settingsViewModel)
        }
        composable<ScreensRoute.FavouriteScreen> {
            isBottomNavigationVisible(true)
            FavouriteScreen()
        }
        composable<ScreensRoute.MapScreen> {
            isBottomNavigationVisible(false)
            MapScreen()
        }
    }
}