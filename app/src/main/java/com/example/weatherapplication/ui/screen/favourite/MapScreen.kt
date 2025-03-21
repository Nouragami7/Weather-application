package com.example.weatherapplication.ui.screen.favourite

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    var selectedPoint by remember { mutableStateOf(LatLng(31.20663675, 29.907445625)) }
    var polygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedPoint, 10f)
    }

    val markerState = rememberMarkerState(position = selectedPoint)

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
        }

    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data ?: return@rememberLauncherForActivityResult)
            selectedPoint = place.latLng ?: selectedPoint
            markerState.position = selectedPoint
            cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedPoint, 15f)

            fetchPlaceDetails(context, place.id) { bounds ->
                bounds?.let {
                    polygonPoints = listOf(
                        it.southwest,
                        LatLng(it.northeast.latitude, it.southwest.longitude),
                        it.northeast,
                        LatLng(it.southwest.latitude, it.northeast.longitude)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Search location") },
            placeholder = { Text("Type a place name") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    ).build(context)
                    launcher.launch(intent)
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedPoint = latLng
                markerState.position = latLng
            }
        ) {
            Marker(
                state = markerState,
                title = "Selected Point",
                snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
            )

            if (polygonPoints.isNotEmpty()) {
                Polygon(
                    points = polygonPoints,
                    strokeWidth = 5f,
                    strokeColor = Color.Blue,
                    fillColor = Color(0x550000FF)
                )
            }
        }
    }
}

fun fetchPlaceDetails(context: Context, placeId: String, onResult: (LatLngBounds?) -> Unit) {
    val placesClient = Places.createClient(context)
    val fields = listOf(Place.Field.LAT_LNG, Place.Field.VIEWPORT)

    val request = FetchPlaceRequest.builder(placeId, fields).build()
    placesClient.fetchPlace(request).addOnSuccessListener { response ->
        val place = response.place
        onResult(place.viewport)
    }.addOnFailureListener {
        onResult(null)
    }
}