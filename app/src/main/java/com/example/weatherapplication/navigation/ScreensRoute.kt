package com.example.weatherapplication.navigation
import kotlinx.serialization.Serializable


@Serializable
sealed class ScreensRoute(){
    @Serializable
    data object HomeScreen: ScreensRoute()
    @Serializable
    data object SearchScreen : ScreensRoute()
    @Serializable
    data object SettingsScreen : ScreensRoute()
    @Serializable
    data object FavouriteScreen : ScreensRoute()
    @Serializable
    data class MapScreen(val isFavourite: Boolean) : ScreensRoute()
    @Serializable
    data class DetailsScreen(val location: String) : ScreensRoute()

}