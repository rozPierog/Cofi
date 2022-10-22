package com.omelan.cofi.wearos.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import com.omelan.cofi.wearos.R

@Composable
fun StartButton(
    isTimerRunning: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
    ) {
        Icon(
            painter = if (isTimerRunning) {
                painterResource(id = R.drawable.ic_pause)
            } else {
                painterResource(id = R.drawable.ic_play)
            },
            contentDescription = null,
        )
    }
}
