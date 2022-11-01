@file:OptIn(ExperimentalAnimationGraphicsApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import kotlinx.coroutines.launch

@Composable
fun StartFAB(isTimerRunning: Boolean, onClick: () -> Unit) {
    val animatedFabRadii = remember { Animatable(100f) }
    val icon = AnimatedImageVector.animatedVectorResource(R.drawable.play_anim)
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
    LargeFloatingActionButton(
        shape = RoundedCornerShape(animatedFabRadii.value.dp),
        onClick = onClick,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("recipe_start")
            .toggleable(isTimerRunning) {},
    ) {
        Icon(
            painter = rememberAnimatedVectorPainter(icon, atEnd),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            contentDescription = null,
        )
    }
}
