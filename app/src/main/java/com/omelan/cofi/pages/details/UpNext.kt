package com.omelan.cofi.pages.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.StepListItem
import com.omelan.cofi.components.StepProgress
import com.omelan.cofi.share.components.slideUpDown
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.ui.shapes

@Composable
fun UpNext(
    modifier: Modifier = Modifier,
    step: Step,
    weightMultiplier: Float = 1f,
    timeMultiplier: Float = 1f,
) {
    Surface(
        modifier = modifier.animateContentSize(),
        shape = shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Spacing.big),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Text(
                text = stringResource(id = R.string.recipe_details_upNext),
                style = MaterialTheme.typography.titleMedium,
            )
            AnimatedContent(
                targetState = step,
                transitionSpec = slideUpDown { target, initial ->
                    (target.orderInRecipe ?: 0) > (initial.orderInRecipe ?: 0)
                },
                label = "Next step",
            ) {
                StepListItem(
                    step = it,
                    stepProgress = StepProgress.Upcoming,
                    weightMultiplier = weightMultiplier,
                    timeMultiplier = timeMultiplier,
                )
            }
        }
    }
}
