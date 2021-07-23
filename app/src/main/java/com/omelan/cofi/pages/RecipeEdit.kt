package com.omelan.cofi.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.StepAddCard
import com.omelan.cofi.components.StepListItem
import com.omelan.cofi.components.StepProgress
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.RecipeIcon
import com.omelan.cofi.model.Step
import com.omelan.cofi.ui.modal
import com.omelan.cofi.ui.shapes
import com.omelan.cofi.ui.spacingDefault
import kotlinx.coroutines.launch

@ExperimentalAnimatedInsets
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun RecipeEdit(
    saveRecipe: (Recipe, List<Step>) -> Unit,
    goBack: () -> Unit = {},
    stepsToEdit: List<Step> = listOf(),
    recipeToEdit: Recipe = Recipe(name = "", description = "", recipeIcon = RecipeIcon.Grinder),
    deleteRecipe: () -> Unit = {},
    isEditing: Boolean = false,
) {
    val showDeleteModal = remember { mutableStateOf(false) }
    val pickedIcon = remember(recipeToEdit) { mutableStateOf(recipeToEdit.recipeIcon) }
    val name = remember(recipeToEdit) { mutableStateOf(recipeToEdit.name) }
    val description = remember(recipeToEdit) {
        mutableStateOf(
            recipeToEdit.description
        )
    }
    val steps = remember(stepsToEdit) { mutableStateOf(stepsToEdit) }
    val stepWithOpenEditor = remember { mutableStateOf<Step?>(null) }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    fun pickIcon(icon: RecipeIcon) {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.collapse()
            pickedIcon.value = icon
        }
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetElevation = 30.dp,
        sheetShape = shapes.modal,
        sheetContent = {
            FlowRow(
                modifier = Modifier
                    .navigationBarsWithImePadding()
                    .fillMaxWidth(),
            ) {
                RecipeIcon.values().map {
                    IconButton(
                        onClick = { pickIcon(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.2F)
                            .aspectRatio(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = "Coffee grinder"
                        )
                    }
                }
            }
        },
        topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showDeleteModal.value = true }) {
                            Icon(Icons.Rounded.Delete, contentDescription = null)
                        }
                    }
                    IconButton(
                        modifier = Modifier.testTag("recipe_edit_save"),
                        onClick = {
                            saveRecipe(
                                recipeToEdit.copy(
                                    name = name.value,
                                    description = description.value,
                                    recipeIcon = pickedIcon.value,
                                ),
                                steps.value
                            )
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = if (isEditing) {
                            stringResource(id = R.string.recipe_edit_title)
                        } else {
                            stringResource(id = R.string.recipe_add_new_title)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }
    ) {
        BoxWithConstraints {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colors.background),
                contentPadding = PaddingValues(
                    bottom = maxHeight / 2,
                    top = spacingDefault,
                    start = spacingDefault,
                    end = spacingDefault,
                ),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                } else {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                                keyboardController?.hide()
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = pickedIcon.value.icon),
                                contentDescription = null
                            )
                        }
                        OutlinedTextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recipe_edit_name"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            label = { Text(text = stringResource(id = R.string.recipe_edit_name)) },
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacingDefault)
                            .testTag("recipe_edit_description"),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        label = {
                            Text(text = stringResource(id = R.string.recipe_edit_description))
                        },
                    )
                }
                items(steps.value) { step ->
                    AnimatedVisibility(
                        visible = stepWithOpenEditor.value == step,
                        enter = expandVertically(),
                        exit = shrinkVertically(),

                        ) {
                        val indexOfThisStep = steps.value.indexOf(step)
                        StepAddCard(
                            stepToEdit = step,
                            save = { stepToSave ->
                                if (stepToSave == null) {
                                    steps.value = steps.value.minus(step)
                                } else {
                                    steps.value =
                                        steps.value.mapIndexed { index, step ->
                                            if (index == indexOfThisStep) {
                                                stepToSave
                                            } else {
                                                step
                                            }
                                        }
                                }
                                stepWithOpenEditor.value = null
                            },
                            orderInRecipe = steps.value.indexOf(step),
                            recipeId = recipeToEdit.id,
                        )
                    }
                    AnimatedVisibility(
                        visible = stepWithOpenEditor.value != step,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        StepListItem(
                            step = step,
                            stepProgress = StepProgress.Upcoming,
                            onClick = { clickedStep ->
                                stepWithOpenEditor.value = clickedStep
                            }
                        )
                    }
                }
                if (stepWithOpenEditor.value == null) {
                    item {
                        StepAddCard(
                            save = { stepToSave ->
                                if (stepToSave != null) {
                                    steps.value = listOf(
                                        *steps.value.toTypedArray(),
                                        stepToSave
                                    )
                                }
                            },
                            orderInRecipe = steps.value.size,
                            recipeId = recipeToEdit.id,
                        )
                    }
                }
            }
        }

        if (showDeleteModal.value && isEditing) {
            AlertDialog(
                onDismissRequest = { showDeleteModal.value = false },
                shape = shapes.medium,
                buttons = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacingDefault)
                    ) {
                        TextButton(onClick = { showDeleteModal.value = false }) {
                            Text(text = stringResource(id = R.string.button_cancel))
                        }
                        TextButton(
                            onClick = {
                                deleteRecipe()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.button_delete))
                        }
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.step_delete_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.step_delete_text))
                },
            )
        }
    }
}

@ExperimentalAnimatedInsets
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview
@Composable
fun RecipeEditPreview() {
    RecipeEdit(saveRecipe = { _, _ -> })
}