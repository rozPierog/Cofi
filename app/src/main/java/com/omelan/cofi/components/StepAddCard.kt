@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.omelan.cofi.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.safeToInt
import com.omelan.cofi.share.utils.toMillis
import com.omelan.cofi.share.utils.toStringDuration
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.requestFocusSafer
import kotlinx.coroutines.launch

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
    recipeId: Int,
) {
    var pickedType by remember(stepToEdit) { mutableStateOf(stepToEdit?.type) }
    val pickedTypeName = pickedType?.stringRes?.let { stringResource(id = it) } ?: ""
    var stepName by remember(stepToEdit, pickedTypeName) {
        val name = stepToEdit?.name ?: pickedTypeName
        mutableStateOf(TextFieldValue(name, TextRange(name.length)))
    }
    var stepTime by remember(stepToEdit) {
        if (stepToEdit == null) {
            return@remember mutableStateOf("")
        }
        val stepToEditTime = stepToEdit.time
        if (stepToEditTime == null) {
            return@remember mutableStateOf("")
        } else {
            return@remember mutableStateOf((stepToEditTime / 1000).toString())
        }
    }
    var stepValue by remember(stepToEdit) {
        mutableStateOf((stepToEdit?.value ?: 0).toString())
    }
    var timeExplainerIsOpen by remember {
        mutableStateOf(false)
    }
    val dismissTimeExplainer: () -> Unit = {
        timeExplainerIsOpen = false
    }
    val nameFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val isExpanded = pickedType != null
    val coroutineScope = rememberCoroutineScope()
    fun saveStep() {
        focusManager.clearFocus()
        save(
            Step(
                name = stepName.text,
                time = if (stepTime.isBlank()) null else stepTime.safeToInt().toMillis(),
                type = pickedType ?: StepType.OTHER,
                value = if (stepValue.isNotBlank() &&
                    stepValue.toFloatOrNull() != 0f &&
                    pickedType != StepType.WAIT
                ) {
                    stepValue.toFloatOrNull()
                } else {
                    null
                },
                recipeId = recipeId,
                orderInRecipe = orderInRecipe,
            ),
        )
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.medium)
                .animateContentSize(),
        ) {
            FlowRow {
                StepType.entries.forEach { stepType ->
                    Chip(
                        value = stringResource(id = stepType.stringRes),
                        onCheck = {
                            pickedType = stepType
                        },
                        isChecked = pickedType == stepType,
                        modifier = Modifier.testTag(
                            "step_type_button_${stepType.name.lowercase()}",
                        ),
                    )
                }
            }
            if (isExpanded) {
                OutlinedTextField(
                    label = { Text(text = stringResource(id = R.string.step_add_name)) },
                    value = stepName,
                    isError = stepName.text.isBlank(),
                    singleLine = true,
                    onValueChange = { stepName = it },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
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
                        .onFocusEvent {
                            if (it.isFocused) {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                        .fillMaxWidth(),
                )
                OutlinedNumbersField(
                    label = { Text(text = stringResource(id = R.string.step_add_duration)) },
                    value = stepTime,
                    allowFloat = false,
                    onValueChange = { value ->
                        stepTime = value
                    },
                    supportingText = {
                        val duration = (stepTime.toIntOrNull()?.times(1000))
                        if (duration != null) {
                            Text(text = duration.toStringDuration())
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { timeExplainerIsOpen = true }) {
                            Icon(Icons.Rounded.Info, contentDescription = "")
                        }
                    },
                    imeAction = when {
                        pickedType?.isNotWaitStepType() == true -> ImeAction.Next
                        stepName.text.isNotBlank() -> ImeAction.Done
                        else -> ImeAction.Previous
                    },
                    keyboardActions = KeyboardActions(
                        onPrevious = { focusManager.moveFocus(FocusDirection.Up) },
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        onDone = {
                            saveStep()
                        },
                    ),
                    modifier = Modifier
                        .testTag("step_time")
                        .padding(Spacing.xSmall)
                        .animateContentSize()
                        .fillMaxWidth(),
                )
                AnimatedVisibility(visible = pickedType?.isNotWaitStepType() == true) {
                    OutlinedNumbersField(
                        label = { Text(text = stringResource(id = R.string.step_add_weight)) },
                        value = stepValue,
                        onValueChange = { value ->
                            stepValue = value
                        },
                        imeAction = if (stepName.text.isNotBlank()) {
                            ImeAction.Done
                        } else {
                            ImeAction.Previous
                        },
                        keyboardActions = KeyboardActions(
                            onDone = { saveStep() },
                            onPrevious = {
                                coroutineScope.launch {
                                    nameFocusRequester.requestFocusSafer()
                                }
                            },
                        ),
                        modifier = Modifier
                            .testTag("step_value")
                            .padding(Spacing.xSmall)
                            .fillMaxWidth(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.big),
                ) {
                    if (stepToEdit != null) {
                        OutlinedButton(
                            onClick = { save(null) },
                            modifier = Modifier.testTag("step_remove"),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = null,
                            )
                            Text(stringResource(id = R.string.step_add_remove))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    PillButton(
                        modifier = Modifier.testTag("step_save"),
                        text = stringResource(id = R.string.step_add_save),
                        painter = rememberVectorPainter(Icons.Rounded.Done),
                        enabled = stepName.text.isNotBlank(),
                        onClick = { saveStep() },
                    )
                }
                if (stepToEdit != null) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        PillButton(
                            painter = rememberVectorPainter(Icons.Rounded.KeyboardArrowUp),
                            onClick = {
                                onPositionChange(-1)
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            },
                            enabled = !isFirst,
                        )
                        Spacer(modifier = Modifier.width(Spacing.normal))
                        PillButton(
                            painter = rememberVectorPainter(Icons.Rounded.KeyboardArrowDown),
                            onClick = {
                                onPositionChange(1)
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            },
                            enabled = !isLast,
                        )
                    }
                }
            }
        }
    }
    if (timeExplainerIsOpen) {
        AlertDialog(
            onDismissRequest = dismissTimeExplainer,
            confirmButton = {
                TextButton(onClick = dismissTimeExplainer) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            icon = {
                Icon(
                    painterResource(StepType.WAIT.iconRes),
                    contentDescription = null,
                )
            },
            title = {
                Text(text = stringResource(id = R.string.step_add_duration))
            },
            text = {
                Text(text = stringResource(id = R.string.step_add_duration_explainer))
            },
        )
    }
    LaunchedEffect(key1 = isExpanded) {
        if (isExpanded) {
            onTypeSelect()
            nameFocusRequester.requestFocusSafer()
        }
    }
}

@Composable
fun PillButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    painter: Painter,
    onClick: () -> Unit,
) {
    if (!text.isNullOrBlank()) {
        FilledTonalButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
        ) {
            Icon(painter = painter, contentDescription = null, modifier = Modifier.size(22.dp))
            if (text.isNotBlank()) {
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(text = text)
            }
        }
    } else {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
        ) {
            Icon(painter = painter, contentDescription = null, modifier = Modifier.size(22.dp))
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
            orderInRecipe = 0,
            value = null,
        ),
    )
}
