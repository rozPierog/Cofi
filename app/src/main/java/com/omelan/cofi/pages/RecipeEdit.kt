package com.omelan.cofi.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.StepAddCard
import com.omelan.cofi.components.StepListItem
import com.omelan.cofi.components.StepProgress
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.Step
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.card
import com.omelan.cofi.ui.shapes

@ExperimentalAnimationApi
@ExperimentalLayout
@Composable
fun RecipeEdit(
    saveRecipe: (Recipe, List<Step>) -> Unit,
    goBack: () -> Unit = {},
    stepsToEdit: List<Step> = listOf(),
    recipeToEdit: Recipe = Recipe(name = "", description = ""),
    deleteRecipe: () -> Unit = {},
    isEditing: Boolean = false,
) {
    val showDeleteModal = remember { mutableStateOf(false) }
    val name = remember(recipeToEdit) { mutableStateOf(recipeToEdit.name) }
    val description = remember(recipeToEdit) {
        mutableStateOf(
            recipeToEdit.description
        )
    }
    val steps = remember(stepsToEdit) { mutableStateOf(stepsToEdit) }
    val stepWithOpenEditor = remember { mutableStateOf<Step?>(null) }
    CofiTheme {
        Scaffold(
            topBar = {
                PiPAwareAppBar(
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(Icons.Rounded.ArrowBack)
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = { showDeleteModal.value = true }) {
                                Icon(Icons.Rounded.Delete)
                            }
                        }
                        IconButton(
                            modifier = Modifier.testTag("recipe_edit_save"),
                            onClick = {
                                saveRecipe(
                                    recipeToEdit.copy(
                                        name = name.value,
                                        description = description.value
                                    ),
                                    steps.value
                                )
                            }
                        ) {
                            Icon(vectorResource(id = R.drawable.ic_save))
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
            WithConstraints {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colors.background),
                    contentPadding = PaddingValues(
                        bottom = maxHeight / 2,
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                ) {
                    item {
                        OutlinedTextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recipe_edit_name"),
                            singleLine = true,
                            label = { Text(text = stringResource(id = R.string.recipe_edit_name)) },
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description.value,
                            onValueChange = { description.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp)
                                .testTag("recipe_edit_description"),
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
                    shape = shapes.card,
                    buttons = {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
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
}

@ExperimentalAnimationApi
@ExperimentalLayout
@Preview
@Composable
fun RecipeEditPreview() {
    RecipeEdit(saveRecipe = { _, _ -> })
}