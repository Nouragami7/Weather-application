package com.example.weatherapplication.navigation

import MapScreen
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapplication.ui.screen.AlertScreen
import com.example.weatherapplication.ui.screen.detailscreen.DetailsScreen
import com.example.weatherapplication.ui.screen.favourite.favouritescreen.FavouriteScreen
import com.example.weatherapplication.ui.screen.homescreen.HomeScreen
import com.example.weatherapplication.ui.screen.settings.SettingsScreen

@Composable
fun SetupNavHost(
    modifier: Modifier = Modifier,
    location: Location,
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
            SettingsScreen()
        }
        composable<ScreensRoute.FavouriteScreen> {
            isBottomNavigationVisible(true)
            FavouriteScreen(
                goToDetails = { latitude, longitude ->
                    NavigationManager.navigateTo(ScreensRoute.DetailsScreen(latitude, longitude))
                }
            )
        }
        composable<ScreensRoute.DetailsScreen> {
            isBottomNavigationVisible(false)
            val latitude = it.arguments?.getDouble("latitude") ?: 30.0444
            val longitude = it.arguments?.getDouble("longitude") ?: 31.2357
            DetailsScreen(latitude, longitude)
        }

        composable<ScreensRoute.MapScreen> {
            isBottomNavigationVisible(false)
            val isFavourite = it.toRoute<ScreensRoute.MapScreen>().isFavourite
            MapScreen(isFavourite,
               getLocation = { latitude, longitude ->
                   location.latitude = latitude
                   location.longitude = longitude

               })
        }
    }
}