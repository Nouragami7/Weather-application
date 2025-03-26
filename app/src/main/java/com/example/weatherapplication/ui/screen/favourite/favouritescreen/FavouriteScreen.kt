package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import GeocoderHelper
import LottieAnimationView
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.R
import com.example.weatherapplication.datasource.local.WeatherDatabase
import com.example.weatherapplication.datasource.local.WeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.ApiService
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.remote.RetrofitHelper
import com.example.weatherapplication.datasource.remote.WeatherRemoteDataSource
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.LocationData
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun FavouriteScreen(
    goToDetails: (
        latitude: Double,
        longitude: Double
    ) -> Unit
) {
    var showMap by remember { mutableStateOf(false) }
    var isFavourite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val factory = FavouriteViewModel.MapViewModelFactory(
        WeatherRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.retrofitInstance.create(ApiService::class.java)),
            WeatherLocalDataSource(WeatherDatabase.getDatabase(context).locationDao())
        )
    )
    val favViewModel: FavouriteViewModel = viewModel(factory = factory)
    val favouriteState by favViewModel.favLocations.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        favViewModel.getAllFavouriteLocations()
        favViewModel.toastEvent.collect { message ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    favViewModel.addLastDeletedLocation()
                }
            }

        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showMap = !showMap
                   isFavourite = true
                },
                modifier = Modifier.padding(16.dp, bottom = 60.dp),
                containerColor = SkyBlue
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "favourite",
                    tint = onPrimaryDark
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (showMap) {
                NavigationManager.navigateTo(ScreensRoute.MapScreen(isFavourite))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Text(
                        text = "Saved Locations",
                        style = TextStyle(
                            brush = Brush.verticalGradient(
                                0f to primaryContainerDark,
                                1f to onPrimaryDark
                            ),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = onPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    when (favouriteState) {
                        is ResponseState.Loading -> {
                            LoadingIndicator()
                        }

                        is ResponseState.Failure -> {
                            Toast.makeText(
                                context,
                                "An error occurred: ${(favouriteState as ResponseState.Failure).message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ResponseState.Success<*> -> {
                            val locations =
                                (favouriteState as ResponseState.Success<List<LocationData>>).data

                            if (locations.isEmpty()) {
                                Box(
                                    modifier = Modifier.wrapContentSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LottieAnimationView(
                                        resId = R.raw.no_data,
                                        modifier = Modifier.size(300.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    items(locations.size) { index ->
                                        FavouriteItem(locations[index], favViewModel, goToDetails)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FavouriteItem(
    locationData: LocationData,
    favViewModel: FavouriteViewModel,
    goToDetails: (latitude: Double, longitude: Double) -> Unit
) {
    val context = LocalContext.current
    val geocoderHelper = remember { GeocoderHelper(context) }
    var isVisible by remember { mutableStateOf(true) }
    val offsetX = remember { Animatable(0f) }

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(12.dp)
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SkyBlue, LightBlue)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .pointerInput(Unit) {
                    coroutineScope {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                launch {
                                    if (offsetX.value < -300 || offsetX.value > 300) {
                                        offsetX.animateTo(
                                            if (offsetX.value < 0) -600f else 600f,
                                            tween(300)
                                        )
                                        isVisible = false
                                        favViewModel.deleteLocationFromFavourite(
                                            locationData.latitude,
                                            locationData.longitude
                                        )

                                    } else {
                                        offsetX.animateTo(0f, tween(300))
                                    }
                                }
                            }
                        ) { _, dragAmount ->
                            launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                            }
                        }
                    }
                }
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Clicked on ${locationData.latitude}, ${locationData.longitude}",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToDetails(locationData.latitude, locationData.longitude)
                        }
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.countries),
                        contentDescription = "Favourite Icon",
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    val countryName = geocoderHelper.getLocationInfo(
                        LatLng(locationData.latitude, locationData.longitude)
                    ).country

                    val cityName = geocoderHelper.getLocationInfo(
                        LatLng(locationData.latitude, locationData.longitude)
                    ).city

                    Text(
                        text = "$countryName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    /*    Text(
                            text = "$cityName",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )*/

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Latitude: ${locationData.latitude}",
                            style = TextStyle(
                                brush = Brush.verticalGradient(
                                    0f to Color.White,
                                    1f to Color.White.copy(alpha = 0.8f)
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Longitude: ${locationData.longitude}",
                            style = TextStyle(
                                brush = Brush.verticalGradient(
                                    0f to Color.White,
                                    1f to Color.White.copy(alpha = 0.8f)
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
