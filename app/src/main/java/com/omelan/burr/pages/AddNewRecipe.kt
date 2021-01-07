package com.omelan.burr.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.vectorResource
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
import kotlin.time.ExperimentalTime

@ExperimentalLayout
@ExperimentalTime
@Composable
fun AddNewRecipePage(
    saveRecipe: (Recipe, List<Step>) -> Unit,
    goBack: () -> Unit = {},
    stepsToEdit: List<Step> = listOf(),
    recipeToEdit: Recipe = Recipe(name = "", description = ""),
    deleteRecipe: () -> Unit = {},
) {
    val (recipeName, setRecipeName) = remember(v1 = recipeToEdit) { mutableStateOf(recipeToEdit.name) }
    val (recipeDescription, setRecipeDescription) = remember(recipeToEdit) {
        mutableStateOf(
            recipeToEdit.description
        )
    }
    val (editedSteps, setEditedSteps) = remember(stepsToEdit) { mutableStateOf(stepsToEdit) }
    val (stepCurrentlyEdited, setStepCurrentlyEdited) = remember { mutableStateOf<Step?>(null) }
    BurrTheme {
        Scaffold(topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Rounded.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = deleteRecipe) {
                        Icon(Icons.Rounded.Delete)
                    }
                    IconButton(onClick = {
                        saveRecipe(
                            recipeToEdit.copy(name = recipeName, description = recipeDescription),
                            editedSteps
                        )
                    }) {
                        Icon(vectorResource(id = R.drawable.ic_save))
                    }
                },
                title = { Text(text = "Create new recipe") })
        }) {
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
    AddNewRecipePage(saveRecipe = { _, _ -> })
}