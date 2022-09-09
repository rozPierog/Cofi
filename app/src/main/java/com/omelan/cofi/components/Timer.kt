package com.omelan.cofi.components

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.ui.green600
import com.omelan.cofi.utils.toStringDuration

@Composable
fun Track(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    color: Color,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp,
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .aspectRatio(1f)

    ) {
        val startAngle = 270f
        val sweep = progress * 360f
        val diameterOffset = stroke.width / 2
        val arcDimen = size.width - 2 * diameterOffset
        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}

@Composable
fun Timer(
    modifier: Modifier = Modifier,
    currentStep: Step?,
    allSteps: List<Step> = emptyList(),
    alreadyDoneWeight: Int = 0,
    animatedProgressValue: Animatable<Float, AnimationVector1D>,
    animatedProgressColor: Animatable<Color, AnimationVector4D>,
    isInPiP: Boolean,
    isDone: Boolean = false,
) {
    val strokeWidth = if (isInPiP) {
        15.dp
    } else {
        20.dp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        AnimatedVisibility(visible = currentStep == null && !isDone) {
            RecipeInfo(steps = allSteps)
        }
        AnimatedVisibility(visible = isDone, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier
                    .padding(strokeWidth)
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.timer_enjoy),
                    style = if (isInPiP) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.headlineMedium
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isInPiP) 2 else Int.MAX_VALUE,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("timer_enjoy")
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
                    .aspectRatio(1f)
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .animateContentSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                if (currentStep != null) {
                    val durationInString = if (currentStep.time == null) {
                        stringResource(id = R.string.recipe_details_noTime)
                    } else {
                        val duration = (currentStep.time * animatedProgressValue.value).toInt()
                        duration.toStringDuration(
                            padMillis = true,
                            padMinutes = true,
                            padSeconds = true,
                            showMillis = !isInPiP
                        )
                    }
                    Text(
                        text = durationInString,
                        style = if (isInPiP) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.headlineMedium
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = if (isInPiP) 2 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = if (isInPiP) Spacing.xSmall else Spacing.normal)
                            .testTag("timer_duration")
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (currentStep.time != null) stringResource(
                            id = R.string.timer_step_name_time,
                            currentStep.name,
                            currentStep.time / 1000,
                        ) else currentStep.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = if (isInPiP) {
                            MaterialTheme.typography.titleSmall
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                        textAlign = TextAlign.Center,
                        maxLines = if (isInPiP) 1 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .animateContentSize()
                            .padding(horizontal = if (isInPiP) Spacing.xSmall else Spacing.normal)
                            .testTag("timer_name")
                    )
                    AnimatedVisibility(
                        visible = currentStep.value != null,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        val currentStepValue = currentStep.value ?: 0
                        val currentValueFromProgress =
                            (currentStepValue * animatedProgressValue.value).toInt()
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(
                                id = R.string.timer_progress_weight,
                                currentValueFromProgress + alreadyDoneWeight,
                                currentStepValue + alreadyDoneWeight,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = if (isInPiP) 1 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("timer_value"),
                            style = if (isInPiP) {
                                MaterialTheme.typography.titleLarge
                            } else {
                                MaterialTheme.typography.headlineMedium
                            },
                        )
                    }
                }
            }
        }
        Track(
            progress = if (isDone) 1f else animatedProgressValue.value,
            color = animatedProgressColor.value,
            strokeWidth = strokeWidth,
        )
    }
}

@SuppressLint("UnrememberedAnimatable")
@Preview
@Composable
fun TimerPreview() {
    Timer(
        currentStep = Step(
            id = 1,
            name = "ExperimentalAnimatedInsets ExperimentalAnimatedInsets " +
                "ExperimentalAnimatedInsets ExperimentalAnimatedInsets",
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
@Preview(widthDp = 150, showBackground = true)
@Composable
fun TimerPreviewPiP() {
    Timer(
        currentStep = Step(
            id = 1,
            name = "ExperimentalAnimatedInsets ExperimentalAnimatedInsets " +
                "ExperimentalAnimatedInsets ExperimentalAnimatedInsets",
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

@SuppressLint("UnrememberedAnimatable")
@Preview(widthDp = 150, showBackground = true)
@Composable
fun TimerPreviewDonePip() {
    Timer(
        currentStep = null,
        animatedProgressValue = Animatable(0.5f),
        animatedProgressColor = Animatable(green600),
        isInPiP = true,
        isDone = true
    )
}