package com.omelan.cofi.wearos.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import com.omelan.cofi.wearos.R
import kotlinx.coroutines.launch

@Composable
fun StartButton(
    isTimerRunning: Boolean,
    onClick: () -> Unit,
) {
    val animatedFabRadii = remember { Animatable(100f) }
    var atEnd by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        launch {
            atEnd = isTimerRunning
        }
        launch {
            animatedFabRadii.animateTo(
                if (isTimerRunning) 28.0f else 100f,
                tween(if (isTimerRunning) 300 else 500),
            )
        }
    }
    val icon = remember(isTimerRunning) {
        if (isTimerRunning) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
    }
    Button(
        modifier = Modifier.testTag("start_button"),
        onClick = onClick,
        shape = RoundedCornerShape(animatedFabRadii.value),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
        )
    }
}
