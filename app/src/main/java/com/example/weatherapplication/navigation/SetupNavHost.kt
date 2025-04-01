package com.example.weatherapplication.navigation

import MapScreen
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.ui.screen.notification.AlertScreen
import com.example.weatherapplication.ui.screen.detailscreen.DetailsScreen
import com.example.weatherapplication.ui.screen.favourite.favouritescreen.FavouriteScreen
import com.example.weatherapplication.ui.screen.homescreen.HomeScreen
import com.example.weatherapplication.ui.screen.settings.SettingsScreen
import com.google.gson.Gson

@Composable
fun SetupNavHost(
    modifier: Modifier = Modifier,
    location: MutableState<Location>,
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
            SettingsScreen(location)
        }
        composable<ScreensRoute.FavouriteScreen> {
            isBottomNavigationVisible(true)
            FavouriteScreen(
                goToDetails = { LocationData ->
                    val gson = Gson()
                    var location = gson.toJson(LocationData)
                    NavigationManager.navigateTo(ScreensRoute.DetailsScreen(location))
                }
            )
        }

        composable<ScreensRoute.DetailsScreen> { backStackEntry ->
           val jsonString = backStackEntry.toRoute<ScreensRoute.DetailsScreen>().location
            val location = try {
                Gson().fromJson(jsonString, LocationData::class.java)
            } catch (e: Exception) {
                Log.e("TAG", "Error parsing JSON: $e")
                null
            }
            if (location != null) {
                DetailsScreen(location)

            } else {
                Log.e("TAG", "Location data is null")
            }
        }


        composable<ScreensRoute.MapScreen> {
            isBottomNavigationVisible(false)
            val isFavourite = it.toRoute<ScreensRoute.MapScreen>().isFavourite
            MapScreen(isFavourite,
               getLocation = { latitude, longitude ->
                   location.value.latitude = latitude
                   location.value.longitude = longitude

               })
        }
    }
}