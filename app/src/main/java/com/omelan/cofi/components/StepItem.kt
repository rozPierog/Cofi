package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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

@ExperimentalTime
@Composable
fun StepListItem(step: Step, stepProgress: StepProgress, onClick: ((Step) -> Unit)? = null) {
    val constraintModifier = Modifier.animateContentSize()
        .fillMaxWidth()
        .padding(vertical = 5.dp).clickable(onClick = { onClick?.let { it(step) } })

    CofiTheme {
        ConstraintLayout(
            modifier = constraintModifier
        ) {
            val (icon, name, value, time) = createRefs()
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
                modifier = Modifier.padding(horizontal = 5.dp).constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }

            )

            Text(
                text = step.name,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(horizontal = 5.dp).constrainAs(name) {
                    start.linkTo(icon.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )

            if (step.value != null) {
                Text(
                    text = "${step.value}g",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(horizontal = 5.dp).constrainAs(value) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(time.start)
                    }

                )
            }
            Text(
                text = step.time.toStringDuration(),
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(horizontal = 5.dp).constrainAs(time) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }

            )
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
            name = "Add water",
            time = 35.toMillis(),
            type = StepType.WATER,
            value = 60
        ),
        stepProgress = StepProgress.Current,
    )
}