package com.example.weatherapplication.ui.screen.favourite.Map.viewmodel

import android.content.Context
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest

class PlacesHelper(private val context: Context) {
    private val placesClient = Places.createClient(context)

    fun fetchPlaceDetails(placeId: String, onResult: (LatLngBounds?, String?) -> Unit) {
        val fields = listOf(Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS_COMPONENTS)

        val request = FetchPlaceRequest.builder(placeId, fields).build()
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val country = place.addressComponents?.asList()?.find { component ->
                component.types.contains("country")
            }?.name

            onResult(place.viewport, country)
        }.addOnFailureListener {
            onResult(null, null)
        }
    }
}
