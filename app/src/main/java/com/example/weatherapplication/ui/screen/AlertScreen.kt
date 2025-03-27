package com.example.weatherapplication.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark

@Composable
fun AlertScreen(){
    var showAlert by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier.padding(16.dp, bottom = 60.dp),
                containerColor = SkyBlue
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "alert",
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
            if (showAlert) {
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

                    }
                }
            }
        }
    }