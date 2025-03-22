package com.example.weatherapplication.ui.screen.favourite.Map

import GeocoderHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.enums.Location
import com.example.weatherapplication.ui.viewmodel.WeatherViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(val repository: WeatherRepository) : ViewModel() {

    var selectedPoint by mutableStateOf(LatLng(31.20663675, 29.907445625))
    var selectedPlaceName by mutableStateOf("Unknown Place")
    var selectedCountry by mutableStateOf("Unknown Country")
    var polygonPoints by mutableStateOf<List<LatLng>>(emptyList())

    private val favMutableLocations = MutableStateFlow<Location?>(null)
    val favLocations = favMutableLocations.asStateFlow()


    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    fun updateSelectedPoint(latLng: LatLng, placeName: String = "Custom Location") {
        selectedPoint = latLng
        selectedPlaceName = placeName
    }

    fun fetchCountryName(geocoderHelper: GeocoderHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedCountry = geocoderHelper.getCountryName(selectedPoint) ?: "Unknown Country"
        }
    }

    fun fetchPlaceDetails(placesHelper: PlacesHelper, placeId: String) {
        placesHelper.fetchPlaceDetails(placeId) { bounds, country ->
            selectedCountry = country ?: "Unknown Country"
            polygonPoints = bounds?.let {
                listOf(
                    it.southwest,
                    LatLng(it.northeast.latitude, it.southwest.longitude),
                    it.northeast,
                    LatLng(it.southwest.latitude, it.northeast.longitude)
                )
            } ?: emptyList()
        }
    }

    /*fun getAllFavouriteLocations() {
        viewModelScope.launch {
            try {
                val locations = repository.getAllLocations()
                locations.catch { e ->
                    favMutableLocations.value = ResponseState.Failure(e)
                    _toastEvent.emit("An error occurred: ${e.message}")
                }.collect {
                    favMutableLocations.value = ResponseState.Success(it)
                }
            } catch (e: Exception) {
                _toastEvent.emit("An error occurred: ${e.message}")
            }

        }
    }*/

    fun addLocationToFavourite(longitude: Double, latitude: Double) {
        val locationData = LocationData(latitude, longitude)
        viewModelScope.launch {
            try {
                repository.insertLocation(locationData)
                _toastEvent.emit("Location added to favourites")
            } catch (e: Exception) {
                _toastEvent.emit("An error occurred: ${e.message}")

            }
        }

    }

    fun deleteLocationFromFavourite(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                repository.deleteLocation(lat, lng)
                _toastEvent.emit("Location deleted from favourites")
            } catch (e: Exception) {
                _toastEvent.emit("An error occurred: ${e.message}")
            }
        }
    }

    class MapViewModelFactory(
        private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}



