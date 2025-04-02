package com.example.weatherapplication.ui.screen.notification.view

import LottieAnimationView
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.res.stringResource
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
import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.ui.screen.favourite.favouritescreen.view.LoadingIndicator
import com.example.weatherapplication.ui.screen.favourite.favouritescreen.view.SwipeToDeleteContainer
import com.example.weatherapplication.ui.screen.notification.viewmodel.AlertViewModel
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.theme.onPrimaryDark
import com.example.weatherapplication.ui.theme.primaryContainerDark
import com.example.weatherapplication.utils.isAlertExpired

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AlertScreen() {
    var isSheetOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val factory = AlertViewModel.AlertFactory(
        WeatherRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.retrofitInstance.create(ApiService::class.java)),
            WeatherLocalDataSource(WeatherDatabase.getDatabase(context).locationDao())
        )
    )
    val alertViewModel: AlertViewModel = viewModel(factory = factory)
    val alertState by alertViewModel.alert.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        alertViewModel.fetchAlertData()

    }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Location Icon",
                        tint = primaryContainerDark,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

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
                        fontWeight = FontWeight.Bold
                    )
                }

                when (alertState) {
                    is ResponseState.Failure -> {
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(
                                "Error: ${(alertState as ResponseState.Failure).message}"
                            )
                        }
                    }

                    ResponseState.Loading -> LoadingIndicator()
                    is ResponseState.Success<*> -> {
                        val alerts = (alertState as ResponseState.Success<List<AlertData>>).data
                        if (alerts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimationView(
                                    resId = R.raw.no_fav,
                                    modifier = Modifier.size(300.dp)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(alerts.size) { index ->
                                    AlertItem(
                                        alerts[index],
                                        alertViewModel,
                                        context,
                                        snackbarHostState
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isSheetOpen = false },
                    containerColor = SkyBlue
                ) {
                    BottomSheetContent(
                        alertViewModel = alertViewModel,
                        context = context,

                        ) { isSheetOpen = false }
                }
            }
        }
    }
}

@Composable
fun AlertItem(
    alertData: AlertData,
    alertViewModel: AlertViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(alertData) {
        if (isAlertExpired(alertData.startDate, alertData.startTime)) {
            alertViewModel.deleteFromAlerts(alertData, context)
        }
    }

    SwipeToDeleteContainer(
        item = alertData,
        onDelete = { alertViewModel.deleteFromAlerts(alertData, context) },
        onRestore = { alertViewModel.insertAtAlerts(alertData, onSuccess = {}) },
        snackbarHostState = snackbarHostState
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(12.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF4B91F1), Color(0xFF64D3EF)),
                            center = Offset(200f, 200f),
                            radius = 500f
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
                        painter = painterResource(id = R.drawable.clock_alert),
                        contentDescription = "Time Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = alertData.startTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = alertData.startDate,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Arrow Icon",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}






