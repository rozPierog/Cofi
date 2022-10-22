package com.omelan.cofi.share.timer

import android.media.MediaPlayer
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.omelan.cofi.share.R
import com.omelan.cofi.share.Step
import com.omelan.cofi.share.utils.Haptics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun <R, T> suspendCompat(block: suspend (T) -> R): suspend (T) -> R = block

data class TimerControllers(
    val currentStep: MutableState<Step?>,
    val isDone: Boolean,
    val isTimerRunning: Boolean,
    val indexOfCurrentStep: Int,
    val animatedProgressValue: Animatable<Float, AnimationVector1D>,
    val animatedProgressColor: Animatable<Color, AnimationVector4D>,
    val pauseAnimations: suspend () -> Unit,
    val progressAnimation: suspend (Unit) -> Unit,
    val startAnimations: suspend () -> Job,
    val changeToNextStep: suspend (Boolean) -> Unit,
)

object Timer {

    @Composable
    fun createTimerControllers(
        steps: List<Step>,
        onRecipeEnd: () -> Unit,
        isStepChangeSoundEnabled: Boolean,
        isStepChangeVibrationEnabled: Boolean,
    ): TimerControllers {
        val currentStep = remember { mutableStateOf<Step?>(null) }
        var isDone by remember { mutableStateOf(false) }
        var isTimerRunning by remember { mutableStateOf(false) }
        val animatedProgressValue = remember { Animatable(0f) }
        val isDarkMode = isSystemInDarkTheme()
        val animatedProgressColor =
            remember { Animatable(if (isDarkMode) Color.LightGray else Color.DarkGray) }
        val coroutineScope = rememberCoroutineScope()

        val context = LocalContext.current
        val haptics = Haptics(context)
        val mediaPlayer = MediaPlayer.create(context, R.raw.ding)

        val indexOfCurrentStep = steps.indexOf(currentStep.value)
        val indexOfLastStep = steps.lastIndex

        val pauseAnimations = suspend {
            animatedProgressColor.stop()
            animatedProgressValue.stop()
            isTimerRunning = false
        }

        val changeToNextStep = suspendCompat { silent: Boolean ->
            animatedProgressValue.snapTo(0f)
            if (indexOfCurrentStep != indexOfLastStep) {
                currentStep.value = steps[indexOfCurrentStep + 1]
            } else {
                currentStep.value = null
                isTimerRunning = false
                isDone = true
                onRecipeEnd()
            }
            if (silent) {
                return@suspendCompat
            }
            if (isStepChangeSoundEnabled) {
                mediaPlayer.start()
            }
            if (isStepChangeVibrationEnabled) {
                haptics.progress()
            }
        }

        val progressAnimation = suspendCompat<Unit, Unit> {
            val safeCurrentStep = currentStep.value ?: return@suspendCompat
            isDone = false
            isTimerRunning = true
            val currentStepTime = safeCurrentStep.time
            if (currentStepTime == null) {
                animatedProgressValue.snapTo(1f)
                isTimerRunning = false
                return@suspendCompat
            }
            val duration =
                (currentStepTime - (currentStepTime * animatedProgressValue.value)).toInt()
            coroutineScope.launch(Dispatchers.Default) {
                withContext(
                    object : MotionDurationScale {
                        override val scaleFactor: Float = 1f
                    },
                ) {
                    animatedProgressColor.animateTo(
                        targetValue = if (isDarkMode) {
                            safeCurrentStep.type.colorNight
                        } else {
                            safeCurrentStep.type.color
                        },
                        animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                    )
                }
            }
            withContext(
                object : MotionDurationScale {
                    override val scaleFactor: Float = 1f
                },
            ) {
                val result = animatedProgressValue.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                )
                if (result.endReason != AnimationEndReason.Finished) {
                    return@withContext
                }
                changeToNextStep(false)
            }
        }

        val startAnimations = suspend {
            coroutineScope.launch {
                progressAnimation(Unit)
            }
        }

        return TimerControllers(
            currentStep,
            isDone,
            isTimerRunning,
            indexOfCurrentStep,
            animatedProgressValue,
            animatedProgressColor,
            pauseAnimations,
            progressAnimation,
            startAnimations,
            changeToNextStep,
        )
    }

}
