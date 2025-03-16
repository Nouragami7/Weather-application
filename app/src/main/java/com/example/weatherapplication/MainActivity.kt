package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weatherapplication.navigation.SetupNavHost
import com.example.weatherapplication.ui.screen.SplashScreen
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.navigation.NavigationManager
import com.example.weatherapplication.navigation.ScreensRoute
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var displaySplashScreen by remember { mutableStateOf(true) }
            if (displaySplashScreen) {
                SplashScreen {
                    displaySplashScreen = false
                }
            } else {
                Scaffold(
                    modifier = Modifier.padding(bottom = 56.dp),
                    bottomBar = { BottomNavigation() }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        SetupNavHost()
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomNavigation() {
        val navigationBarItems = NavigationBarItems.values()
        var selectedIndex by remember { mutableStateOf(0) }
        AnimatedNavigationBar(
            modifier = Modifier.height(64.dp),
            selectedIndex = selectedIndex,
            cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
            ballAnimation = Parabolic(tween(300)),
            indentAnimation = Height(tween(300)),
            ballColor = Color(0xFF41BDFC),
            barColor = Color(0xFF99CCFF),

        ) {
            navigationBarItems.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            selectedIndex = index
                            NavigationManager.navigateTo(item.route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = if (selectedIndex == index)
                            MaterialTheme.colorScheme.inversePrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    enum class NavigationBarItems(val icon: ImageVector, val route: String) {
        Home(icon = Icons.Default.Home, route = ScreensRoute.HomeScreen.route),
        Search(icon = Icons.Default.Search, route = ScreensRoute.SearchScreen.route),
        Settings(icon = Icons.Default.Settings, route = ScreensRoute.SettingsScreen.route),
        Favourite(icon = Icons.Default.Favorite, route = ScreensRoute.FavouriteScreen.route)
    }

    private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
        clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }
    }
}


