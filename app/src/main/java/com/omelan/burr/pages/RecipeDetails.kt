package com.omelan.burr.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientClipboardManager
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.AmbientPiPState
import com.omelan.burr.R
import com.omelan.burr.appDeepLinkUrl
import com.omelan.burr.components.*
import com.omelan.burr.model.*
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.ui.card
import com.omelan.burr.ui.shapes
import com.omelan.burr.utils.Haptics
import dev.chrisbanes.accompanist.insets.*
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalMaterialApi
@ExperimentalTime
@Composable
fun RecipeDetails(
    recipeId: Int,
    isInPiP: Boolean = AmbientPiPState.current,
    onRecipeEnd: (Recipe) -> Unit = {},
    goToEdit: () -> Unit = {},
    goBack: () -> Unit = {},
    stepsViewModel: StepsViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val (currentStep, setCurrentStep) = remember { mutableStateOf<Step?>(null) }
    val steps = stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    val recipe =
        recipeViewModel.getRecipe(recipeId).observeAsState(Recipe(name = "", description = ""))
    val indexOfCurrentStep = steps.value.indexOf(currentStep)
    val indexOfLastStep = steps.value.lastIndex
    val haptics = Haptics(AmbientContext.current)
    val animatedProgressValue = animatedFloat(0f)
    val animatedProgressColor = animatedColor(Color.DarkGray)
    var isAnimationRunning by remember { mutableStateOf(false) }
    val clipboardManager = AmbientClipboardManager.current
    var showAutomateLinkDialog by remember { mutableStateOf(false) }
    val snackbarState = SnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarMessage = stringResource(id = R.string.snackbar_copied)
    fun copyAutomateLink() {
        clipboardManager.setText(AnnotatedString(text = "$appDeepLinkUrl/recipe/${recipeId}"))
        coroutineScope.launch {
            snackbarState.showSnackbar(message = snackbarMessage)
        }
    }
    fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
        isAnimationRunning = false
    }

    fun changeToNextStep() {
        if (indexOfCurrentStep != indexOfLastStep) {
            setCurrentStep(steps.value[indexOfCurrentStep + 1])
        } else {
            animatedProgressValue.snapTo(0f)
            setCurrentStep(null)
            isAnimationRunning = false
            onRecipeEnd(recipe.value)
        }
        haptics.progress()
    }


    fun startAnimations() {
        if (currentStep == null) {
            return
        }
        isAnimationRunning = true
        val duration = (currentStep.time - (currentStep.time * animatedProgressValue.value)).toInt()
        animatedProgressColor.animateTo(
            targetValue = currentStep.type.color,
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
        Scaffold(snackbarHost = {
            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(10.dp)
            ) {
                Snackbar {
                    Text(text = it.message)
                }
            }
        },
            topBar = {
                PiPAwareAppBar(
                    title = {
                        Text(
                            text = recipe.value.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(Icons.Rounded.ArrowBack)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showAutomateLinkDialog = true
                        }) {
                            Icon(Icons.Rounded.Share)
                        }
                        IconButton(onClick = goToEdit) {
                            Icon(Icons.Rounded.Edit)
                        }
                    },
                )
            }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colors.background),
                contentPadding = if (isInPiP) {
                    PaddingValues(0.dp)
                } else {
                    AmbientWindowInsets.current.navigationBars.toPaddingValues()
                        .add(start = 16.dp, end = 16.dp)
                }
            ) {
                item {
                    Column {
                        if (!isInPiP) {
//                            Text(
//                                text = recipe.value.name,
//                                color = MaterialTheme.colors.onSurface,
//                                style = MaterialTheme.typography.h6
//                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            if (recipe.value.description.isNotBlank()) {
                                Description(
                                    modifier = Modifier.fillMaxWidth(),
                                    descriptionText = recipe.value.description
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                        Timer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            currentStep = currentStep,
                            animatedProgressValue = animatedProgressValue,
                            animatedProgressColor = animatedProgressColor,
                            isInPiP = isInPiP,
                        )
                        if (!isInPiP) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Button(
                                modifier = Modifier
                                    .animateContentSize()
                                    .align(Alignment.CenterHorizontally),
                                onClick = if (currentStep != null) {
                                    if (isAnimationRunning) {
                                        { pauseAnimations() }
                                    } else {
                                        { startAnimations() }
                                    }
                                } else {
                                    { setCurrentStep(steps.value.first()) }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAnimationRunning) {
                                        vectorResource(id = R.drawable.ic_pause)
                                    } else {
                                        Icons.Rounded.PlayArrow
                                    }
                                )
                                Text(
                                    text = if (isAnimationRunning) {
                                        stringResource(id = R.string.recipe_details_button_pause)
                                    } else {
                                        stringResource(id = R.string.recipe_details_button_start)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                        }
                    }
                }
                if (!isInPiP) {
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
        if (showAutomateLinkDialog) {
            AlertDialog(
                onDismissRequest = { showAutomateLinkDialog = false },
                shape = shapes.card,
                buttons = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        TextButton(onClick = { showAutomateLinkDialog = false }) {
                            Text(text = stringResource(id = R.string.button_cancel))
                        }
                        TextButton(onClick = {
                            copyAutomateLink()
                            showAutomateLinkDialog = false
                        }) {
                            Text(text = stringResource(id = R.string.button_copy))
                        }

                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.recipe_details_automation_dialog_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.recipe_details_automation_dialog_text))
                },
            )
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreview() {
    RecipeDetails(recipeId = 1, isInPiP = false)
}

@ExperimentalMaterialApi
@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreviewPip() {
    RecipeDetails(recipeId = 1, isInPiP = true)
}