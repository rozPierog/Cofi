package com.omelan.burr.pages

import androidx.compose.animation.animate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.model.Recipe
import com.omelan.burr.model.Step
import com.omelan.burr.ui.BurrTheme

@Composable
fun RecipeTimerPage(recipe: Recipe) {
    val (progress, setProgress) = remember { mutableStateOf(0.0f) }
    val (currentStep, setCurrentStep) = remember { mutableStateOf(recipe.steps[0]) }
    val (progressColor, setProgressColor) = remember { mutableStateOf(Color.DarkGray) }
    val indexOfCurrentStep = recipe.steps.indexOf(currentStep)
    val indexOfLastStep = recipe.steps.lastIndex
    val animatedProgress = animate(target = progress)
    val animatedColor = animate(target = progressColor)
    BurrTheme {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp)
        ) {
            Text(text = recipe.name, color = Color.Black, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = recipe.description,
                color = Color.Black,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(15.dp))
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
                color = animatedColor,
                strokeWidth = 25.dp
            )
            Button(
                onClick = {
                    if (progress <= 1f) {
                        setProgress(progress + 0.1f)
                        if (progress >= 0.5f) {
                            setProgressColor(Color.LightGray)
                        }
                        if (progress >= 0.8f) {
                            setProgressColor(Color.Green)
                        }
                    } else {
                        setProgress(0f)
                        setProgressColor(Color.DarkGray)
                        if (indexOfCurrentStep != indexOfLastStep) {
                            setCurrentStep(recipe.steps[indexOfCurrentStep + 1])
                        }
                    }
                },
            ) {
                Text(text = "Click Me $progress")
            }
            Spacer(modifier = Modifier.height(25.dp))

            LazyColumn {
                items(recipe.steps, itemContent = { step ->
                    Row(modifier = Modifier.animateContentSize()) {
                        val indexOfThisStep = recipe.steps.indexOf(step)
                        if (indexOfThisStep < indexOfCurrentStep) {
                            Icon(Icons.Rounded.CheckCircle)
                        } else if ( indexOfCurrentStep == indexOfThisStep) {
                            Icon(Icons.Rounded.AccountCircle)
                        }
                        Text(text = step.name, style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(1.dp))
                    }
                })
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun RecipeTimerPagePreview() {
    RecipeTimerPage(Recipe(id="1", name = "V60", description = "Ble ble"))
}