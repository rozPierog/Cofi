package com.omelan.cofi.pages

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.cofi.*
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.model.*
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.shapes
import com.omelan.cofi.ui.spacingDefault
import com.omelan.cofi.utils.Haptics
import dev.chrisbanes.accompanist.insets.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalAnimationApi
@ExperimentalMaterialApi
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
    var isAnimationRunning by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    var showAutomateLinkDialog by remember { mutableStateOf(false) }
    val steps = stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    val recipe =
        recipeViewModel.getRecipe(recipeId).observeAsState(Recipe(name = "", description = ""))
    val indexOfCurrentStep = steps.value.indexOf(currentStep)
    val indexOfLastStep = steps.value.lastIndex
    val haptics = Haptics(AmbientContext.current)
    val animatedProgressValue = animatedFloat(0f)
    val animatedProgressColor = animatedColor(Color.DarkGray)

    val clipboardManager = AmbientClipboardManager.current
    val snackbarState = SnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarMessage = stringResource(id = R.string.snackbar_copied)

    val dataStore = AmbientSettingsDataStore.current

    val combineWeightFlow = dataStore.data.map { preferences ->
        preferences[COMBINE_WEIGHT] ?: CombineWeight.WATER.name
    }
    val combineWeightState = combineWeightFlow.collectAsState(initial = CombineWeight.WATER.name)

    val combinedWeight = remember(combineWeightState.value) {
        return@remember when (combineWeightState.value) {
            CombineWeight.ALL.name -> steps.value.sumOf { it.value ?: 0 }
            CombineWeight.WATER.name -> steps.value.sumOf {
                if (it.type === StepType.WATER) {
                    it.value ?: 0
                } else {
                    0
                }
            }
            CombineWeight.NONE.name -> null
            else -> null
        }
    }

    val combinedDoneWeight = remember(combineWeightState.value, currentStep) {
        val doneSteps = if (indexOfCurrentStep == -1) {
            listOf()
        } else {
            steps.value.subList(0, indexOfCurrentStep)
        }
        return@remember when (combineWeightState.value) {
            CombineWeight.ALL.name -> doneSteps.sumOf { it.value ?: 0 }
            CombineWeight.WATER.name -> doneSteps.sumOf {
                if (it.type === StepType.WATER) {
                    it.value ?: 0
                } else {
                    0
                }
            }
            CombineWeight.NONE.name -> 0
            else -> 0
        }
    }

    fun copyAutomateLink() {
        clipboardManager.setText(AnnotatedString(text = "$appDeepLinkUrl/recipe/$recipeId"))
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
            isDone = true
            onRecipeEnd(recipe.value)
        }
        haptics.progress()
    }

    fun startAnimations() {
        if (currentStep == null) {
            return
        }
        isDone = false
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
    CofiTheme {
        Scaffold(
            snackbarHost = {
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
                        IconButton(
                            onClick = {
                                showAutomateLinkDialog = true
                            }
                        ) {
                            Icon(painterResource(id = R.drawable.ic_link))
                        }
                        IconButton(onClick = goToEdit) {
                            Icon(Icons.Rounded.Edit)
                        }
                    },
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colors.background),
                contentPadding = if (isInPiP) {
                    PaddingValues(0.dp)
                } else {
                    AmbientWindowInsets.current.navigationBars.toPaddingValues()
                        .add(start = spacingDefault, end = spacingDefault, top = spacingDefault)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isInPiP) {
                    item {
                        if (recipe.value.description.isNotBlank()) {
                            Description(
                                modifier = Modifier.fillMaxWidth(),
                                descriptionText = recipe.value.description
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(spacingDefault))

                    Timer(
                        currentStep = currentStep,
                        animatedProgressValue = animatedProgressValue,
                        animatedProgressColor = animatedProgressColor,
                        isInPiP = isInPiP,
                        combinedWeight = combinedWeight,
                        alreadyDoneWeight = combinedDoneWeight,
                        isDone = isDone,
                    )
                }
                item {
                    if (!isInPiP) {
                        Spacer(modifier = Modifier.height(spacingDefault))
                        Button(
                            modifier = Modifier.animateContentSize(),
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
                        Spacer(modifier = Modifier.height(spacingDefault))
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
            if (showAutomateLinkDialog) {
                AlertDialog(
                    onDismissRequest = { showAutomateLinkDialog = false },
                    shape = shapes.medium,
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
                            TextButton(
                                onClick = {
                                    copyAutomateLink()
                                    showAutomateLinkDialog = false
                                }
                            ) {
                                Text(text = stringResource(id = R.string.button_copy))
                            }
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.recipe_details_automation_dialog_title))
                    },
                    text = {
                        Text(text = stringResource(R.string.recipe_details_automation_dialog_text))
                    },
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreview() {
    RecipeDetails(recipeId = 1, isInPiP = false)
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalTime
@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreviewPip() {
    RecipeDetails(recipeId = 1, isInPiP = true)
}