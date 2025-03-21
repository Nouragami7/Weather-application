package com.example.weatherapplication.ui.screen.favourite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import java.io.IOException
import java.util.Locale

@Composable
fun MapScreen() {
    var selectedPoint by remember { mutableStateOf(LatLng(31.20663675, 29.907445625)) }
    var selectedPlaceName by remember { mutableStateOf("Unknown Place") }
    var selectedCountry by remember { mutableStateOf("Unknown Country") }
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

    LaunchedEffect(selectedPoint) {
        fetchCountryName(context, selectedPoint) { country ->
            selectedCountry = country ?: "Unknown Country"
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data ?: return@rememberLauncherForActivityResult)
            selectedPoint = place.latLng ?: selectedPoint
            selectedPlaceName = place.name ?: "Unknown Place"
            markerState.position = selectedPoint
            cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedPoint, 15f)

            fetchPlaceDetails(context, place.id) { bounds, country ->
                bounds?.let {
                    polygonPoints = listOf(
                        it.southwest,
                        LatLng(it.northeast.latitude, it.southwest.longitude),
                        it.northeast,
                        LatLng(it.southwest.latitude, it.northeast.longitude)
                    )
                }
                selectedCountry = country ?: "Unknown Country"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedPoint = latLng
                selectedPlaceName = "Custom Location"
                markerState.position = latLng
            }
        ) {
            Marker(
                state = markerState,
                title = selectedPlaceName,
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

        Box(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = {
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS_COMPONENTS,
                        Place.Field.VIEWPORT
                    )
                ).build(context)
                launcher.launch(intent)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        }

        selectedPlaceName.takeIf { it.isNotBlank() }?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Country: $selectedCountry", color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Lat: ${selectedPoint.latitude}, Lng: ${selectedPoint.longitude}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { /* Add to favorites logic */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Add to Favorites")
                    }
                }
            }
        }
    }
}

fun fetchCountryName(context: Context, latLng: LatLng, onResult: (String?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val country = addresses?.firstOrNull()?.countryName
        onResult(country)
    } catch (e: IOException) {
        e.printStackTrace()
        onResult(null)
    }
}

fun fetchPlaceDetails(context: Context, placeId: String, onResult: (LatLngBounds?, String?) -> Unit) {
    val placesClient = Places.createClient(context)
    val fields = listOf(Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS_COMPONENTS)

    val request = FetchPlaceRequest.builder(placeId, fields).build()
    placesClient.fetchPlace(request).addOnSuccessListener { response ->
        val place = response.place
        val country = place.addressComponents?.asList()?.find { component ->
            component.types.contains("country")
        }?.name

        println("DEBUG: Place found - ${place.name}, Country: $country, LatLng: ${place.latLng}")
        onResult(place.viewport, country)
    }.addOnFailureListener { exception ->
        println("ERROR: Failed to fetch place details - ${exception.localizedMessage}")
        onResult(null, null)
    }
}