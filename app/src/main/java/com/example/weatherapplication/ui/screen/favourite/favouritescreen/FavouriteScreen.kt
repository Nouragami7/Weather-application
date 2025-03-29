package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import LottieAnimationView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark

@Composable
fun FavouriteScreen(
    goToDetails: (
       LocationData
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

    LaunchedEffect(Unit) {
        favViewModel.getAllFavouriteLocations()
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
                                        FavouriteItem(
                                            locations[index],
                                            favViewModel,
                                            goToDetails,
                                            snackbarHostState
                                        )
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
    goToDetails: (LocationData) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    SwipeToDeleteContainer(
        item = locationData,
        onDelete = { item ->
            favViewModel.deleteFromFavourite(item.latitude, item.longitude)
        },
        onRestore = {},
        snackbarHostState = snackbarHostState
    ) { item ->
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(12.dp)
                .shadow(10.dp, RoundedCornerShape(24.dp))
                .clickable {
                    Toast.makeText(
                        context,
                        "Clicked on ${item.latitude}, ${item.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToDetails(item)
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF64A8F1), Color(0xFF7CD1EF)), // Blue gradient
                            start = Offset(0f, 0f),
                            end = Offset(400f, 400f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.countries),
                        contentDescription = "Country Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f), // Pushes the icon to the right
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = locationData.country,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val cityName = locationData.city
                            .replace(" Governorate", "")
                            .replace(" County", "")
                            .replace(" Province", "")
                            .replace(" District", "")
                            .substringBefore(" ")

                        Text(
                            text = cityName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.weight(1f))
                       /* Button(onClick = { showNotification(context, locationData) }) {
                            Text(text = "Show Notification")
                        }*/
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Location Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


