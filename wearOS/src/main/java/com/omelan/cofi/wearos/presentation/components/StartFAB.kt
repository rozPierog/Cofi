package com.omelan.cofi.wearos.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import com.omelan.cofi.share.greyBlue900
import com.omelan.cofi.wearos.R

@Composable
fun StartFAB(isTimerRunning: Boolean, onClick: () -> Unit) {
    val animatedFabRadii by animateFloatAsState(
        if (isTimerRunning) 28.0f else 100f,
        tween(if (isTimerRunning) 300 else 500),
        label = "Fab Radius",
    )
    val backgroundColor by animateColorAsState(
        if (isTimerRunning) greyBlue900 else MaterialTheme.colors.primary,
        tween(if (isTimerRunning) 300 else 500),
        label = "Fab color",
    )
    val iconColor by animateColorAsState(
        if (isTimerRunning) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary,
        tween(if (isTimerRunning) 300 else 500),
        label = "Fab icon color",
    )
    val size by animateDpAsState(
        if (isTimerRunning) ButtonDefaults.SmallButtonSize else ButtonDefaults.LargeButtonSize,
        tween(if (isTimerRunning) 300 else 500),
        label = "Fab size",
    )
    val icon = remember(isTimerRunning) {
        if (isTimerRunning) R.drawable.ic_pause else R.drawable.ic_play
    }
    Button(
        modifier = Modifier
            .testTag("start_button")
            .size(size),
        onClick = onClick,
        colors = ButtonDefaults.primaryButtonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(animatedFabRadii),
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = iconColor)
    }
}
