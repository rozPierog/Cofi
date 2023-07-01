@file:OptIn(ExperimentalAnimationGraphicsApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R

@Composable
fun StartFAB(isTimerRunning: Boolean, onClick: () -> Unit) {
    val icon = AnimatedImageVector.animatedVectorResource(R.drawable.play_anim)
    val animatedFabRadii by animateFloatAsState(
        if (isTimerRunning) 28.0f else 100f,
        tween(if (isTimerRunning) 300 else 500),
        label = "Fab Radius",
    )
    LargeFloatingActionButton(
        shape = RoundedCornerShape(animatedFabRadii.dp),
        onClick = onClick,
        modifier = Modifier
            .navigationBarsPadding()
            .semantics {
                this.toggleableState = if (isTimerRunning) {
                    ToggleableState.On
                } else {
                    ToggleableState.Off
                }
            }
            .testTag("recipe_start"),
    ) {
        Icon(
            painter = rememberAnimatedVectorPainter(icon, isTimerRunning),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            contentDescription = null,
        )
    }
}
