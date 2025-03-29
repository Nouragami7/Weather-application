package com.example.weatherapplication

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.domain.model.City
import com.example.weatherapplication.domain.model.Clouds
import com.example.weatherapplication.domain.model.Coord
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.Forecast
import com.example.weatherapplication.domain.model.Item0
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.domain.model.Main
import com.example.weatherapplication.domain.model.Rain
import com.example.weatherapplication.domain.model.Sys
import com.example.weatherapplication.domain.model.Weather
import com.example.weatherapplication.domain.model.Wind
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.navigation.SetupNavHost
import com.example.weatherapplication.service.NotificationService
import com.example.weatherapplication.ui.screen.SplashScreen
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.LocationHelper
import com.example.weatherapplication.utils.PermissionUtils
import com.example.weatherapplication.utils.SharedPreference
import com.example.weatherapplication.utils.setLocale
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import createNotificationChannel

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    private val sharedPreference = SharedPreference()
    lateinit var locationState: MutableState<Location>
    lateinit var mapLocationState: MutableState<Location>
    lateinit var settingsLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        val forecast = Forecast(
            city = City(
                coord = Coord(lat = 30.0444, lon = 31.2357),
                country = "Egypt",
                id = 12345,
                name = "Cairo",
                population = 9500000,
                sunrise = 1680000000,
                sunset = 1680043200,
                timezone = 7200
            ),
            cnt = 5,
            cod = "200",
            list = listOf(
                Item0(
                    clouds = Clouds(all = 40),
                    dt = 1680000000,
                    dt_txt = "2024-03-29 12:00:00",
                    main = Main(
                        feels_like = 30.5,
                        grnd_level = 1000,
                        humidity = 60,
                        pressure = 1012,
                        sea_level = 1015,
                        temp = 28.3,
                        temp_kf = 0.5,
                        temp_max = 29.0,
                        temp_min = 27.5
                    ),
                    pop = 0.1,
                    rain = Rain(`3h` = 0.0),
                    sys = Sys(pod = "d"),
                    visibility = 10000,
                    weather = listOf(
                        Weather(
                            description = "clear sky",
                            icon = "01d",
                            id = 800,
                            main = "Clear"
                        )
                    ),
                    wind = Wind(deg = 180, gust = 3.5, speed = 5.0)
                )
            ),
            message = 0
        )



        val currentWeather = CurrentWeather(
            base = "stations",
            clouds = CurrentWeather.Clouds(all = 20),
            cod = 200,
            coord = CurrentWeather.Coord(lat = 30.0444, lon = 31.2357),
            dt = 1680000000,
            id = 98765,
            main = CurrentWeather.Main(
                feels_like = 32.0,
                grnd_level = 1000,
                humidity = 55,
                pressure = 1013,
                sea_level = 1015,
                temp = 30.0,
                temp_max = 31.5,
                temp_min = 28.0
            ),
            name = "Cairo",
            sys = CurrentWeather.Sys(
                country = "EG",
                id = 1,
                sunrise = 1680000000,
                sunset = 1680043200,
                type = 1
            ),
            timezone = 7200,
            visibility = 10000,
            weather = listOf(
                CurrentWeather.Weather(
                    description = "few clouds",
                    icon = "02d",
                    id = 801,
                    main = "Clouds"
                )
            ),
            wind = CurrentWeather.Wind(deg = 150, gust = 4.0, speed = 6.5)
        )





        //notification
        val locationData = LocationData(
            latitude = 30.0444,
            longitude = 31.2357,
            currentWeather = currentWeather,
            forecast = forecast,
            country = "Egypt",
            city = "Cairo"
        )

        val serviceIntent = Intent(this, NotificationService::class.java).apply {
            putExtra("location", Gson().toJson(locationData))
        }

        startService(serviceIntent)




        enableEdgeToEdge()
        hideSystemUI()
        setLocale(this, sharedPreference.getFromSharedPreference(this, "language") ?: "English")

        if (!Places.isInitialized()) {
            Places.initialize(this, Constants.API_KEY_Google)
        }

        settingsLocation = sharedPreference.getFromSharedPreference(this, "location") ?: "GPS"

        locationHelper = LocationHelper(this) { newLocation ->
            locationState.value = newLocation
        }

        setContent {

            val navController = rememberNavController()
            val deepLinkUri = intent?.data?.toString()
            LaunchedEffect(deepLinkUri) {
                deepLinkUri?.let { uri ->
                    if (uri.startsWith("\"C:\\Android projects\\AndroidKotlinProject\\WeatherApp\\app\\src\\main\\java\\com\\example\\weatherapplication\\ui\\screen\\detailscreen\\DetailsScreen.kt\"")) {
                        val jsonString = uri.substringAfter("favDetails/")
                        navController.navigate("favDetails/$jsonString")
                    }
                }
            }


            var displaySplashScreen by remember { mutableStateOf(true) }
            locationState = remember { mutableStateOf(Location("")) }
            mapLocationState = remember { mutableStateOf(Location("")) }

            mapLocationState.value.latitude = sharedPreference.getFromSharedPreference(this, "latitude")?.toDouble() ?: 0.0
            mapLocationState.value.longitude = sharedPreference.getFromSharedPreference(this, "longitude")?.toDouble() ?: 0.0

            val isBottomNavigationVisible = remember { mutableStateOf(true) }
            var selectedIndex = remember { mutableStateOf(0) }

            if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                    PermissionUtils.requestLocationPermissions(this)
                }
            } else {
                Scaffold(bottomBar = {
                    if (isBottomNavigationVisible.value) {
                        BottomNavigation(selectedIndex)
                    }
                }) {
                    SetupNavHost(
                        modifier = Modifier.padding(it),
                        if (settingsLocation == "GPS") locationState else mapLocationState,
                        isBottomNavigationVisible = { visible -> isBottomNavigationVisible.value = visible }
                    )
                }
            }
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onStart() {
        super.onStart()
        if (PermissionUtils.checkPermissions(this)) {
            if (PermissionUtils.isLocationEnabled(this)) {
                locationHelper.getLastKnownLocation()
            } else {
                PermissionUtils.enableLocationServices(this)
            }
        } else {
            PermissionUtils.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        PermissionUtils.handlePermissionsResult(requestCode, grantResults) {
            locationHelper.getLastKnownLocation()
    }
}
}

@Composable
private fun BottomNavigation(selectedIndex: MutableState<Int>) {
    val navigationBarItems = NavigationBarItems.values()
    AnimatedNavigationBar(
        modifier = Modifier.height(64.dp),
        selectedIndex = selectedIndex.value,
        cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
        ballAnimation = Parabolic(tween(300)),
        indentAnimation = Height(tween(300)),
        ballColor = inversePrimaryDarkHighContrast,
        barColor = LightSkyBlue
    ) {
        navigationBarItems.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        selectedIndex.value = index
                        NavigationManager.navigateTo(item.route)
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = if (selectedIndex.value == index) MaterialTheme.colorScheme.inversePrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

enum class NavigationBarItems(val icon: ImageVector, val route: ScreensRoute) {
    Home(
        icon = Icons.Default.Home,
        route = ScreensRoute.HomeScreen
    ),
    Favourite(
        icon = Icons.Default.Favorite,
        route = ScreensRoute.FavouriteScreen
    ),
    Alert(
        icon = Icons.Default.Notifications,
        route = ScreensRoute.SearchScreen
    ),
    Settings(icon = Icons.Default.Settings, route = ScreensRoute.SettingsScreen)
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}