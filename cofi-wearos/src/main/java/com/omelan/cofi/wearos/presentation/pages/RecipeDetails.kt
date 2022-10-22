package com.omelan.cofi.wearos.presentation.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.share.Step
import com.omelan.cofi.share.StepsViewModel
import com.omelan.cofi.share.components.StepNameText
import com.omelan.cofi.share.components.TimeText
import com.omelan.cofi.share.components.TimerValue
import com.omelan.cofi.share.timer.Timer
import com.omelan.cofi.wearos.presentation.components.StartButton


@Composable
fun RecipeDetails(recipeId: Int) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val stepsViewModel: StepsViewModel = viewModel()
    val recipe by recipeViewModel.getRecipe(recipeId).observeAsState(initial = Recipe(name = ""))
    val steps by stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    RecipeDetails(recipe = recipe, steps = steps)
}

@Composable
fun RecipeDetails(recipe: Recipe, steps: List<Step>) {
    val (
        currentStep,
        isDone,
        isTimerRunning,
        _,
        animatedProgressValue,
        animatedProgressColor,
        pauseAnimations,
        progressAnimation,
        startAnimations,
        changeToNextStep,
    ) = Timer.createTimerControllers(
        steps = steps,
        onRecipeEnd = { },
        isStepChangeSoundEnabled = true,
        isStepChangeVibrationEnabled = true,
    )

    val currentStepSafe = currentStep.value
    LaunchedEffect(currentStep.value) {
        progressAnimation(Unit)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            progress = animatedProgressValue.value,
            indicatorColor = animatedProgressColor.value,
            startAngle = 300f,
            endAngle = 240f,
        )
        Column(
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .fillMaxSize()
                .animateContentSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                visible = currentStepSafe == null && !isDone,
            ) {
                Text(
                    text = recipe.name,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 2,
                    style = MaterialTheme.typography.title1,
                )
            }
            AnimatedVisibility(
                visible = currentStepSafe != null && !isDone,
            ) {
                Column {
                    if (currentStepSafe != null) {
                        TimeText(
                            currentStep = currentStepSafe,
                            animatedProgressValue = animatedProgressValue.value,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 2,
                            style = MaterialTheme.typography.title2,
                            paddingHorizontal = 2.dp,
                            showMillis = false,
                        )
                        StepNameText(
                            currentStep = currentStepSafe,
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.title3,
                            maxLines = 1,
                            paddingHorizontal = 2.dp,
                        )
                        TimerValue(
                            currentStep = currentStepSafe,
                            animatedProgressValue = animatedProgressValue.value,
                            alreadyDoneWeight = 0,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,
                            style = MaterialTheme.typography.title1,
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            StartButton(
                currentStepSafe,
                isTimerRunning,
                animatedProgressValue,
                pauseAnimations,
                changeToNextStep,
                startAnimations,
            )
        }
    }
}


@Preview
@Composable
fun TimerPreview() {
    RecipeDetails(Recipe(name = "test"), steps = emptyList())
}
