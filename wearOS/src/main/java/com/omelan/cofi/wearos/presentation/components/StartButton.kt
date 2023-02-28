package com.omelan.cofi.wearos.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import com.omelan.cofi.wearos.R

@Composable
fun StartButton(
    isTimerRunning: Boolean,
    onClick: () -> Unit,
) {
    val animatedFabRadii by animateFloatAsState(
        if (isTimerRunning) 28.0f else 100f,
        tween(if (isTimerRunning) 300 else 500),
    )

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
        shape = RoundedCornerShape(animatedFabRadii),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
        )
    }
}
