package com.omelan.burr.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.components.StepAddCard
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.model.Recipe
import com.omelan.burr.model.Step
import com.omelan.burr.model.dummySteps
import com.omelan.burr.ui.BurrTheme
import kotlin.time.ExperimentalTime

@ExperimentalLayout
@ExperimentalTime
@Composable
fun AddNewRecipePage(steps: List<Step> = listOf(), saveRecipe: (Recipe, List<Step>) -> Unit) {
    val (recipeName, setRecipeName) = remember { mutableStateOf("") }
    val (recipeDescription, setRecipeDescription) = remember { mutableStateOf("") }
    val (editedSteps, setEditedSteps) = remember { mutableStateOf(steps) }
    val (stepCurrentlyEdited, setStepCurrentlyEdited) = remember { mutableStateOf<Step?>(null) }
    BurrTheme {
        WithConstraints {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
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
                        value = recipeName,
                        onValueChange = { setRecipeName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(text = "Name") },
                    )
                }
                item {
                    OutlinedTextField(
                        value = recipeDescription,
                        onValueChange = { setRecipeDescription(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Description") },
                    )
                }
                items(editedSteps) { step ->
                    if (stepCurrentlyEdited == step) {
                        val indexOfThisStep = editedSteps.indexOf(step)
                        StepAddCard(stepToEdit = step,
                            save = { stepToSave ->
                                setEditedSteps(
                                    editedSteps.mapIndexed { index, step ->
                                        if (index == indexOfThisStep) {
                                            stepToSave
                                        } else {
                                            step
                                        }
                                    }
                                )
                            })
                    } else {
                        StepListItem(
                            step = step,
                            stepProgress = StepProgress.Upcoming,
                            onClick = { clickedStep ->
                                setStepCurrentlyEdited(clickedStep)
                            })
                    }
                }
                if (stepCurrentlyEdited == null) {
                    item {
                        StepAddCard(save = { stepToSave ->
                            setEditedSteps(
                                listOf(
                                    *editedSteps.toTypedArray(),
                                    stepToSave
                                )
                            )
                        })
                    }
                }
                item {
                    Button(
                        onClick = {
                            saveRecipe(
                                Recipe(
                                    name = recipeName,
                                    description = recipeDescription,
                                ),
                                editedSteps
                            )
                        },
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(text = "Save this recipe")
                    }
                }

            }
        }
    }
}

@ExperimentalLayout
@ExperimentalTime
@Preview
@Composable
fun AddNewRecipePreview() {
    AddNewRecipePage(steps = dummySteps, saveRecipe = { _, _ -> })
}