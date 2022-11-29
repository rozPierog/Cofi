package com.omelan.cofi.share.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.omelan.cofi.share.R
import com.omelan.cofi.share.Step
import com.omelan.cofi.utils.toStringDuration
import kotlin.math.roundToInt

@Composable
fun ColumnScope.TimerValue(
    currentStep: Step,
    animatedProgressValue: Float,
    weightMultiplier: Float = 1f,
    alreadyDoneWeight: Int = 0,
    color: Color,
    maxLines: Int,
    style: TextStyle,
) {
    AnimatedVisibility(
        visible = currentStep.value != null,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        val currentStepValue = currentStep.value ?: 0
        val currentValueFromProgress =
            (currentStepValue * animatedProgressValue).toInt()
        Text(
            text = stringResource(
                id = R.string.timer_progress_weight,
                (currentValueFromProgress * weightMultiplier).roundToInt() + alreadyDoneWeight,
                (currentStepValue * weightMultiplier).roundToInt() + alreadyDoneWeight,
            ),
            color = color,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("timer_value"),
            style = style,
        )
    }
}

@Composable
fun ColumnScope.StepNameText(
    currentStep: Step,
    color: Color,
    style: TextStyle,
    maxLines: Int,
    paddingHorizontal: Dp,
) {
    Text(
        text = if (currentStep.time != null) {
            stringResource(
                id = R.string.timer_step_name_time,
                currentStep.name,
                currentStep.time / 1000,
            )
        } else {
            currentStep.name
        },
        color = color,
        style = style,
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .animateContentSize()
            .padding(horizontal = paddingHorizontal)
            .testTag("timer_name"),
    )
}

@Composable
fun ColumnScope.TimeText(
    currentStep: Step, animatedProgressValue: Float,
    color: Color, maxLines: Int, style: TextStyle,
    paddingHorizontal: Dp,
    showMillis: Boolean,
) {
    val durationInString = if (currentStep.time == null) {
        stringResource(id = R.string.recipe_details_noTime)
    } else {
        val duration = (currentStep.time * animatedProgressValue).toInt()
        duration.toStringDuration(
            padMillis = true,
            padMinutes = true,
            padSeconds = true,
            showMillis = showMillis,
        )
    }
    Text(
        text = durationInString,
        style = style,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = paddingHorizontal)
            .testTag("timer_duration"),
    )
}
