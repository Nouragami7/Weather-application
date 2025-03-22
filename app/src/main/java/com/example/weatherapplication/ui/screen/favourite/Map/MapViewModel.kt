package com.example.weatherapplication.ui.screen.favourite.Map

import GeocoderHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    var selectedPoint by mutableStateOf(LatLng(31.20663675, 29.907445625))
    var selectedPlaceName by mutableStateOf("Unknown Place")
    var selectedCountry by mutableStateOf("Unknown Country")
    var polygonPoints by mutableStateOf<List<LatLng>>(emptyList())

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
}
