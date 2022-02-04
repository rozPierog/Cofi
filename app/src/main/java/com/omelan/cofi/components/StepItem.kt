package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.toMillis
import com.omelan.cofi.utils.toStringDuration

enum class StepProgress { Current, Done, Upcoming }

@Composable
fun StepListItem(
    modifier: Modifier = Modifier,
    step: Step,
    stepProgress: StepProgress,
    onClick: ((Step) -> Unit)? = null
) {
    val constraintModifier = modifier
        .animateContentSize()
        .fillMaxWidth()
        .padding(vertical = Spacing.small)
        .clickable(
            onClick = { onClick?.let { it(step) } },
            enabled = onClick != null,
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true),
        )

    ConstraintLayout(
        modifier = constraintModifier
    ) {
        val (icon, name, valueAndTimeBox) = createRefs()
        val painter = when (stepProgress) {
            StepProgress.Current -> painterResource(id = R.drawable.ic_play_arrow)
            StepProgress.Done -> painterResource(id = R.drawable.ic_check_circle)
            StepProgress.Upcoming -> when (step.type) {
                StepType.WATER -> painterResource(id = R.drawable.ic_water_plus)
                StepType.ADD_COFFEE -> painterResource(id = R.drawable.ic_coffee)
                StepType.WAIT -> painterResource(id = R.drawable.ic_progress_clock)
                StepType.OTHER -> painterResource(id = R.drawable.ic_playlist_edit)
            }
        }
        Icon(
            painter = painter,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start, Spacing.small)
                    centerVerticallyTo(parent)
                }

        )

        Text(
            text = step.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(icon.end, Spacing.small)
                    centerVerticallyTo(parent)
                    end.linkTo(valueAndTimeBox.start, Spacing.small)
                    width = Dimension.fillToConstraints
                }
        )
        Row(
            Modifier.constrainAs(valueAndTimeBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            if (step.value != null) {
                Text(
                    text = "${step.value}g",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = Spacing.small)

                )
            }
            Text(
                text = step.time.toStringDuration(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = Spacing.small)
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
            value = 60,
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