package com.example.weatherapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.BabyBlue
import com.example.weatherapplication.ui.theme.IceBlue
import com.example.weatherapplication.ui.theme.LightBlue
import com.example.weatherapplication.ui.theme.LightSkyBlue
import com.example.weatherapplication.ui.theme.PaleSkyBlue
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.SoftSkyBlue
import com.example.weatherapplication.ui.theme.onSecondaryContainerDarkHighContrast
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onCompletion: () -> Unit) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1000
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onCompletion()

    }

    val gradientColors = listOf(
        SkyBlue,
        LightSkyBlue,
        SoftSkyBlue,
        PaleSkyBlue,
        LightBlue,
        BabyBlue,
        IceBlue
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.width(300.dp).height(200.dp),
            alignment = Alignment.Center,
        )
        Text(
            text = "Cloudora",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = onSecondaryContainerDarkHighContrast ,
                    blurRadius = 8f
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}