@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages

import android.graphics.Rect
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.omelan.cofi.utils.FabType
import com.omelan.cofi.utils.Haptics
import com.omelan.cofi.utils.getDefaultPadding
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
    onTimerRunning: (Boolean, Rect?) -> Unit = { _, _ -> },
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
        windowSizeClass
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
    onTimerRunning: (Boolean, Rect?) -> Unit = { _, _ -> },
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1920.dp, 1080.dp)),
) {
    val recipeId by remember(recipe) {
        derivedStateOf { recipe.id }
    }

    var currentStep by remember { mutableStateOf<Step?>(null) }
    var isDone by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showAutomateLinkDialog by remember { mutableStateOf(false) }
    var timerRect by remember { mutableStateOf<Rect?>(null) }

    val indexOfCurrentStep = steps.indexOf(currentStep)
    val indexOfLastStep = steps.lastIndex

    val coroutineScope = rememberCoroutineScope()
    val animatedProgressValue = remember { Animatable(0f) }
    val isDarkMode = isSystemInDarkTheme()
    val animatedProgressColor =
        remember { Animatable(if (isDarkMode) Color.LightGray else Color.DarkGray) }
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = SnackbarHostState()
    val snackbarMessage = stringResource(id = R.string.snackbar_copied)
    val lazyListState = rememberLazyListState()
    val (appBarBehavior, collapse) = createAppBarBehaviorWithCollapse()

    val dataStore = DataStore(LocalContext.current)
    val isDingEnabled by dataStore.getStepChangeSetting()
        .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)

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

    fun copyAutomateLink() {
        clipboardManager.setText(AnnotatedString(text = "$appDeepLinkUrl/recipe/$recipeId"))
        coroutineScope.launch {
            snackbarState.showSnackbar(message = snackbarMessage)
        }
    }

    suspend fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
        isTimerRunning = false
    }

    val context = LocalContext.current
    val haptics = Haptics(context)
    val mediaPlayer = MediaPlayer.create(context, R.raw.ding)
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
        if (!silent && isDingEnabled) {
            haptics.progress()
            mediaPlayer.start()
        }
    }

    suspend fun progressAnimation() {
        val safeCurrentStep = currentStep ?: return
        isDone = false
        isTimerRunning = true
        val currentStepTime = safeCurrentStep.time
        if (currentStepTime == null) {
            animatedProgressValue.snapTo(1f)
            isTimerRunning = false
            return
        }
        val duration = (currentStepTime - (currentStepTime * animatedProgressValue.value)).toInt()
        coroutineScope.launch(Dispatchers.Default) {
            withContext(object : MotionDurationScale {
                override val scaleFactor: Float = 1f
            }) {
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
        withContext(object : MotionDurationScale {
            override val scaleFactor: Float = 1f
        }) {
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
    LaunchedEffect(isTimerRunning, timerRect) {
        onTimerRunning(isTimerRunning, timerRect)
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

    val configuration = LocalConfiguration.current

    val isPhoneLayout by remember(
        windowSizeClass.widthSizeClass,
        configuration.screenHeightDp,
        configuration.screenWidthDp
    ) {
        derivedStateOf {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
                    (configuration.screenHeightDp / configuration.screenWidthDp.toFloat() > 1.3)
        }
    }

    val renderDescription: @Composable (() -> Unit)? = if (recipe.description.isBlank()) null else {
        {
            Description(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipe_description"),
                descriptionText = recipe.description
            )
            Spacer(modifier = Modifier.height(Spacing.big))
        }
    }
    val renderTimer: @Composable (Modifier) -> Unit = {
        Timer(
            modifier = it
                .testTag("recipe_timer")
                .onGloballyPositioned { coordinates ->
                    timerRect = coordinates
                        .boundsInWindow()
                        .toAndroidRect()
                },
            currentStep = currentStep,
            animatedProgressValue = animatedProgressValue,
            animatedProgressColor = animatedProgressColor,
            isInPiP = isInPiP,
            alreadyDoneWeight = alreadyDoneWeight.value,
            isDone = isDone,
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
                }
            )
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarState, modifier = Modifier.padding(Spacing.medium)) {
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
                            modifier = Modifier.padding(end = Spacing.small)
                        )
                        Text(
                            text = recipe.name, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAutomateLinkDialog = true }) {
                        Icon(
                            painterResource(id = R.drawable.ic_link), contentDescription = null
                        )
                    }
                    IconButton(onClick = goToEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
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

    if (showAutomateLinkDialog) {
        DirectLinkDialog(dismiss = { showAutomateLinkDialog = false }, onConfirm = {
            copyAutomateLink()
            showAutomateLinkDialog = false
        })
    }
}

@Composable
fun TabletLayout(
    paddingValues: PaddingValues,
    description: (@Composable () -> Unit)? = null,
    timer: @Composable (Modifier) -> Unit,
    steps: LazyListScope.() -> Unit,
    isInPiP: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                if (isInPiP) {
                    PaddingValues(0.dp)
                } else {
                    getDefaultPadding(paddingValues)
                }
            ),
        horizontalArrangement = Arrangement.Center,
    ) {
        timer(
            Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterVertically)
        )
        if (!isInPiP) {
            LazyColumn(
                modifier = Modifier.padding(Spacing.normal),
                contentPadding = PaddingValues(bottom = Spacing.bigFab, top = Spacing.big)
            ) {
                if ((description != null)) {
                    item {
                        description()
                    }
                }
                steps()
            }
        }
    }
}

@Composable
fun PhoneLayout(
    paddingValues: PaddingValues,
    description: (@Composable () -> Unit)? = null,
    timer: @Composable (Modifier) -> Unit,
    steps: LazyListScope.() -> Unit,
    isInPiP: Boolean,
    lazyListState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = if (isInPiP) {
            PaddingValues(0.dp)
        } else {
            getDefaultPadding(paddingValues = paddingValues, FabType.Big)
        },
        state = lazyListState,
    ) {
        if (!isInPiP && (description != null)) {
            item {
                description()
            }
        }
        item {
            timer(Modifier)
        }
        if (!isInPiP) {
            steps()
        }
    }
}

@Composable
fun DirectLinkDialog(dismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.button_copy))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_link), contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_text))
        },
    )
}

@Composable
fun StartFAB(isTimerRunning: Boolean, onClick: () -> Unit) {
    val animatedFabRadii = remember { Animatable(100f) }
    LaunchedEffect(key1 = isTimerRunning) {
        animatedFabRadii.animateTo(
            if (isTimerRunning) 28.0f else 100f,
            tween(if (isTimerRunning) 300 else 500)
        )
    }
    LargeFloatingActionButton(
        shape = RoundedCornerShape(animatedFabRadii.value.dp),
        onClick = onClick,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("recipe_start")
            .toggleable(isTimerRunning) {},
    ) {
        Icon(
            painter = if (isTimerRunning) {
                painterResource(id = R.drawable.ic_pause)
            } else {
                painterResource(id = R.drawable.ic_play_arrow)
            },
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreview() {
    RecipeDetails(
        recipeId = 1, isInPiP = false,
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(1920.dp, 1080.dp)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreviewPip() {
    RecipeDetails(
        recipeId = 1, isInPiP = true,
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(1920.dp, 1080.dp)
        )
    )
}