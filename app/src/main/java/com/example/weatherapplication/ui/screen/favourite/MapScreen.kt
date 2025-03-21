package com.example.weatherapplication.ui.screen.favourite

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    var selectedPoint by remember { mutableStateOf(LatLng(31.20663675, 29.907445625)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedPoint, 10f)
    }

    val markerState = rememberMarkerState(position = selectedPoint)

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            selectedPoint = latLng
            markerState.position = latLng
          // cameraPositionState.move(CameraUpdateFactory.newLatLng(latLng))
        }

    ) {
        Marker(
            state = markerState,
            title = "Selected Point",
            snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
        )
    }
}
