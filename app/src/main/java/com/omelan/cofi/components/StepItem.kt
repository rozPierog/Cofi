@file:OptIn(ExperimentalAnimationGraphicsApi::class, ExperimentalAnimationGraphicsApi::class)

package com.omelan.cofi.components

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.share.Step
import com.omelan.cofi.share.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.toMillis
import com.omelan.cofi.utils.toStringDuration
import com.omelan.cofi.utils.toStringShort
import kotlin.math.roundToInt

enum class StepProgress { Current, Done, Upcoming }

@Composable
fun StepListItem(
    modifier: Modifier = Modifier,
    step: Step,
    stepProgress: StepProgress,
    weightMultiplier: Float = 1.0f,
    timeMultiplier: Float = 1.0f,
    onClick: ((Step) -> Unit)? = null,
) {
    val icon = AnimatedImageVector.animatedVectorResource(R.drawable.step_done_anim)
    var atEnd by remember { mutableStateOf(false) }
    val painter = when (stepProgress) {
        StepProgress.Current, StepProgress.Done -> rememberAnimatedVectorPainter(icon, atEnd)
        StepProgress.Upcoming -> painterResource(id = step.type.iconRes)
    }
    LaunchedEffect(stepProgress) {
        atEnd = stepProgress == StepProgress.Done
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 42.dp)
            .clickable(
                onClick = { onClick?.let { it(step) } },
                enabled = onClick != null,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
            )
            .padding(vertical = Spacing.small),
        Arrangement.Center,
        Alignment.CenterVertically,
    ) {
        Icon(
            painter = painter,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = Spacing.small),
        )
        Text(
            text = step.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f, true)
                .padding(horizontal = Spacing.small),
        )
        if (step.value != null) {
            Text(
                text = "${(step.value!! * weightMultiplier).toStringShort()}g",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = Spacing.small),

            )
        }
        if (step.time != null) {
            Text(
                text = (step.time!! * timeMultiplier).roundToInt().toStringDuration(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = Spacing.small),
            )
        }
    }
}

@Preview
@Composable
fun StepListItemPreview() {
    StepListItem(
        step = Step(
            id = 0,
            name = "Somebody once told me the world is gonna roll me I ain't the sharpest " +
                "tool in the shed She was looking kind of dumb with her finger and her thumb " +
                "In the shape of an \"L\" on her forehead",
            time = 35.toMillis(),
            type = StepType.WATER,
            value = 60.0f,
            orderInRecipe = 0,
        ),
        stepProgress = StepProgress.Current,
    )
}

@Preview
@Composable
fun StepListItemPreviewShort() {
    StepListItem(
        step = Step(
            id = 0,
            name = "Somebody once told",
            time = 35.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = 0,
        ),
        stepProgress = StepProgress.Current,
    )
}
