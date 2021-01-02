package com.omelan.burr.pages

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.MainActivity
import com.omelan.burr.R
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.model.Step
import com.omelan.burr.ui.BurrTheme
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun AddNewRecipePage(steps: List<Step> = listOf(), openStepEdit: (Step?) -> Unit) {
    val (recipeName, setRecipeName) = remember { mutableStateOf("") }
    val (recipeDescription, setRecipeDescription) = remember { mutableStateOf("") }
    BurrTheme {
        ScrollableColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = recipeName,
                    onValueChange = { setRecipeName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(text = "Name") },
                    backgroundColor = colorResource(id = R.color.navigationBar)
                )
                TextField(
                    value = recipeDescription,
                    onValueChange = { setRecipeDescription(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Description") },
                    backgroundColor = colorResource(id = R.color.navigationBar)
                )
                steps.forEach { step ->
                    StepListItem(step = step, stepProgress = StepProgress.Upcoming)
                }
                Button(onClick = {
                    openStepEdit(null)
                }) {
                    Text(text = "Add next step")
                }
            }
        }
    }
}

@ExperimentalTime
@Preview
@Composable
fun AddNewRecipePreview() {
    AddNewRecipePage(openStepEdit = {})
}