package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    onRestore: (T) -> Unit = {},
    animationDuration: Int = 500,
    snackbarHostState: SnackbarHostState,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    var canSwipe by remember { mutableStateOf(true) }

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                canSwipe = false
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            val result = snackbarHostState.showSnackbar(
                message = "Location deleted from favourites",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRestore(item)
                canSwipe = true
                isRemoved = false
            } else {
                onDelete(item)
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = animationDuration)
        ) + fadeOut()
    ) {
        if (canSwipe) {
            SwipeToDismissBox(
                state = state,
                backgroundContent = { DeleteBackground(state) },
                enableDismissFromStartToEnd = false
            ) {
                content(item)
            }
        } else {
            LaunchedEffect(Unit) {
                state.snapTo(SwipeToDismissBoxValue.Settled)
            }
            content(item)
        }
    }
}


@Composable
fun DeleteBackground(
    swipeDismissState: SwipeToDismissBoxState
) {
    val color = if (swipeDismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
        Color.Red
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .padding(start = 24.dp)
            .background(color)
        ,
        contentAlignment = Alignment.CenterEnd
    ) {

        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White,
        )
    }

}
