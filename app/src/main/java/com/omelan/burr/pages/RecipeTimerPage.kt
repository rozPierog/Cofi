package com.omelan.burr.pages

import android.util.Log
import androidx.compose.animation.animate
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.*
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
    val (currentStep, setCurrentStep) = remember { mutableStateOf<Step?>(null) }
    val indexOfCurrentStep = recipe.steps.indexOf(currentStep)
    val indexOfLastStep = recipe.steps.lastIndex
    val animatedProgress = animatedFloat(0f)
    val animatedColor = animatedColor(Color.DarkGray)
    onCommit(v1 = indexOfCurrentStep, callback = {
        if (currentStep == null) {
            return@onCommit
        }
        animatedProgress.snapTo(0f)
        animatedColor.snapTo(Color.DarkGray)
        animatedColor.animateTo(
            targetValue = currentStep.type.getColor(),
            anim = tween(durationMillis = currentStep.time, easing = LinearEasing),
        )
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = tween(durationMillis = currentStep.time, easing = LinearEasing),
            onEnd = { _, _ ->
                if (indexOfCurrentStep != indexOfLastStep) {
                    setCurrentStep(recipe.steps[indexOfCurrentStep + 1])
                } else {
                    animatedProgress.snapTo(0f)
                    setCurrentStep(null)
                }
            }
        ) })


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
                progress = animatedProgress.value,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
                color = animatedColor.value,
                strokeWidth = 25.dp
            )
            Button(
                onClick = {
                    setCurrentStep(recipe.steps.first())
                },
            ) {
                Text(text = "Click Me ${animatedProgress.value}")
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