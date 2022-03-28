package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.ensureNumbersOnly
import com.omelan.cofi.utils.safeToInt
import com.omelan.cofi.utils.toMillis

@Composable
fun StepAddCard(
    modifier: Modifier = Modifier,
    stepToEdit: Step? = null,
    save: (Step?) -> Unit,
    onTypeSelect: () -> Unit = {},
    orderInRecipe: Int,
    isLast: Boolean = false,
    isFirst: Boolean = false,
    onPositionChange: (Int) -> Unit = {},
    recipeId: Int
) {
    var pickedType by remember(stepToEdit) { mutableStateOf(stepToEdit?.type) }
    val pickedTypeName = pickedType?.stringRes?.let { stringResource(id = it) } ?: ""
    var stepName by remember(stepToEdit, pickedTypeName) {
        mutableStateOf(TextFieldValue(stepToEdit?.name ?: pickedTypeName))
    }
    var stepTime by remember(stepToEdit) {
        mutableStateOf(((stepToEdit?.time ?: 0) / 1000).toString())
    }
    var stepValue by remember(stepToEdit) {
        mutableStateOf((stepToEdit?.value ?: 0).toString())
    }
    val nameFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val isExpanded = pickedType != null
    LaunchedEffect(key1 = isExpanded) {
        if (isExpanded) {
            onTypeSelect()
        }
    }
    fun saveStep() {
        save(
            Step(
                name = stepName.text,
                time = stepTime.safeToInt().toMillis(),
                type = pickedType ?: StepType.OTHER,
                value = if (stepValue.isNotBlank() &&
                    stepValue.toInt() != 0 &&
                    pickedType != StepType.WAIT
                ) {
                    stepValue.toInt()
                } else {
                    null
                },
                recipeId = recipeId,
                orderInRecipe = orderInRecipe,
            )
        )
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.medium)
                .animateContentSize()
        ) {
            FlowRow {
                StepType.values().forEach { stepType ->
                    Chip(
                        value = stringResource(id = stepType.stringRes),
                        onCheck = {
                            pickedType = stepType
                        },
                        isChecked = pickedType == stepType,
                        modifier = Modifier.testTag(
                            "step_type_button_${stepType.name.lowercase()}"
                        )
                    )
                }
            }
            if (isExpanded) {
                OutlinedTextField(
                    label = { Text(text = stringResource(id = R.string.step_add_name)) },
                    value = stepName,
                    singleLine = true,
                    onValueChange = { stepName = it },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                    ),
                    modifier = Modifier
                        .testTag("step_name")
                        .padding(Spacing.xSmall)
                        .focusRequester(nameFocusRequester)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    label = { Text(text = stringResource(id = R.string.step_add_duration)) },
                    value = stepTime,
                    onValueChange = { value ->
                        stepTime = ensureNumbersOnly(value) ?: stepTime
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (pickedType?.isNotWaitStepType() == true) {
                            ImeAction.Next
                        } else {
                            if (stepName.text.isNotBlank()) {
                                ImeAction.Done
                            } else {
                                ImeAction.Previous
                            }
                        }
                    ),
                    keyboardActions = KeyboardActions(
                        onPrevious = { focusManager.moveFocus(FocusDirection.Up) },
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        onDone = {
                            saveStep()
                        }
                    ),
                    modifier = Modifier
                        .testTag("step_time")
                        .padding(Spacing.xSmall)
                        .fillMaxWidth(),
                )
                if (pickedType?.isNotWaitStepType() == true) {
                    OutlinedTextField(
                        label = { Text(text = stringResource(id = R.string.step_add_weight)) },
                        value = stepValue,
                        onValueChange = { value ->
                            stepValue = ensureNumbersOnly(value) ?: stepValue
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (stepName.text.isNotBlank()) {
                                ImeAction.Done
                            } else {
                                ImeAction.Previous
                            },
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { saveStep() },
                            onPrevious = { nameFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .testTag("step_value")
                            .padding(Spacing.xSmall)
                            .fillMaxWidth(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.big),
                ) {
                    PillButton(
                        modifier = Modifier.testTag("step_save"),
                        text = stringResource(id = R.string.step_add_save),
                        imageVector = Icons.Rounded.Add,
                        enabled = stepName.text.isNotBlank(),
                        onClick = { saveStep() }
                    )
                    if (stepToEdit != null) {
                        PillButton(
                            modifier = Modifier.testTag("step_remove"),
                            text = stringResource(id = R.string.step_add_remove),
                            imageVector = Icons.Rounded.Delete,
                            onClick = { save(null) }
                        )
                    }
                }
                if (stepToEdit != null) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PillButton(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            onClick = { onPositionChange(-1) },
                            enabled = !isFirst
                        )
                        Spacer(modifier = Modifier.width(Spacing.normal))
                        PillButton(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            onClick = { onPositionChange(1) },
                            enabled = !isLast
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PillButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        enabled = enabled,
        modifier = modifier,
    ) {
        Icon(imageVector = imageVector, contentDescription = null)
        if (!text.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(text = text)
        }
    }
}

@Composable
@Preview
fun StepAddCardPreview() {
    StepAddCard(save = {}, orderInRecipe = 0, recipeId = 0, isFirst = true)
}

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