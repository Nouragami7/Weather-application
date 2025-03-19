package com.example.weatherapplication.ui.screen.favourite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun FavouriteScreen() {
    var showMap by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMap = !showMap },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (showMap) "Hide" else "Show")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Prevent FAB from overlapping map
        ) {
            if (showMap) {
                MapScreen(Modifier.fillMaxSize())
            } else {
                Text(
                    text = "Tap the button to view the map",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val singapore = LatLng(31.20663675, 29.907445625)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = rememberMarkerState(position = singapore),
            title = "Singapore",
            snippet = "A beautiful place"
        )
    }
}


