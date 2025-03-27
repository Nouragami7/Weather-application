package com.example.weatherapplication

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.navigation.SetupNavHost
import com.example.weatherapplication.ui.screen.SplashScreen
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.utils.Constants.Companion.API_KEY_Google
import com.example.weatherapplication.utils.Constants.Companion.REQUEST_LOCATION_CODE
import com.example.weatherapplication.utils.PermissionUtils
import com.example.weatherapplication.utils.SharedPreference
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationState: MutableState<Location>
    lateinit var mapLocationState: MutableState<Location>
    lateinit var settingsLocation: String
    val sharedPreference = SharedPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI()
        if (!Places.isInitialized()) {
            Places.initialize(this, API_KEY_Google)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsLocation = sharedPreference.getFromSharedPreference(this, "location") ?: "GPS"

        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            locationState = remember { mutableStateOf(Location("")) }
            mapLocationState = remember { mutableStateOf(Location("")) }

            mapLocationState.value.latitude = sharedPreference.getFromSharedPreference(this, "latitude")?.toDouble() ?: 0.0
            mapLocationState.value.longitude = sharedPreference.getFromSharedPreference(this, "longitude")?.toDouble() ?: 0.0
            Log.d(TAG, "onCreate: ${mapLocationState.value.latitude} ${mapLocationState.value.longitude}")

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
                    SetupNavHost(modifier = Modifier.padding(it),
                        if (settingsLocation == "GPS") {
                            locationState.value
                        } else {
                            mapLocationState.value
                        },
                        isBottomNavigationVisible = { visible ->
                            isBottomNavigationVisible.value = visible
                        })
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
                getFreshLocation()
            } else {
                PermissionUtils.enableLocationServices(this)
            }
        } else {
            PermissionUtils.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, // code that we passed when requesting the permissions (specialize the request)
        permissions: Array<out String>, // array of permissions that we requested (fine location, coarse location)
        grantResults: IntArray, // array of results (granted or not granted)
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationState.value = location
            } else {
                requestNewLocationData()
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get location: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).setMinUpdateDistanceMeters(10f).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.i(
                            TAG, "New Location: Lat=${location.latitude}, Lon=${location.longitude}"
                        )
                        locationState.value = location
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }, mainLooper
        )
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