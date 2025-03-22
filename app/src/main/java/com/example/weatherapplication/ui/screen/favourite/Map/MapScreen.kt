import android.app.Activity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.utils.Constants.Companion.API_KEY_Google
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val geocoderHelper = remember { GeocoderHelper(context) }
    val placesHelper = remember { PlacesHelper(context) }

    val selectedPoint by remember { derivedStateOf { mapViewModel.selectedPoint } }
    val selectedPlaceName by remember { derivedStateOf { mapViewModel.selectedPlaceName } }
    val selectedCountry by remember { derivedStateOf { mapViewModel.selectedCountry } }
    val polygonPoints by remember { derivedStateOf { mapViewModel.polygonPoints } }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedPoint, 10f)
    }
    val markerState = rememberMarkerState(position = selectedPoint)

    LaunchedEffect(Unit) {
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(context, API_KEY_Google)
        }
        mapViewModel.fetchCountryName(geocoderHelper)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = com.google.android.libraries.places.widget.Autocomplete.getPlaceFromIntent(
                result.data ?: return@rememberLauncherForActivityResult
            )
            mapViewModel.updateSelectedPoint(place.latLng ?: selectedPoint, place.name ?: "Unknown Place")
            markerState.position = selectedPoint
            cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedPoint, 15f)

            mapViewModel.fetchPlaceDetails(placesHelper, place.id)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                mapViewModel.updateSelectedPoint(latLng)
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
                val intent = com.google.android.libraries.places.widget.Autocomplete.IntentBuilder(
                    com.google.android.libraries.places.widget.model.AutocompleteActivityMode.OVERLAY,
                    listOf(
                        com.google.android.libraries.places.api.model.Place.Field.ID,
                        com.google.android.libraries.places.api.model.Place.Field.NAME,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
                        com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS,
                        com.google.android.libraries.places.api.model.Place.Field.VIEWPORT
                    )
                ).build(context)
                launcher.launch(intent)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        }
    }
}
