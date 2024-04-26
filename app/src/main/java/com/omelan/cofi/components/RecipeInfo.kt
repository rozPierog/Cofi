package com.omelan.cofi.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.toMillis
import com.omelan.cofi.share.utils.toStringDuration
import com.omelan.cofi.share.utils.toStringShort
import com.omelan.cofi.ui.Spacing
import kotlin.math.roundToInt

private data class CoffeeWaterTime(
    val coffeeWeight: Float = 0f,
    val waterWeight: Float = 0f,
    val duration: Int = 0,
) {
    fun addCoffee(weight: Float?, time: Int?): CoffeeWaterTime =
        this.copy(coffeeWeight = coffeeWeight + (weight ?: 0f), duration = duration + (time ?: 0))

    fun addWater(weight: Float?, time: Int?): CoffeeWaterTime =
        this.copy(waterWeight = waterWeight + (weight ?: 0f), duration = duration + (time ?: 0))

    fun addTime(time: Int?): CoffeeWaterTime = this.copy(duration = duration + (time ?: 0))
}

val enter = scaleIn(initialScale = 0.5f) + fadeIn()
val exit = scaleOut() + fadeOut()

@Composable
fun RecipeInfo(
    modifier: Modifier = Modifier,
    steps: List<Step>,
    compactStyle: Boolean = false,
    weightMultiplier: Float = 1f,
    timeMultiplier: Float = 1f,
) {
    val stepInfo by remember(steps) {
        derivedStateOf {
            steps.fold(CoffeeWaterTime()) { acc, step ->
                return@fold when (step.type) {
                    StepType.ADD_COFFEE -> acc.addCoffee(step.value, step.time)
                    StepType.WATER -> acc.addWater(step.value, step.time)
                    else -> acc.addTime(step.time)
                }
            }
        }
    }
    val localDensity = LocalDensity.current
    var isSmall by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val width = with(localDensity) { coordinates.size.width.toDp() }
                isSmall = width < 225.0.dp
            }
            .testTag("recipe_info_box"),
        contentAlignment = Alignment.Center,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.normal),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                AnimatedVisibility(
                    modifier = Modifier.animateItem(),
                    visible = stepInfo.coffeeWeight * weightMultiplier > 0,
                    enter = enter,
                    exit = exit,
                ) {
                    Param(
                        modifier = Modifier.testTag("recipe_info_coffee"),
                        icon = painterResource(id = R.drawable.recipe_icon_coffee_grinder),
                        text = "${(stepInfo.coffeeWeight * weightMultiplier).toStringShort()}g",
                        compactStyle = compactStyle,
                    )
                }
            }
            item {
                AnimatedVisibility(
                    modifier = Modifier.animateItem(),
                    visible = stepInfo.waterWeight * weightMultiplier > 0,
                    enter = enter,
                    exit = exit,
                ) {
                    Param(
                        modifier = Modifier.testTag("recipe_info_water"),
                        icon = painterResource(id = R.drawable.ic_water),
                        text = "${(stepInfo.waterWeight * weightMultiplier).toStringShort()}g",
                        compactStyle = compactStyle,
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = !isSmall && (stepInfo.duration * timeMultiplier) > 0,
                    enter = enter,
                    exit = exit,
                ) {
                    Param(
                        modifier = Modifier.testTag("recipe_info_time"),
                        icon = painterResource(id = R.drawable.ic_timer),
                        text = (stepInfo.duration * timeMultiplier).roundToInt().toStringDuration(),
                        compactStyle = compactStyle,
                    )
                }
            }
        }
        if (!compactStyle) {
            HorizontalDivider()
        }
    }
}

@Composable
fun Param(modifier: Modifier = Modifier, icon: Painter, text: String, compactStyle: Boolean) {
    Container(
        modifier = modifier.animateContentSize(),
        compactStyle = compactStyle,
    ) {
        Icon(
            modifier = Modifier.size(if (compactStyle) 20.dp else 30.dp),
            painter = icon,
            contentDescription = "",
        )
        Spacer(modifier = Modifier.size(Spacing.normal))
        Text(
            modifier = Modifier.testTag("recipe_info_text"),
            text = text,
            style = if (compactStyle) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.headlineSmall
            },
        )
    }
}

@Composable
fun Container(
    modifier: Modifier = Modifier,
    compactStyle: Boolean,
    content: @Composable () -> Unit,
) {
    if (compactStyle) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    } else {
        Column(
            modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            content()
        }
    }
}

@Composable
@Preview
fun RecipeInfoPreview() {
    RecipeInfo(
        steps = listOf(
            Step(
                name = stringResource(R.string.prepopulate_step_coffee),
                value = 30.0f,
                time = 5.toMillis(),
                type = StepType.ADD_COFFEE,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                value = 60.0f,
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
                value = 240.0f,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                time = 30.toMillis(),
                type = StepType.WATER,
                value = 200.0f,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_swirl),
                time = 5.toMillis(),
                type = StepType.OTHER,
            ),
        ),
    )
}

@Composable
@Preview
fun RecipeInfoCompactPreview() {
    RecipeInfo(
        compactStyle = true,
        steps = listOf(
            Step(
                name = stringResource(R.string.prepopulate_step_coffee),
                value = 30.0f,
                time = 5.toMillis(),
                type = StepType.ADD_COFFEE,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                value = 60.0f,
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
                value = 240.0f,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_water),
                time = 30.toMillis(),
                type = StepType.WATER,
                value = 200.0f,
            ),
            Step(
                name = stringResource(R.string.prepopulate_step_swirl),
                time = 5.toMillis(),
                type = StepType.OTHER,
            ),
        ),
    )
}
