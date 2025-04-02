package com.example.weatherapplication.ui.screen.favourite.Map.viewmodel

import GeocoderHelper
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.R
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.SharedPreference
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapViewModel(val repository: WeatherRepository) : ViewModel() {

    var selectedPoint by mutableStateOf(LatLng(31.20663675, 29.907445625))
    var selectedPlaceName by mutableStateOf("Unknown Place")
    private var selectedCountry by mutableStateOf("Unknown Country")
    private var selectedCity by mutableStateOf("Unknown City")
    var polygonPoints by mutableStateOf<List<LatLng>>(emptyList())

    private val sharedPreferences = SharedPreference()



    private val _message = MutableSharedFlow<String>()
    val messageState = _message.asSharedFlow()


    fun updateSelectedPoint(latLng: LatLng, placeName: String = "Custom Location") {
        selectedPoint = latLng
        selectedPlaceName = placeName
    }

    fun fetchCountryName(geocoderHelper: GeocoderHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            val locationInfo = geocoderHelper.getLocationInfo(selectedPoint)
            selectedCountry = locationInfo.country ?: "Unknown Country"
            selectedCity = locationInfo.city ?: "Unknown City"
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


     fun addLocationToFavourite(longitude: Double, latitude: Double,context: Context) {
        val lang = sharedPreferences.getFromSharedPreference(context, "language") ?: "en"
        val unit = sharedPreferences.getFromSharedPreference(context, "tempUnit") ?: "Celsius °C"
         val geocoderHelper = GeocoderHelper(context)
        viewModelScope.launch {
            val currentWeather = repository.getCurrentWeather(latitude, longitude,lang , unit, Constants.API_KEY).first()
            val forecast = repository.getForecast(latitude, longitude,lang , unit, Constants.API_KEY).first()
            val country = geocoderHelper.getLocationInfo(LatLng(latitude, longitude)).country ?: "Unknown Country"
            val city = geocoderHelper.getLocationInfo(LatLng(latitude, longitude)).city ?: "Unknown City"
            val locationData = LocationData(latitude, longitude, currentWeather, forecast, country, city)
            try {
                repository.insertLocation(locationData)
                _message.emit(context.getString(R.string.location_added_to_favourites))
            } catch (e: Exception) {
                _message.emit("An error occurred: ${e.message}")

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



