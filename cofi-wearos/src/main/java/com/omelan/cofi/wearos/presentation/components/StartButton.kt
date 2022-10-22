package com.omelan.cofi.wearos.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import com.omelan.cofi.share.Step
import com.omelan.cofi.wearos.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun StartButton(
    currentStep: Step?,
    isTimerRunning: Boolean,
    animatedProgressValue: Animatable<Float, AnimationVector1D>,
    pauseAnimations: suspend () -> Unit,
    changeToNextStep: suspend (Boolean) -> Unit,
    startAnimations: suspend () -> Job,
) {
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            if (currentStep != null) {
                if (animatedProgressValue.isRunning) {
                    coroutineScope.launch { pauseAnimations() }
                } else {
                    coroutineScope.launch {
                        if (currentStep.time == null) {
                            changeToNextStep(false)
                        } else {
                            startAnimations()
                        }
                    }
                }
                return@Button
            }
            coroutineScope.launch { changeToNextStep(true) }
        },
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
