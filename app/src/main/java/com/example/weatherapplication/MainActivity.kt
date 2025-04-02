package com.example.weatherapplication

import ConnectivityObserver
import OfflineBar
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.navigation.SetupNavHost
import com.example.weatherapplication.ui.screen.SplashScreen
import com.example.weatherapplication.ui.screen.settings.viewmodel.SettingsViewModel
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.LocationHelper
import com.example.weatherapplication.utils.PermissionUtils
import com.example.weatherapplication.utils.SharedPreference
import com.example.weatherapplication.utils.checkForInternet
import com.example.weatherapplication.utils.setLocale
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.google.android.libraries.places.api.Places
import createNotificationChannel
import java.util.Locale

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

        enableEdgeToEdge()
        hideSystemUI()
        setLocale(this, sharedPreference.getFromSharedPreference(this, "language") ?: "English")

        if (!Places.isInitialized()) {
            Places.initialize(this, Constants.API_KEY_Google)
        }

        settingsLocation = sharedPreference.getFromSharedPreference(this, "location") ?: "GPS"

        locationHelper = LocationHelper(this) { newLocation ->
            locationState.value = newLocation
            sharedPreference.saveToSharedPreference(
                this, "CurrentLatitude", newLocation.latitude.toString()
            )
            sharedPreference.saveToSharedPreference(
                this, "CurrentLongitude", newLocation.longitude.toString()
            )
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

            mapLocationState.value.latitude =
                sharedPreference.getFromSharedPreference(this, "latitude")?.toDouble() ?: 0.0
            mapLocationState.value.longitude =
                sharedPreference.getFromSharedPreference(this, "longitude")?.toDouble() ?: 0.0

            val isBottomNavigationVisible = remember { mutableStateOf(true) }
            val selectedIndex = remember { mutableIntStateOf(0) }

            val context = LocalContext.current


            val connectivityObserver = remember { ConnectivityObserver(context) }
            val isConnected by connectivityObserver.isConnected.collectAsStateWithLifecycle(
                initialValue = checkForInternet(
                    context
                )
            )

            if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                    PermissionUtils.requestLocationPermissions(this)
                }
            } else {
                Scaffold(topBar = {
                    if (!isConnected) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .padding(top = 34.dp)
                        ) {
                            OfflineBar()
                        }
                    }
                },

                    bottomBar = {
                        if (isBottomNavigationVisible.value) {
                            BottomNavigation(selectedIndex)
                        }
                    }) {
                    Box(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        SetupNavHost(modifier = Modifier.padding(it),
                            if (settingsLocation == "GPS") locationState else mapLocationState,
                            isBottomNavigationVisible = { visible ->
                                isBottomNavigationVisible.value = visible
                            })
                    }
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
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
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

    override fun onResume() {
        super.onResume()

        val sharedPreference = SharedPreference()
        val savedLanguage = sharedPreference.getFromSharedPreference(this, "language")
        val systemLanguage = Locale.getDefault().language
        if (savedLanguage.isNullOrEmpty()) {
            if (systemLanguage == "ar") {
                sharedPreference.saveToSharedPreference(this, "language", "ar")
            } else {
                sharedPreference.saveToSharedPreference(this, "language", "en")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray, deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        PermissionUtils.handlePermissionsResult(requestCode, grantResults) {
            locationHelper.getLastKnownLocation()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val sharedPreference = SharedPreference()
        val savedLanguage = sharedPreference.getFromSharedPreference(this, "language")

        if (savedLanguage == "system") {
            val systemLanguage = newConfig.locales[0].language
            SettingsViewModel(this).applyLanguage(systemLanguage)
        }
    }
}

@Composable
private fun BottomNavigation(selectedIndex: MutableState<Int>) {
    val navigationBarItems = NavigationBarItems.entries.toTypedArray()
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
        icon = Icons.Default.Home, route = ScreensRoute.HomeScreen
    ),
    Favourite(
        icon = Icons.Default.Favorite, route = ScreensRoute.FavouriteScreen
    ),
    Alert(
        icon = Icons.Default.Notifications, route = ScreensRoute.SearchScreen
    ),
    Settings(icon = Icons.Default.Settings, route = ScreensRoute.SettingsScreen)
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}