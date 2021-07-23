package com.omelan.cofi.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.green600
import com.omelan.cofi.ui.grey300
import com.omelan.cofi.ui.grey600
import com.omelan.cofi.utils.toStringDuration

@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@Composable
fun Timer(
    modifier: Modifier = Modifier,
    currentStep: Step?,
    alreadyDoneWeight: Int = 0,
    animatedProgressValue: Animatable<Float, AnimationVector1D>,
    animatedProgressColor: Animatable<Color, AnimationVector4D>,
    isInPiP: Boolean,
    isDone: Boolean = false,
) {
    val strokeWidth = if (isInPiP) {
        15.dp
    } else {
        25.dp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            color = if (MaterialTheme.colors.isLight) {
                grey300
            } else {
                grey600
            },
            strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = if (isDone) 1f else animatedProgressValue.value,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            color = animatedProgressColor.value,
            strokeWidth = strokeWidth
        )
        AnimatedVisibility(visible = isDone, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier
                    .padding(strokeWidth)
                    .animateContentSize()
            ) {
                Text(
                    text = stringResource(id = R.string.timer_enjoy),
                    style = if (isInPiP) {
                        MaterialTheme.typography.subtitle1
                    } else {
                        MaterialTheme.typography.h6
                    },
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .testTag("timer_enjoy")
                )
            }
        }
        AnimatedVisibility(
            visible = currentStep != null && !isDone,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .padding(strokeWidth)
                    .animateContentSize()
            ) {
                if (currentStep != null) {
                    val duration = (currentStep.time * animatedProgressValue.value).toInt()

                    val durationInString = duration.toStringDuration(
                        padMillis = true,
                        padMinutes = true,
                        padSeconds = true,
                        showMillis = !isInPiP
                    )
                    Text(
                        text = durationInString,
                        style = if (isInPiP) {
                            MaterialTheme.typography.subtitle1
                        } else {
                            MaterialTheme.typography.h6
                        },
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .testTag("timer_duration")
                    )
                    Divider(
                        color = MaterialTheme.colors.onSurface,
                    )
                    Text(
                        text = stringResource(
                            id = R.string.timer_step_name_time,
                            currentStep.name,
                            currentStep.time / 1000
                        ),
                        color = MaterialTheme.colors.onSurface,
                        style = if (isInPiP) {
                            MaterialTheme.typography.subtitle2
                        } else {
                            MaterialTheme.typography.subtitle1
                        },
                        modifier = Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .testTag("timer_name")
                    )
                    currentStep.value?.let {
                        val currentValueFromProgress =
                            (currentStep.value * animatedProgressValue.value).toInt()
                        Divider(
                            color = MaterialTheme.colors.onSurface,
                        )
                        Text(
                            text = stringResource(
                                id = R.string.timer_progress_weight,
                                currentValueFromProgress + alreadyDoneWeight,
                                it + alreadyDoneWeight,
                            ),
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterHorizontally
                                )
                                .testTag("timer_value"),
                            style = if (isInPiP) {
                                MaterialTheme.typography.h6
                            } else {
                                MaterialTheme.typography.h5
                            },
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedAnimatable")
@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@Preview
@Composable
fun TimerPreview() {
    Timer(
        currentStep = Step(
            id = 1,
            name = "Stir",
            time = 5 * 1000,
            type = StepType.OTHER,
            orderInRecipe = 0,
        ),
        animatedProgressValue = Animatable(0.5f),
        animatedProgressColor = Animatable(green600),
        isInPiP = false,
        isDone = false
    )
}

@SuppressLint("UnrememberedAnimatable")
@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@Preview
@Composable
fun TimerPreviewPiP() {
    Timer(
        currentStep = Step(
            id = 1,
            name = "Stir",
            time = 5 * 1000,
            type = StepType.WATER,
            value = 300,
            orderInRecipe = 0,
        ),
        animatedProgressValue = Animatable(0.5f),
        animatedProgressColor = Animatable(green600),
        isInPiP = true,
        isDone = false
    )
}

@SuppressLint("UnrememberedAnimatable")
@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@Preview
@Composable
fun TimerPreviewDone() {
    Timer(
        currentStep = null,
        animatedProgressValue = Animatable(0.5f),
        animatedProgressColor = Animatable(green600),
        isInPiP = false,
        isDone = true
    )
}