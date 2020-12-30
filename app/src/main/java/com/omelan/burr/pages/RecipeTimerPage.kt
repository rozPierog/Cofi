package com.omelan.burr.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.components.Description
import com.omelan.burr.components.Timer
import com.omelan.burr.model.Recipe
import com.omelan.burr.model.Step
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.utils.Haptics
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun RecipeTimerPage(recipe: Recipe) {
    val (currentStep, setCurrentStep) = remember { mutableStateOf<Step?>(null) }
    val indexOfCurrentStep = recipe.steps.indexOf(currentStep)
    val indexOfLastStep = recipe.steps.lastIndex
    val haptics = Haptics(AmbientContext.current)

    val animatedProgressValue = animatedFloat(0f)
    val animatedProgressColor = animatedColor(Color.DarkGray)


    fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
    }

    fun changeToNextStep() {
        if (indexOfCurrentStep != indexOfLastStep) {
            setCurrentStep(recipe.steps[indexOfCurrentStep + 1])
        } else {
            animatedProgressValue.snapTo(0f)
            setCurrentStep(null)
        }
        haptics.tick()
    }


    fun startAnimations() {
        if (currentStep == null) {
            return
        }
        val duration = (currentStep.time - (currentStep.time * animatedProgressValue.value)).toInt()
        animatedProgressColor.animateTo(
            targetValue = currentStep.type.getColor(),
            anim = tween(durationMillis = duration, easing = LinearEasing),
        )
        animatedProgressValue.animateTo(
            targetValue = 1f,
            anim = tween(durationMillis = duration, easing = LinearEasing),
            onEnd = { _, endValue ->
                if (endValue != 1f) {
                    return@animateTo
                }
                changeToNextStep()
            }
        )
    }
    onCommit(v1 = currentStep, callback = {
        if (currentStep == null) {
            return@onCommit
        }
        animatedProgressValue.snapTo(0f)
        startAnimations()
    })
    BurrTheme {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp)
        ) {
            Text(text = recipe.name, color = Color.Black, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(15.dp))
            Description(modifier = Modifier.fillMaxWidth(), descriptionText = recipe.description)
            Spacer(modifier = Modifier.height(15.dp))
            Timer(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                currentStep = currentStep,
                animatedProgressValue = animatedProgressValue,
                animatedProgressColor = animatedProgressColor,
            )
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                modifier = Modifier.animateContentSize().align(Alignment.CenterHorizontally),
                onClick = {
                    if (currentStep != null) {
                        if (animatedProgressColor.isRunning && animatedProgressValue.isRunning) {
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
                    Row(modifier = Modifier.animateContentSize().padding(vertical = 5.dp)) {
                        val indexOfThisStep = recipe.steps.indexOf(step)
                        val isPastStep = indexOfThisStep < indexOfCurrentStep
                        val isCurrentStep = indexOfCurrentStep == indexOfThisStep
                        if (isPastStep || isCurrentStep) {
                            Icon(
                                imageVector = if (isPastStep) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.AccountCircle
                                },
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
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