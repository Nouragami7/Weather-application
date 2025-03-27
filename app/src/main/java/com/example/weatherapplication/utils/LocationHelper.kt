package com.example.weatherapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(private val context: Context, private val onLocationUpdated: (Location) -> Unit) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val TAG = "LocationHelper"

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        if (!PermissionUtils.checkPermissions(context)) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationUpdated(location)
                } else {
                    requestNewLocationData()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get location: ${e.message}")
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(10f)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.i(TAG, "New Location: Lat=${location.latitude}, Lon=${location.longitude}")
                        onLocationUpdated(location)
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }
}
