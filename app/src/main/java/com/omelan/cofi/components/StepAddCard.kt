package com.omelan.cofi.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
    var stepName by remember(stepToEdit, pickedTypeName) {
        mutableStateOf(TextFieldValue(stepToEdit?.name ?: pickedTypeName))
    }
    var stepTime by remember(stepToEdit) {
        mutableStateOf(((stepToEdit?.time ?: 0) / 1000).toString())
    }
    var stepValue by remember(stepToEdit) {
        mutableStateOf((stepToEdit?.value ?: 0).toString())
    }
    val textFieldColors = androidx.compose.material3.MaterialTheme.createTextFieldColors()
//    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.medium)
                .animateContentSize()
        ) {
            Box {
                FlowRow {
                    StepType.values().forEach { stepType ->
                        Chip(
                            value = stringResource(id = stepType.stringRes),
                            onCheck = { pickedType = stepType },
                            isChecked = pickedType == stepType,
                            modifier = Modifier.testTag("step_type_button_${stepType.name.lowercase()}")
                        )
                    }
                }
            }
            if (pickedType != null) {
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
                        }
                    ),
                    colors = textFieldColors,
                    modifier = Modifier
                        .testTag("step_name")
                        .padding(Spacing.xSmall)
//                        .focusRequester(focusRequester)
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
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    colors = textFieldColors,
                    modifier = Modifier
                        .testTag("step_time")
                        .padding(Spacing.xSmall)
                        .fillMaxWidth(),
                )
                if (listOf(
                        StepType.WATER,
                        StepType.ADD_COFFEE,
                        StepType.OTHER,
                    ).contains(pickedType)
                ) {
                    OutlinedTextField(
                        label = { Text(text = stringResource(id = R.string.step_add_weight)) },
                        value = stepValue,
                        onValueChange = { value ->
                            stepValue = ensureNumbersOnly(value) ?: stepValue
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { saveStep() }
                        ),
                        colors = textFieldColors,
                        modifier = Modifier
                            .testTag("step_value")
                            .padding(Spacing.xSmall)
                            .fillMaxWidth(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
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
            }
        }
    }
}

@Composable
fun PillButton(
    modifier: Modifier = Modifier,
    text: String,
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
        modifier = modifier.padding(vertical = Spacing.big, horizontal = Spacing.xSmall),
    ) {
        Icon(imageVector = imageVector, contentDescription = null)
        Spacer(modifier = Modifier.width(Spacing.small))
        Text(text = text)
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