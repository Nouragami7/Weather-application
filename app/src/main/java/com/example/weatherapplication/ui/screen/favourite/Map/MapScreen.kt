import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.datasource.local.WeatherDatabase
import com.example.weatherapplication.datasource.local.WeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.ApiService
import com.example.weatherapplication.datasource.remote.RetrofitHelper
import com.example.weatherapplication.datasource.remote.WeatherRemoteDataSource
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.ui.screen.favourite.Map.MapViewModel
import com.example.weatherapplication.ui.screen.favourite.Map.PlacesHelper
import com.example.weatherapplication.utils.Constants.Companion.API_KEY_Google
import com.example.weatherapplication.utils.SharedPreference
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    isFavourite: Boolean,
    getLocation: (latitude: Double, longitude: Double) -> Unit
) {
    val sharedPreference = SharedPreference()
    val context = LocalContext.current
    val geocoderHelper = remember { GeocoderHelper(context) }
    val placesHelper = remember { PlacesHelper(context) }

    val factory = MapViewModel.MapViewModelFactory(
        WeatherRepository.getInstance(
            WeatherRemoteDataSource(
                RetrofitHelper.retrofitInstance.create(ApiService::class.java)
            ), WeatherLocalDataSource(WeatherDatabase.getDatabase(context).locationDao())
        )
    )
    val mapViewModel: MapViewModel = viewModel(factory = factory)

    val selectedPoint by remember { derivedStateOf { mapViewModel.selectedPoint } }
    val selectedPlaceName by remember { derivedStateOf { mapViewModel.selectedPlaceName } }
    val polygonPoints by remember { derivedStateOf { mapViewModel.polygonPoints } }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mapViewModel.messageState.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
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
            val place = Autocomplete.getPlaceFromIntent(
                result.data ?: return@rememberLauncherForActivityResult
            )
            mapViewModel.updateSelectedPoint(
                place.latLng ?: selectedPoint,
                place.name ?: "Unknown Place"
            )
            markerState.position = selectedPoint
            cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedPoint, 15f)

            mapViewModel.fetchPlaceDetails(placesHelper, place.id)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
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
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .wrapContentSize(),
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

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                MapCard(
                    selectedPoint = selectedPoint,
                    actionName = if (isFavourite) "Add to Favorite" else "Select Location",
                    action = {
                        if (isFavourite) mapViewModel.addLocationToFavourite(
                            selectedPoint.longitude,
                            selectedPoint.latitude,
                            context
                        )
                        else {
                            sharedPreference.saveToSharedPreference(
                                context,
                                "latitude",
                                selectedPoint.latitude.toString()
                            )
                            sharedPreference.saveToSharedPreference(
                                context,
                                "longitude",
                                selectedPoint.longitude.toString()
                            )
                            getLocation(selectedPoint.latitude, selectedPoint.longitude)
                        }
                    }
                )
            }
        }
    }
}



