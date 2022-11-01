@file:OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationGraphicsApi::class, ExperimentalMaterialApi::class,
)

package com.omelan.cofi.pages.details

import android.app.Activity
import android.media.MediaPlayer
import android.os.Build
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omelan.cofi.*
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.model.*
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.Haptics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RecipeDetails(
    recipeId: Int,
    isInPiP: Boolean = LocalPiPState.current,
    onRecipeEnd: (Recipe) -> Unit = {},
    goToEdit: () -> Unit = {},
    goBack: () -> Unit = {},
    onTimerRunning: (Boolean) -> Unit = { },
    stepsViewModel: StepsViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1920.dp, 1080.dp)),
) {
    val steps by stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    val recipe by recipeViewModel.getRecipe(recipeId)
        .observeAsState(Recipe(name = "", description = ""))
    RecipeDetails(
        recipe,
        steps,
        isInPiP,
        onRecipeEnd,
        goToEdit,
        goBack,
        onTimerRunning,
        windowSizeClass,
    )
}

@Composable
fun RecipeDetails(
    recipe: Recipe,
    steps: List<Step>,
    isInPiP: Boolean = LocalPiPState.current,
    onRecipeEnd: (Recipe) -> Unit = {},
    goToEdit: () -> Unit = {},
    goBack: () -> Unit = {},
    onTimerRunning: (Boolean) -> Unit = { },
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1920.dp, 1080.dp)),
) {
    val recipeId by remember(recipe) {
        derivedStateOf { recipe.id }
    }

    var currentStep by remember { mutableStateOf<Step?>(null) }
    var isDone by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showAutomateLinkDialog by remember { mutableStateOf(false) }

    val weightMultiplier = remember { mutableStateOf(1.0f) }
    val timeMultiplier = remember { mutableStateOf(1.0f) }

    val ratioBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val indexOfCurrentStep = steps.indexOf(currentStep)
    val indexOfLastStep = steps.lastIndex

    val coroutineScope = rememberCoroutineScope()
    val animatedProgressValue = remember { Animatable(0f) }
    val isDarkMode = isSystemInDarkTheme()
    val animatedProgressColor =
        remember { Animatable(if (isDarkMode) Color.LightGray else Color.DarkGray) }
    val snackbarState = SnackbarHostState()
    val lazyListState = rememberLazyListState()
    val (appBarBehavior, collapse) = createAppBarBehaviorWithCollapse()

    val dataStore = DataStore(LocalContext.current)
    val isStepChangeSoundEnabled by dataStore.getStepChangeSoundSetting()
        .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
    val isStepChangeVibrationEnabled by dataStore.getStepChangeVibrationSetting()
        .collectAsState(initial = STEP_VIBRATION_DEFAULT_VALUE)
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)

    val copyAutomateLink = rememberCopyAutomateLink(snackbarState, recipeId)

    val alreadyDoneWeight = remember(combineWeightState, currentStep) {
        derivedStateOf {
            val doneSteps = if (indexOfCurrentStep == -1) {
                listOf()
            } else {
                steps.subList(0, indexOfCurrentStep)
            }
            when (combineWeightState) {
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
    }

    suspend fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
        isTimerRunning = false
    }

    val context = LocalContext.current
    val haptics = remember { Haptics(context) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.ding) }

    suspend fun changeToNextStep(silent: Boolean = false) {
        animatedProgressValue.snapTo(0f)
        if (indexOfCurrentStep != indexOfLastStep) {
            currentStep = steps[indexOfCurrentStep + 1]
        } else {
            currentStep = null
            isTimerRunning = false
            isDone = true
            onRecipeEnd(recipe)
        }
        if (silent) {
            return
        }
        if (isStepChangeSoundEnabled) {
            mediaPlayer.start()
        }
        if (isStepChangeVibrationEnabled) {
            haptics.progress()
        }
    }

    suspend fun progressAnimation() {
        val safeCurrentStep = currentStep ?: return
        isDone = false
        isTimerRunning = true
        val currentStepTime =
            if (safeCurrentStep.time != null) safeCurrentStep.time * timeMultiplier.value else null
        if (currentStepTime == null) {
            animatedProgressValue.snapTo(1f)
            isTimerRunning = false
            return
        }
        val duration =
            (currentStepTime - (currentStepTime * animatedProgressValue.value)).toInt()
        coroutineScope.launch(Dispatchers.Default) {
            withContext(
                object : MotionDurationScale {
                    override val scaleFactor: Float = 1f
                },
            ) {
                animatedProgressColor.animateTo(
                    targetValue = if (isDarkMode) {
                        safeCurrentStep.type.colorNight
                    } else {
                        safeCurrentStep.type.color
                    },
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                )
            }
        }
        withContext(
            object : MotionDurationScale {
                override val scaleFactor: Float = 1f
            },
        ) {
            val result = animatedProgressValue.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            )
            if (result.endReason != AnimationEndReason.Finished) {
                return@withContext
            }
            changeToNextStep()
        }
    }

    suspend fun startAnimations() {
        coroutineScope.launch {
            progressAnimation()
        }
    }
    LaunchedEffect(currentStep) {
        progressAnimation()
    }
    LaunchedEffect(isTimerRunning) {
        onTimerRunning(isTimerRunning)
    }
    DisposableEffect(true) {
        onDispose {
            onTimerRunning(false)
        }
    }

    suspend fun startRecipe() = coroutineScope.launch {
        collapse()
        launch {
            lazyListState.animateScrollToItem(if (recipe.description.isNotBlank()) 1 else 0)
        }
        launch {
            changeToNextStep(silent = true)
        }
    }

    val isPhoneLayout = rememberIsPhoneLayout(windowSizeClass)
    val renderDescription: @Composable (() -> Unit)? = if (recipe.description.isBlank()) {
        null
    } else {
        {
            Description(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipe_description"),
                descriptionText = recipe.description,
            )
            Spacer(modifier = Modifier.height(Spacing.big))
        }
    }
    val activity = LocalContext.current as Activity
    val renderTimer: @Composable (Modifier) -> Unit = {
        Timer(
            modifier = it
                .testTag("recipe_timer")
                .onGloballyPositioned { coordinates ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        coroutineScope.launch(Dispatchers.IO) {
                            setPiPSettings(
                                activity,
                                isTimerRunning,
                                coordinates
                                    .boundsInWindow()
                                    .toAndroidRect(),
                            )
                        }
                    }
                },
            currentStep = currentStep,
            allSteps = steps,
            animatedProgressValue = animatedProgressValue,
            animatedProgressColor = animatedProgressColor,
            isInPiP = isInPiP,
            alreadyDoneWeight = alreadyDoneWeight.value,
            isDone = isDone,
            weightMultiplier = weightMultiplier.value,
            timeMultiplier = timeMultiplier.value,
        )
        if (!isInPiP) {
            Spacer(modifier = Modifier.height(Spacing.big))
        }
    }
    val getCurrentStepProgress: (Int) -> StepProgress = { index ->
        when {
            index < indexOfCurrentStep -> StepProgress.Done
            indexOfCurrentStep == index -> StepProgress.Current
            else -> StepProgress.Upcoming
        }
    }
    val renderSteps: LazyListScope.() -> Unit = {
        itemsIndexed(items = steps, key = { _, step -> step.id }) { index, step ->
            StepListItem(
                modifier = Modifier.testTag("recipe_step"),
                step = step,
                stepProgress = getCurrentStepProgress(index),
                onClick = { newStep: Step ->
                    coroutineScope.launch {
                        if (newStep == currentStep) {
                            return@launch
                        }
                        animatedProgressValue.snapTo(0f)
                        currentStep = newStep
                    }
                },
            )
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }

    RatioBottomSheet(
        timeMultiplier = timeMultiplier,
        weightMultiplier = weightMultiplier,
        sheetState = ratioBottomSheetState,
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarState,
                    modifier = Modifier.padding(Spacing.medium),
                ) {
                    Snackbar(shape = RoundedCornerShape(50)) {
                        Text(text = it.visuals.message)
                    }
                }
            },
            topBar = {
                PiPAwareAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = recipe.recipeIcon.icon),
                                contentDescription = null,
                                modifier = Modifier.padding(end = Spacing.small),
                            )
                            Text(
                                text = recipe.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    ratioBottomSheetState.show()
                                }
                            },
                        ) {
                            Icon(
                                Icons.Rounded.ExitToApp,
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = { showAutomateLinkDialog = true }) {
                            Icon(
                                painterResource(id = R.drawable.ic_link),
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = goToEdit) {
                            Icon(
                                painterResource(id = R.drawable.ic_edit),
                                contentDescription = null,
                            )
                        }
                    },
                    scrollBehavior = appBarBehavior,
                )
            },
            floatingActionButton = {
                if (!isInPiP) {
                    StartFAB(
                        isTimerRunning = isTimerRunning,
                        onClick = {
                            if (currentStep != null) {
                                if (animatedProgressValue.isRunning) {
                                    coroutineScope.launch { pauseAnimations() }
                                } else {
                                    coroutineScope.launch {
                                        if (currentStep?.time == null) {
                                            changeToNextStep()
                                        } else {
                                            startAnimations()
                                        }
                                    }
                                }
                                return@StartFAB
                            }
                            coroutineScope.launch { startRecipe() }
                        },
                    )
                }
            },
            floatingActionButtonPosition = if (isPhoneLayout) FabPosition.Center else FabPosition.End,
        ) {
            if (isPhoneLayout) {
                PhoneLayout(it, renderDescription, renderTimer, renderSteps, isInPiP, lazyListState)
            } else {
                TabletLayout(it, renderDescription, renderTimer, renderSteps, isInPiP)
            }
        }
    }

    if (showAutomateLinkDialog) {
        DirectLinkDialog(
            dismiss = { showAutomateLinkDialog = false },
            onConfirm = {
                copyAutomateLink()
                showAutomateLinkDialog = false
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreview() {
    RecipeDetails(
        recipeId = 1,
        isInPiP = false,
    )
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreviewPip() {
    RecipeDetails(
        recipeId = 1,
        isInPiP = true,
    )
}
