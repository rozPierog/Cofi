package com.omelan.burr.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R
import com.omelan.burr.model.Step
import com.omelan.burr.model.StepType
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.ui.full
import com.omelan.burr.ui.shapes
import com.omelan.burr.utils.toMillis

@ExperimentalLayout
@Composable
fun StepAddCard(stepToEdit: Step? = null, save: (Step) -> Unit) {
    val pickedType = remember(stepToEdit) { mutableStateOf<StepType?>(stepToEdit?.type) }
    val pickedTypeName = pickedType.value?.stringRes?.let { stringResource(id = it) } ?: ""
    val stepName = remember(stepToEdit, pickedTypeName) {
        mutableStateOf<String>(
            stepToEdit?.name ?: pickedTypeName
        )
    }
    val stepTime = remember(stepToEdit) {
        mutableStateOf<String>(
            (stepToEdit?.time ?: 0).toString()
        )
    }
    val stepValue = remember(stepToEdit) {
        mutableStateOf<String>(
            (stepToEdit?.value ?: 0).toString()
        )
    }
    BurrTheme {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .animateContentSize()
            ) {
                Box {
                    FlowRow(mainAxisSpacing = 5.dp, crossAxisSpacing = 5.dp) {
                        StepType.values().forEach { stepType ->
                            Button(onClick = { pickedType.value = stepType }, shape = shapes.full) {
                                Text(
                                    text = if (pickedType.value == stepType) {
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
                if (pickedType.value != null) {
                    OutlinedTextField(
                        label = { Text(text = stringResource(id = R.string.step_add_name)) },
                        value = stepName.value,
                        singleLine = true,
                        onValueChange = { stepName.value = it })
                    OutlinedTextField(
                        label = { Text(text = stringResource(id = R.string.step_add_duration)) },
                        value = stepTime.value,
                        onValueChange = {
                            stepTime.value = ensureNumbersOnly(it) ?: stepTime.value
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (listOf<StepType>(
                            StepType.WATER,
                            StepType.ADD_COFFEE,
                            StepType.OTHER
                        ).contains(pickedType.value)
                    ) {
                        OutlinedTextField(
                            label = { Text(text = stringResource(id = R.string.step_add_weight)) },
                            value = stepValue.value,
                            onValueChange = {
                                stepValue.value = ensureNumbersOnly(it) ?: stepValue.value
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Button(
                        onClick = {
                            save(
                                Step(
                                    name = stepName.value,
                                    time = stepTime.value.safeToInt().toMillis(),
                                    type = pickedType.value ?: StepType.OTHER,
                                    value = if (stepValue.value.isNotBlank() || stepValue.value != "0") {
                                        stepValue.value.safeToInt()
                                    } else {
                                        null
                                    }

                                )
                            )
                        },
                        modifier = Modifier.padding(vertical = 15.dp)
                    ) {
                        Text(text = stringResource(id = R.string.step_add_save))
                    }
                }
            }
        }
    }
}

private fun String.safeToInt(): Int {
    return when {
        this.isEmpty() -> 0
        else -> this.toInt()
    }
}

private fun ensureNumbersOnly(string: String): String? {
    if (string.isEmpty()) {
        return string
    }
    return try {
        string.toInt()
        string
    } catch (e: NumberFormatException) {
        null
    }
}

@ExperimentalLayout
@Composable
@Preview
fun StepAddCardPreview() {
    StepAddCard(save = {})
}