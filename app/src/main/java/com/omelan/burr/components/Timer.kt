package com.omelan.burr.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimatedValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.model.Step
import com.omelan.burr.model.StepType
import com.omelan.burr.ui.green600
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@Composable
fun Timer(
    modifier: Modifier = Modifier,
    currentStep: Step?,
    animatedProgressValue: AnimatedFloat,
    animatedProgressColor: AnimatedValue<Color, AnimationVector4D>,
    isInPiP: Boolean,
) {
    val strokeWidth = if (isInPiP) 15.dp else 25.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            color = Color(0xFFE8EAF6),
            strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = animatedProgressValue.value,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            color = animatedProgressColor.value,
            strokeWidth = strokeWidth
        )
        Column(modifier = Modifier.padding(strokeWidth).animateContentSize()) {

            if (currentStep != null) {
                val duration = (currentStep.time * animatedProgressValue.value).toInt()
                    .toDuration(DurationUnit.MILLISECONDS)
                val durationInString = "${
                    duration.inMinutes.toInt().toString().padStart(2, '0')
                }:${
                    duration.inSeconds.toInt().toString().padStart(2, '0')
                }".also {
                    if (!isInPiP) {
                        it + ":${
                            duration.inMilliseconds.toInt().toString().padStart(4, '0')
                        }"
                    }
                }
                Text(
                    text = durationInString,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                Divider(color = Color.Black)
                Text(
                    text = "${currentStep.name} (${currentStep.time / 1000}s)",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                currentStep.value?.let {
                    val currentValueFromProgress =
                        (currentStep.value * animatedProgressValue.value).toInt()
                    Divider(color = Color.Black)
                    Text(
                        text = "${currentValueFromProgress}g/${it}g",
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ), style = MaterialTheme.typography.h5
                    )
                }

            }
        }
    }
}

@ExperimentalTime
@Preview
@Composable
fun TimerPreview() {
    Timer(
        currentStep = Step(id = 1, name = "Stir", time = 5 * 1000, type = StepType.OTHER),
        animatedProgressValue = animatedFloat(initVal = 0.5f),
        animatedProgressColor = animatedColor(initVal = green600),
        isInPiP = false,
    )
}