package com.omelan.burr.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.model.Step
import com.omelan.burr.model.StepType
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.utils.toMillis
import com.omelan.burr.utils.toStringDuration
import kotlin.time.ExperimentalTime
import com.omelan.burr.R

enum class StepProgress { Current, Done, Upcoming }

@ExperimentalTime
@Composable
fun StepListItem(step: Step, stepProgress: StepProgress) {
    BurrTheme {
        ConstraintLayout(
            modifier = Modifier.animateContentSize().fillMaxWidth().padding(vertical = 5.dp),
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
                    modifier = Modifier.padding(horizontal = 5.dp).constrainAs(icon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }

                )

                Text(
                    text = step.name,
                    style = MaterialTheme.typography.subtitle1,
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