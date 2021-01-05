package com.omelan.burr.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.MainActivityViewModel
import com.omelan.burr.components.Description
import com.omelan.burr.components.StepListItem
import com.omelan.burr.components.StepProgress
import com.omelan.burr.components.Timer
import com.omelan.burr.model.*
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.utils.Haptics
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun RecipeTimerPage(
    recipeId: Int,
    isInPiP: Boolean,
    stepsViewModel: StepsViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
    mainActivityViewModel: MainActivityViewModel = viewModel(),
) {
    val (currentStep, setCurrentStep) = remember { mutableStateOf<Step?>(null) }
    val steps = stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    val recipe = recipeViewModel.getRecipe(recipeId).observeAsState(null)
    val indexOfCurrentStep = steps.value.indexOf(currentStep)
    val indexOfLastStep = steps.value.lastIndex
    val haptics = Haptics(AmbientContext.current)
    val navBarHeight = mainActivityViewModel.navBarHeight.observeAsState(48.dp)
    val paddingValues = remember(navBarHeight) {
        PaddingValues(
            bottom = navBarHeight.value,
            start = 16.dp,
            end = 16.dp,
            top = 16.dp
        )
    }
    val animatedProgressValue = animatedFloat(0f)
    val animatedProgressColor = animatedColor(Color.DarkGray)

    fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
    }

    fun changeToNextStep() {
        if (indexOfCurrentStep != indexOfLastStep) {
            setCurrentStep(steps.value[indexOfCurrentStep + 1])
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
    onCommit(currentStep) {
        if (currentStep == null) {
            return@onCommit
        }
        animatedProgressValue.snapTo(0f)
        startAnimations()
    }
    BurrTheme {

        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().animateContentSize(),
            contentPadding = paddingValues
        ) {
            item {
                Column {
                    if (!isInPiP) {
                        Text(
                            text = recipe.value?.name ?: "",
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Description(
                            modifier = Modifier.fillMaxWidth(),
                            descriptionText = recipe.value?.description ?: ""
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                    Timer(
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                        currentStep = currentStep,
                        animatedProgressValue = animatedProgressValue,
                        animatedProgressColor = animatedProgressColor,
                        isInPiP = isInPiP,
                    )
                    if (!isInPiP) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Button(
                            modifier = Modifier.animateContentSize()
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                if (currentStep != null) {
                                    if (animatedProgressColor.isRunning && animatedProgressValue.isRunning) {
                                        pauseAnimations()
                                    } else {
                                        startAnimations()
                                    }
                                } else {
                                    setCurrentStep(steps.value.first())
                                }
                            },
                        ) {
                            Text(text = "Start")
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                }
            }
            items(steps.value) { step ->
                val indexOfThisStep = steps.value.indexOf(step)
                val stepProgress = when {
                    indexOfThisStep < indexOfCurrentStep -> StepProgress.Done
                    indexOfCurrentStep == indexOfThisStep -> StepProgress.Current
                    else -> StepProgress.Upcoming
                }
                StepListItem(step = step, stepProgress = stepProgress)
                Divider(color = Color(0xFFE8EAF6))
            }
        }
    }
}


@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeTimerPagePreview() {
    RecipeTimerPage(recipeId = 1, isInPiP = false)
}