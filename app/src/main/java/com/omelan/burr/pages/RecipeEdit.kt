package com.omelan.burr.pages

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R
import com.omelan.burr.components.PiPAwareAppBar
import com.omelan.burr.components.StepAddCard
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.model.Recipe
import com.omelan.burr.model.Step
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.ui.card
import com.omelan.burr.ui.shapes
import kotlin.time.ExperimentalTime

@ExperimentalLayout
@ExperimentalTime
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
    BurrTheme {
        Scaffold(topBar = {
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
                    IconButton(onClick = {
                        saveRecipe(
                            recipeToEdit.copy(
                                name = name.value,
                                description = description.value
                            ),
                            steps.value
                        )
                    }) {
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
                })
        }) {
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
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text(text = stringResource(id = R.string.recipe_edit_name)) },
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description.value,
                            onValueChange = { description.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.recipe_edit_description))
                            },
                        )
                    }
                    items(steps.value) { step ->
                        if (stepWithOpenEditor.value == step) {
                            val indexOfThisStep = steps.value.indexOf(step)
                            StepAddCard(stepToEdit = step,
                                save = { stepToSave ->
                                    steps.value =
                                        steps.value.mapIndexed { index, step ->
                                            if (index == indexOfThisStep) {
                                                stepToSave
                                            } else {
                                                step
                                            }
                                        }

                                })
                        } else {
                            StepListItem(
                                step = step,
                                stepProgress = StepProgress.Upcoming,
                                onClick = { clickedStep ->
                                    stepWithOpenEditor.value = clickedStep
                                })
                        }
                    }
                    if (stepWithOpenEditor.value == null) {
                        item {
                            StepAddCard(save = { stepToSave ->
                                steps.value = listOf(
                                    *steps.value.toTypedArray(),
                                    stepToSave
                                )

                            })
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
                            TextButton(onClick = {
                                deleteRecipe()
                            }) {
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

@ExperimentalLayout
@ExperimentalTime
@Preview
@Composable
fun RecipeEditPreview() {
    RecipeEdit(saveRecipe = { _, _ -> })
}