package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(val repository: WeatherRepository) : ViewModel() {

    private val favMutableLocations = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val favLocations = favMutableLocations.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    private var lastDeletedLocation: LocationData? = null
    private var lastDeletedIndex: Int? = null


    fun getAllFavouriteLocations() {
        viewModelScope.launch {
            try {
                favMutableLocations.value = ResponseState.Loading
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
    }

    /*fun deleteLocationFromFavourite(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val currentList =
                    (favMutableLocations.value as? ResponseState.Success<List<LocationData>>)?.data
                        ?: emptyList()
                val index = currentList.indexOfFirst { it.latitude == lat && it.longitude == lng }

                if (index != -1) {
                    lastDeletedLocation = currentList[index]
                    lastDeletedIndex = index
                    repository.deleteLocation(lat, lng)
                    _toastEvent.emit("Location deleted from favourites")
                    getAllFavouriteLocations()
                }
            } catch (e: Exception) {
                _toastEvent.emit("An error occurred: ${e.message}")
            }
        }
    }*/


    fun addLastDeletedLocation() {
        lastDeletedLocation?.let { location ->
            viewModelScope.launch {
                try {
                    repository.insertLocation(location)
                    _toastEvent.emit("Location deleted from favourites")
                    lastDeletedLocation = null
                    lastDeletedIndex = null
                } catch (e: Exception) {
                    _toastEvent.emit("An error occurred: ${e.message}")
                }
            }
        }
    }



 fun deleteFromFavourite(lat: Double, lng: Double) {
   viewModelScope.launch {
       try {
               repository.deleteLocation(lat, lng)
               // getAllFavouriteLocations()
       } catch (e: Exception) {
           _toastEvent.emit("An error occurred: ${e.message}")
       }
   }
}

class MapViewModelFactory(
   private val repository: WeatherRepository
) : ViewModelProvider.Factory {
   override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
           @Suppress("UNCHECKED_CAST")
           return FavouriteViewModel(repository) as T
       }
       throw IllegalArgumentException("Unknown ViewModel class")
   }
}


}