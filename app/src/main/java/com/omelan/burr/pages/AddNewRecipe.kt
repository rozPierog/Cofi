package com.omelan.burr.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R
import com.omelan.burr.components.StepAddCard
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.model.Step
import com.omelan.burr.ui.BurrTheme
import kotlin.time.ExperimentalTime

@ExperimentalLayout
@ExperimentalTime
@Composable
fun AddNewRecipePage(steps: List<Step> = listOf(), openStepEdit: (Step?) -> Unit) {
    val (recipeName, setRecipeName) = remember { mutableStateOf("") }
    val (recipeDescription, setRecipeDescription) = remember { mutableStateOf("") }
    val (editedSteps, setEditedSteps) = remember { mutableStateOf(steps) }
    BurrTheme {
        ScrollableColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Column(modifier = Modifier.padding(16.dp).animateContentSize()) {
                OutlinedTextField(
                    value = recipeName,
                    onValueChange = { setRecipeName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(text = "Name") },
                )
                OutlinedTextField(
                    value = recipeDescription,
                    onValueChange = { setRecipeDescription(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Description") },
                )
                editedSteps.forEach { step ->
                    StepListItem(step = step, stepProgress = StepProgress.Upcoming)
                }
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

@ExperimentalLayout
@ExperimentalTime
@Preview
@Composable
fun AddNewRecipePreview() {
    AddNewRecipePage(openStepEdit = {})
}