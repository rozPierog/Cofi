package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.toMillis
import com.omelan.cofi.utils.toStringDuration
import kotlin.time.ExperimentalTime

enum class StepProgress { Current, Done, Upcoming }

@Composable
fun StepListItem(step: Step, stepProgress: StepProgress, onClick: ((Step) -> Unit)? = null) {
    val constraintModifier = Modifier
        .animateContentSize()
        .fillMaxWidth()
        .padding(vertical = 5.dp)
        .clickable(onClick = { onClick?.let { it(step) } }, enabled = onClick != null)

    CofiTheme {
        ConstraintLayout(
            modifier = constraintModifier
        ) {
            val (icon, name, valueAndTimeBox) = createRefs()
            val imageVector = when (stepProgress) {
                StepProgress.Current -> Icons.Rounded.PlayArrow
                StepProgress.Done -> Icons.Rounded.CheckCircle
                StepProgress.Upcoming -> when (step.type) {
                    StepType.WATER -> vectorResource(id = R.drawable.ic_water_plus)
                    StepType.ADD_COFFEE -> vectorResource(id = R.drawable.ic_coffee)
                    StepType.WAIT -> vectorResource(id = R.drawable.ic_progress_clock)
                    StepType.OTHER -> vectorResource(id = R.drawable.ic_playlist_edit)
                }
            }
            Icon(
                imageVector = imageVector,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(icon) {
                        start.linkTo(parent.start, 5.dp)
                        centerVerticallyTo(parent)
                    }

            )

            Text(
                text = step.name,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(icon.end, 5.dp)
                        centerVerticallyTo(parent)
                        end.linkTo(valueAndTimeBox.start, 5.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            Row(Modifier.constrainAs(valueAndTimeBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }) {
                if (step.value != null) {
                    Text(
                        text = "${step.value}g",
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)

                    )
                }
                Text(
                    text = step.time.toStringDuration(),
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)

                )
            }

        }
    }
}

@ExperimentalTime
@Preview
@Composable
fun StepListItemPreview() {
    StepListItem(
        step = Step(
            id = 0,
            name = "Somebody once told me the world is gonna roll me I ain't the sharpest tool in the shed She was looking kind of dumb with her finger and her thumb In the shape of an \"L\" on her forehead",
            time = 35.toMillis(),
            type = StepType.WATER,
            value = 60,
            orderInRecipe = 0,
        ),
        stepProgress = StepProgress.Current,
    )
}
@ExperimentalTime
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