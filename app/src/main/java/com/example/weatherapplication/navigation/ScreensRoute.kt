package com.example.weatherapplication.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class ScreensRoute(val route: String){
    @Serializable
    data object HomeScreen : ScreensRoute("home")
    @Serializable
    data object SearchScreen : ScreensRoute("search")
    @Serializable
    data object SettingsScreen : ScreensRoute("settings")
    @Serializable
    data object FavouriteScreen : ScreensRoute("favourite")

}