package com.example.weatherapplication.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedPoint = MutableStateFlow(LatLng(31.20663675, 29.907445625))
    val selectedPoint: StateFlow<LatLng> = _selectedPoint

    private val _selectedPlaceName = MutableStateFlow("Unknown Place")
    val selectedPlaceName: StateFlow<String> = _selectedPlaceName

    private val _selectedCountry = MutableStateFlow("Unknown Country")
    val selectedCountry: StateFlow<String> = _selectedCountry

    private val _polygonPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val polygonPoints: StateFlow<List<LatLng>> = _polygonPoints

    private val placesClient = Places.createClient(application)

    init {
        fetchCountryName(_selectedPoint.value)
    }

    fun updateSelectedPoint(latLng: LatLng, placeName: String) {
        _selectedPoint.value = latLng
        _selectedPlaceName.value = placeName
        fetchCountryName(latLng)
    }

    private fun fetchCountryName(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val country = addresses?.firstOrNull()?.countryName ?: "Unknown Country"
                _selectedCountry.value = country
            } catch (e: IOException) {
                _selectedCountry.value = "Unknown Country"
            }
        }
    }

    fun fetchPlaceDetails(placeId: String) {
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS_COMPONENTS)).build()
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            _selectedPoint.value = place.latLng ?: _selectedPoint.value
            _selectedPlaceName.value = place.name ?: "Unknown Place"

            val country = place.addressComponents?.asList()?.find { it.types.contains("country") }?.name
            _selectedCountry.value = country ?: "Unknown Country"

            place.viewport?.let {
                _polygonPoints.value = listOf(
                    it.southwest,
                    LatLng(it.northeast.latitude, it.southwest.longitude),
                    it.northeast,
                    LatLng(it.southwest.latitude, it.northeast.longitude)
                )
            }
        }.addOnFailureListener {
            _selectedCountry.value = "Unknown Country"
        }
    }
}
