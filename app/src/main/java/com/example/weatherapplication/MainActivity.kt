package com.example.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Place
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
import androidx.core.app.ActivityCompat
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.navigation.SetupNavHost
import com.example.weatherapplication.ui.screen.SplashScreen
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.inversePrimaryDarkHighContrast
import com.example.weatherapplication.utils.Constants.Companion.REQUEST_LOCATION_CODE
import com.example.weatherapplication.viewmodel.SettingsViewModel
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            locationState = remember { mutableStateOf(Location("")) }
            val isBottomNavigationVisible = remember { mutableStateOf(true) }
            if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                    requestLocationPermissions()
                }
            } else {
                Scaffold(
                    Modifier.padding(bottom = 54.dp),
                    bottomBar = {
                        if (isBottomNavigationVisible.value){
                            BottomNavigation()
                        }
                    }
                ) {
                    SetupNavHost(
                        modifier = Modifier.padding(it),
                        locationState.value,
                        settingsViewModel = SettingsViewModel(),
                        isBottomNavigationVisible={
                            visible ->
                            isBottomNavigationVisible.value = visible}
                    )
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {
            requestLocationPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationServices() {
        Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
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
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.i(
                            TAG,
                            "New Location: Lat=${location.latitude}, Lon=${location.longitude}"
                        )
                        locationState.value = location
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            mainLooper
        )
    }
}

@Composable
private fun BottomNavigation() {
    val navigationBarItems = NavigationBarItems.values()
    var selectedIndex by remember { mutableStateOf(0) }
    AnimatedNavigationBar(
        modifier = Modifier.height(64.dp),
        selectedIndex = selectedIndex,
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
                        selectedIndex = index
                        NavigationManager.navigateTo(item.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = if (selectedIndex == index)
                        MaterialTheme.colorScheme.inversePrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

enum class NavigationBarItems(val icon: ImageVector, val route: ScreensRoute) {
    Home(icon = Icons.Default.Home, route = ScreensRoute.HomeScreen),
    Favourite(icon = Icons.Default.Favorite, route = ScreensRoute.FavouriteScreen),
    Alert(icon = Icons.Default.Notifications, route = ScreensRoute.SearchScreen),
    Settings(icon = Icons.Default.Settings, route = ScreensRoute.SettingsScreen)
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}