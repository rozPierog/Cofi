package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.ui.createTextFieldColors
import com.omelan.cofi.ui.full
import com.omelan.cofi.utils.ensureNumbersOnly
import com.omelan.cofi.utils.safeToInt
import com.omelan.cofi.utils.toMillis

@ExperimentalAnimatedInsets
@Composable
fun StepAddCard(
    stepToEdit: Step? = null,
    save: (Step?) -> Unit,
    orderInRecipe: Int,
    recipeId: Int
) {
    var pickedType by remember(stepToEdit) { mutableStateOf(stepToEdit?.type) }
    val pickedTypeName = pickedType?.stringRes?.let { stringResource(id = it) } ?: ""
    val stepName = remember(stepToEdit, pickedTypeName) {
        mutableStateOf(
            stepToEdit?.name ?: pickedTypeName
        )
    }
    val stepTime = remember(stepToEdit) {
        mutableStateOf(
            ((stepToEdit?.time ?: 0) / 1000).toString()
        )
    }
    val stepValue = remember(stepToEdit) {
        mutableStateOf(
            (stepToEdit?.value ?: 0).toString()
        )
    }
    val textFieldColors = androidx.compose.material3.MaterialTheme.createTextFieldColors()
    Surface(
        shape = shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(13.dp)
                .animateContentSize()
        ) {
            Box {
                FlowRow(mainAxisSpacing = Spacing.small, crossAxisSpacing = Spacing.small) {
                    StepType.values().forEach { stepType ->
                        Button(
                            onClick = { pickedType = stepType },
                            shape = shapes.full,
                            modifier = Modifier
                                .testTag(
                                    "step_type_button_${stepType.name.lowercase()}"
                                )
                                .padding(2.dp)
                        ) {
                            Text(
                                text = if (pickedType == stepType) {
                                    "✓ "
                                } else {
                                    ""
                                } + stringResource(id = stepType.stringRes),
                                modifier = Modifier.animateContentSize(),
                            )
                        }
                    }
                }
            }
            if (pickedType != null) {
                OutlinedTextField(
                    label = { Text(text = stringResource(id = R.string.step_add_name)) },
                    value = stepName.value,
                    singleLine = true,
                    onValueChange = { stepName.value = it },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    colors = textFieldColors,
                    modifier = Modifier
                        .testTag(
                            "step_name"
                        )
                        .padding(2.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    label = { Text(text = stringResource(id = R.string.step_add_duration)) },
                    value = stepTime.value,
                    onValueChange = {
                        stepTime.value = ensureNumbersOnly(it) ?: stepTime.value
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    modifier = Modifier
                        .testTag(
                            "step_time"
                        )
                        .padding(2.dp)
                        .fillMaxWidth(),
                )
                if (listOf(
                        StepType.WATER,
                        StepType.ADD_COFFEE,
                        StepType.OTHER
                    ).contains(pickedType)
                ) {
                    OutlinedTextField(
                        label = { Text(text = stringResource(id = R.string.step_add_weight)) },
                        value = stepValue.value,
                        onValueChange = {
                            stepValue.value = ensureNumbersOnly(it) ?: stepValue.value
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors,
                        modifier = Modifier
                            .testTag("step_value")
                            .padding(2.dp)
                            .fillMaxWidth(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            save(
                                Step(
                                    name = stepName.value,
                                    time = stepTime.value.safeToInt().toMillis(),
                                    type = pickedType ?: StepType.OTHER,
                                    value = if (stepValue.value.isNotBlank() &&
                                        stepValue.value.toInt() != 0 &&
                                        pickedType != StepType.WAIT
                                    ) {
                                        stepValue.value.toInt()
                                    } else {
                                        null
                                    },
                                    recipeId = recipeId,
                                    orderInRecipe = orderInRecipe,
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(vertical = Spacing.big, horizontal = Spacing.xSmall)
                            .testTag("step_save"),
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.small))
                        Text(text = stringResource(id = R.string.step_add_save))
                    }
                    if (stepToEdit != null) {
                        Button(
                            onClick = {
                                save(null)
                            },
                            modifier = Modifier
                                .padding(vertical = Spacing.big, horizontal = Spacing.xSmall)
                                .testTag("step_remove"),
                        ) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text(text = stringResource(id = R.string.step_add_remove))
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimatedInsets
@Composable
@Preview
fun StepAddCardPreview() {
    StepAddCard(save = {}, orderInRecipe = 0, recipeId = 0)
}

@ExperimentalAnimatedInsets
@Composable
@Preview
fun StepAddCardPreviewExpanded() {
    StepAddCard(
        save = {},
        orderInRecipe = 0,
        recipeId = 0,
        stepToEdit = Step(
            id = 0,
            recipeId = 0,
            name = "Add Water",
            time = 0,
            type = StepType.WATER,
            orderInRecipe = 0
        ),
    )
}