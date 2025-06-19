@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3BottomSheet
import com.omelan.cofi.components.OutlinedNumbersField
import com.omelan.cofi.model.CUSTOM_MULTIPLIER_DEFAULT_VALUE
import com.omelan.cofi.model.DataStore
import com.omelan.cofi.share.components.slideLeftRight
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.timer.MultiplierControllers
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.share.utils.toStringShort
import com.omelan.cofi.ui.Spacing
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RatioBottomSheet(
    multiplierControllers: MultiplierControllers,
    onDismissRequest: () -> Unit,
    allSteps: List<Step>,
) {
    val focusRequester = remember { FocusRequester() }

    Material3BottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = {
                if (it == SheetValue.Expanded) {
                    focusRequester.requestFocus()
                } else {
                    focusRequester.freeFocus()
                }
                true
            },
        ),
    ) {
        {
            Column(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(Spacing.big),
            ) {
                ManualContent(multiplierControllers, allSteps, focusRequester)
            }
        }
    }
}

@Composable
private fun ColumnScope.ManualContent(
    multiplierControllers: MultiplierControllers,
    allSteps: List<Step>,
    focusRequester: FocusRequester,
) {
    val (
        weightMultiplier,
        changeWeightMultiplier,
        timeMultiplier,
        changeTimeMultiplier,
    ) = multiplierControllers

    val dataStore = DataStore(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    val customMultiplier by dataStore.getCustomMultipliers()
        .collectAsState(initial = CUSTOM_MULTIPLIER_DEFAULT_VALUE)
    val combinedWaterWeight by remember(allSteps) {
        derivedStateOf {
            allSteps.sumOf {
                if (it.type == StepType.WATER) {
                    it.value?.toDouble() ?: 0.0
                } else {
                    0.0
                }
            }.toFloat()
        }
    }
    val combinedCoffeeWeight by remember(allSteps) {
        derivedStateOf {
            allSteps.sumOf {
                if (it.type == StepType.ADD_COFFEE) {
                    it.value?.toDouble() ?: 0.0
                } else {
                    0.0
                }
            }.toFloat()
        }
    }
    Title(stringResource(id = R.string.recipe_details_multiply_weight))
    Spacer(modifier = Modifier.height(Spacing.normal))
    Subtitle(
        text = stringResource(
            id = R.string.recipe_details_recipeRatio,
            (combinedWaterWeight / combinedCoffeeWeight).toStringShort(),
        ),
    )

    Row(
        modifier = Modifier.padding(top = Spacing.normal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
    ) {
        OutlinedNumbersField(
            modifier = Modifier
                .weight(1f, true)
                .focusRequester(focusRequester),
            value = (combinedCoffeeWeight * weightMultiplier).toString(),
            onValueChange = {
                val newWeightMultiplier =
                    ((it.toFloatOrNull() ?: combinedCoffeeWeight) / combinedCoffeeWeight)
                changeWeightMultiplier(if (newWeightMultiplier.isNaN()) 0f else newWeightMultiplier)
            },
            suffix = {
                Text(text = "g")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.recipe_icon_coffee_grinder),
                    contentDescription = null,
                )
            },
        )
        Text(text = ":")
        OutlinedNumbersField(
            modifier = Modifier.weight(1f, true),
            value = (combinedWaterWeight * weightMultiplier).toString(),
            onValueChange = {
                val newWeightMultiplier =
                    ((it.toFloatOrNull() ?: combinedWaterWeight) / combinedWaterWeight)
                changeWeightMultiplier(if (newWeightMultiplier.isNaN()) 0f else newWeightMultiplier)
            },
            suffix = {
                Text(text = "g")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_water),
                    contentDescription = null,
                )
            },
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.big),
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            customMultiplier.forEachIndexed { index, value ->
                item(key = value) {
                    ToggleButton(
                        checked = weightMultiplier == value.toFloat(),
                        onCheckedChange = {
                            if (it) {
                                changeWeightMultiplier(value.toFloat())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                            .semantics { role = Role.RadioButton },
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                    ) {
                        Text("${value.toFloat().toStringShort()}x")
                    }
                }
            }
        }
        ToggleButton(
            checked = false,
            onCheckedChange = { checked ->
                coroutineScope.launch {

                    if (customMultiplier.none { it.toFloat() == weightMultiplier }) {
                        dataStore.setCustomMultipliers(
                            customMultiplier.plus(weightMultiplier.toString())
                                .sortedBy { value -> value.toFloat() }
                                .toSet(),
                        )
                    } else {
                        dataStore.setCustomMultipliers(
                            customMultiplier.filter { it.toFloat() != weightMultiplier }.toSet(),
                        )
                    }
                }
            },
            colors = ToggleButtonDefaults.toggleButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .animateContentSize()
                .semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
        ) {
            AnimatedContent(customMultiplier.none { it.toFloat() == weightMultiplier }) {
                if (it) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = " stringResource(id = R.string.recipe_details_custom_multiplier),",
                        )
                        Text("${weightMultiplier.toStringShort()}x")
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = " stringResource(id = R.string.recipe_details_custom_multiplier),",
                    )
                }
            }
        }
    }
    Title(stringResource(id = R.string.recipe_details_multiply_time))
    SliderWithValue(timeMultiplier, changeTimeMultiplier)
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Light,
        color = MaterialTheme.colorScheme.secondary,
    )
}

const val step = 0.1f
val range = 0f..3f
val steps = (range.endInclusive / step).roundToInt() + 1

@Composable
private fun SliderWithValue(value: Float, setValue: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Slider(
            valueRange = range,
            steps = steps,
            value = value,
            onValueChange = { setValue(it.roundToDecimals()) },
            modifier = Modifier.weight(1f, true),
        )
        AnimatedContent(
            targetState = value,
            transitionSpec = slideLeftRight { target, initial -> target > initial },
            label = "slider value",
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = it.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = Spacing.normal),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RatioBottomSheetPreview() {
    MaterialTheme {
        Column {
            ManualContent(
                multiplierControllers = MultiplierControllers(
                    weightMultiplier = 1f,
                    changeWeightMultiplier = {},
                    timeMultiplier = 1f,
                    changeTimeMultiplier = {},
                ),
                allSteps = listOf(
                    Step(name = "Water", value = 200f, type = StepType.WATER),
                    Step(name = "Coffee", value = 20f, type = StepType.ADD_COFFEE),
                ),
                focusRequester = FocusRequester(),
            )
        }
    }
}
