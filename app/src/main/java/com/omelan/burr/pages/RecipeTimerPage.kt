package com.omelan.burr.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
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
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
@Composable
fun RecipeTimerPage(recipe: Recipe) {
    val (currentStep, setCurrentStep) = remember { mutableStateOf<Step?>(null) }
    val indexOfCurrentStep = recipe.steps.indexOf(currentStep)
    val indexOfLastStep = recipe.steps.lastIndex
    val animatedProgress = animatedFloat(0f)
    val animatedColor = animatedColor(Color.DarkGray)

    fun pauseAnimations() {
        animatedColor.stop()
        animatedProgress.stop()
    }

    fun startAnimations() {
        if (currentStep == null) {
            return
        }
        val duration = (currentStep.time - (currentStep.time * animatedProgress.value)).toInt()
        animatedColor.animateTo(
            targetValue = currentStep.type.getColor(),
            anim = tween(durationMillis = duration, easing = LinearEasing),
        )
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = tween(durationMillis = duration, easing = LinearEasing),
            onEnd = { _, endValue ->
                if (endValue != 1f) {
                    return@animateTo
                }
                if (indexOfCurrentStep != indexOfLastStep) {
                    setCurrentStep(recipe.steps[indexOfCurrentStep + 1])
                } else {
                    animatedProgress.snapTo(0f)
                    setCurrentStep(null)
                }
            }
        )
    }
    onCommit(v1 = indexOfCurrentStep, callback = {
        if (currentStep == null) {
            return@onCommit
        }
        animatedProgress.snapTo(0f)
        animatedColor.snapTo(Color.DarkGray)
        startAnimations()
    })


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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress.value,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    color = animatedColor.value,
                    strokeWidth = 25.dp
                )
                Column(modifier = Modifier.padding(25.dp).animateContentSize()) {

                    if (currentStep != null) {
                        val duration = (currentStep.time * animatedProgress.value).toInt()
                            .toDuration(DurationUnit.MILLISECONDS)
                        Text(
                            text = "${
                                duration.inMinutes.toInt()
                            }:${
                                duration.inSeconds.toInt()
                            }:${
                                duration.inMilliseconds.toInt()
                            }",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            )
                        )
                        Divider(color = Color.Black)
                        Text(
                            text = currentStep.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            )
                        )
                        currentStep.value?.let {
                            val currentValueFromProgress =
                                (currentStep.value * animatedProgress.value).toInt()
                            Divider(color = Color.Black)
                            Text(
                                text = "${currentValueFromProgress}g/${it}g",
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            )
                        }

                    }
                }
            }

            Button(
                modifier = Modifier.animateContentSize(),
                onClick = {
                    if (currentStep != null) {
                        if (animatedColor.isRunning && animatedProgress.isRunning) {
                            pauseAnimations()
                        } else {
                            startAnimations()
                        }
                    } else {
                        setCurrentStep(recipe.steps.first())
                    }
                },
            ) {
                Text(text = "Start")
            }
            Spacer(modifier = Modifier.height(25.dp))

            ScrollableColumn {
                recipe.steps.forEach { step ->
                    Row(modifier = Modifier.animateContentSize()) {
                        val indexOfThisStep = recipe.steps.indexOf(step)
                        if (indexOfThisStep < indexOfCurrentStep) {
                            Icon(Icons.Rounded.CheckCircle)
                        } else if (indexOfCurrentStep == indexOfThisStep) {
                            Icon(Icons.Rounded.AccountCircle)
                        }
                        Text(text = step.name, style = MaterialTheme.typography.subtitle1)

                    }
                    Divider(color = Color.Black)
                }
            }
        }
    }


}

@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeTimerPagePreview() {
    RecipeTimerPage(Recipe(id = "1", name = "V60", description = "Ble ble"))
}