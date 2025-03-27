package com.example.weatherapplication.ui.screen.alert

import LottieAnimationView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.R
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen() {
    var isSheetOpen by remember { mutableStateOf(false) }
    var hasData by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isSheetOpen = true },
                modifier = Modifier.padding(16.dp, bottom = 60.dp),
                containerColor = SkyBlue
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.alert),
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.saved_alert),
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

                if (!hasData) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimationView(
                            resId = R.raw.no_fav,
                            modifier = Modifier.size(300.dp)
                        )
                    }
                } else {
                    DetailsAlert()
                }

            }
        }

        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
            ) {
                BottomSheetContent { isSheetOpen = false }
            }
        }
    }
}

