package com.omelan.cofi.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.toMillis
import com.omelan.cofi.utils.toStringDuration

private data class CoffeeWaterTime(
    val coffeeWeight: Int = 0,
    val waterWeight: Int = 0,
    val duration: Int = 0
) {
    fun addCoffee(weight: Int, time: Int): CoffeeWaterTime =
        this.copy(coffeeWeight = coffeeWeight + weight, duration = duration + time)

    fun addWater(weight: Int, time: Int): CoffeeWaterTime =
        this.copy(waterWeight = waterWeight + weight, duration = duration + time)

    fun addTime(time: Int): CoffeeWaterTime = this.copy(duration = duration + time)
}

@Composable
fun RecipeInfo(modifier: Modifier = Modifier, steps: List<Step>) {
    val stepInfo by remember(steps) {
        derivedStateOf {
            steps.fold(CoffeeWaterTime()) { acc, step ->
                when (step.type) {
                    StepType.ADD_COFFEE -> return@fold acc.addCoffee(
                        step.value ?: 0,
                        step.time ?: 0
                    )
                    StepType.WATER -> return@fold acc.addWater(step.value ?: 0, step.time ?: 0)
                    else -> return@fold acc.addTime(step.time ?: 0)
                }
            }
        }
    }
    val localDensity = LocalDensity.current
    var isSmall by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val width = with(localDensity) { coordinates.size.width.toDp() }
                isSmall = width < 225.0.dp
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.normal),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Param(
                icon = painterResource(id = R.drawable.ic_coffee_grinder),
                text = "${stepInfo.coffeeWeight}g"
            )
            Param(
                icon = painterResource(id = R.drawable.ic_water),
                text = "${stepInfo.waterWeight}g"
            )
            AnimatedVisibility(
                visible = !isSmall,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
            ) {
                Param(
                    icon = painterResource(id = R.drawable.ic_timer),
                    text = stepInfo.duration.toStringDuration()
                )
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun Param(icon: Painter, text: String) {
    Column(
        modifier = Modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(modifier = Modifier.size(30.dp), painter = icon, contentDescription = "")
        Spacer(modifier = Modifier.height(Spacing.normal))
        Text(text = text, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
@Preview
fun RecipeInfoPreview() {
    RecipeInfo(
        steps = listOf(
            Step(
                name = stringResource(R.string.prepopulate_step_coffee),
                value = 30,
                time = 5.toMillis(),
                type = StepType.ADD_COFFEE,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                value = 60,
                time = 5.toMillis(),
                type = StepType.WATER,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_swirl),
                time = 5.toMillis(),
                type = StepType.OTHER,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_wait),
                time = 35.toMillis(),
                type = StepType.WAIT,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                time = 30.toMillis(),
                type = StepType.WATER,
                value = 240,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                time = 30.toMillis(),
                type = StepType.WATER,
                value = 200,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_swirl),
                time = 5.toMillis(),
                type = StepType.OTHER,
            )
        )
    )
}