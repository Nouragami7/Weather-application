package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute

@Composable
fun FavouriteScreen() {
    var showMap by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMap = !showMap },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = if (!showMap) Icons.Default.FavoriteBorder
                    else
                        Icons.Default.Favorite,
                    contentDescription = "favourite",

                    )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showMap) {
                NavigationManager.navigateTo(ScreensRoute.MapScreen)

            } else {
                Text(
                    text = "Tap the button to view the map",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

