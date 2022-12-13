@file:OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)

package com.omelan.cofi.wearos.presentation.pages

import android.provider.Settings
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.omelan.cofi.share.*
import com.omelan.cofi.share.components.StepNameText
import com.omelan.cofi.share.components.TimeText
import com.omelan.cofi.share.components.TimerValue
import com.omelan.cofi.share.timer.Timer
import com.omelan.cofi.share.timer.TimerControllers
import com.omelan.cofi.wearos.presentation.LocalAmbientModeProvider
import com.omelan.cofi.wearos.presentation.components.ListenKeyEvents
import com.omelan.cofi.wearos.presentation.components.StartButton
import kotlinx.coroutines.launch
import java.math.RoundingMode
import kotlin.math.roundToInt


@Composable
fun RecipeDetails(modifier: Modifier = Modifier, recipeId: Int, onTimerRunning: (Boolean) -> Unit) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val stepsViewModel: StepsViewModel = viewModel()
    val recipe by recipeViewModel.getRecipe(recipeId).observeAsState(initial = Recipe(name = ""))
    val steps by stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    RecipeDetails(
        modifier = modifier,
        recipe = recipe,
        steps = steps,
        onTimerRunning = onTimerRunning,
    )
}

@Composable
fun RecipeDetails(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    steps: List<Step>,
    onTimerRunning: (Boolean) -> Unit,
) {
    val dataStore = DataStore(LocalContext.current)

    val ambientController = LocalAmbientModeProvider.current
    var weightMultiplier by remember { mutableStateOf(1.0f) }
    var timeMultiplier by remember { mutableStateOf(1.0f) }
    val timerControllers = Timer.createTimerControllers(
        steps = steps,
        onRecipeEnd = { },
        dataStore = dataStore,
    )
    val context = LocalContext.current
    val ambientEnabled: Boolean = remember(LocalLifecycleOwner.current) {
        Settings.Global.getInt(context.contentResolver, "ambient_enabled") == 1
    }

    LaunchedEffect(timerControllers.isTimerRunning) {
        if (ambientEnabled) {
            ambientController.setAmbientOffloadEnabled(timerControllers.isTimerRunning)
        }
        onTimerRunning(timerControllers.isTimerRunning)
    }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            ambientController.setAmbientOffloadEnabled(false)
        }
    }
    LaunchedEffect(timerControllers.currentStep.value) {
        timerControllers.progressAnimation(Unit)
    }


    val pagerState = rememberPagerState(0)
    val animatedSelectedPage by animateFloatAsState(
        targetValue = pagerState.currentPage.toFloat(),
        animationSpec = TweenSpec(durationMillis = 500),
    )
    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = animatedSelectedPage - pagerState.currentPage
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageCount: Int
                get() = pagerState.pageCount
        }
    }
    Scaffold(
        timeText = {
            TimeText()
        },
        pageIndicator = {
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState,
                modifier = Modifier.padding(10.dp),
            )
        },
    ) {
        HorizontalPager(count = 3, modifier = modifier, state = pagerState) { page ->
            when (page) {
                0 -> TimerPage(
                    timerControllers = timerControllers,
                    allSteps = steps,
                    recipe = recipe,
                    dataStore = dataStore,
                )

                1 -> Row {
                    MultiplierPage(
                        multiplier = weightMultiplier,
                        changeMultiplier = { weightMultiplier = it },
                    )
                }

                2 -> MultiplierPage(
                    multiplier = timeMultiplier,
                    changeMultiplier = { timeMultiplier = it },
                )
            }
        }
    }
}

const val step = 0.1f
val range = 0f..3f
val steps = (range.endInclusive / step).roundToInt() + 1

@Composable
fun MultiplierPage(
    multiplier: Float,
    changeMultiplier: (Float) -> Unit,
) {
    var rotaryPosition by remember { mutableStateOf(0f) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Stepper(
        modifier = Modifier
            .onRotaryScrollEvent {
                when (it.verticalScrollPixels.compareTo(rotaryPosition)) {
                    1 -> changeMultiplier(multiplier + step)
                    0 -> {}
                    -1 -> changeMultiplier(multiplier - step)
                }
                rotaryPosition = it.verticalScrollPixels
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        value = multiplier,
        onValueChange = {
            val rounded = it.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
            changeMultiplier(rounded)
        },
        steps = steps,
        valueRange = range,
        decreaseIcon = { Icon(Icons.Rounded.Refresh, contentDescription = "") },
        increaseIcon = { Icon(Icons.Rounded.Add, contentDescription = "") },
    ) {
        Text(text = multiplier.toString())
    }
}

@Composable
fun TimerPage(
    timerControllers: TimerControllers,
    allSteps: List<Step>,
    recipe: Recipe,
    dataStore: DataStore,
) {
    val (
        currentStep,
        isDone,
        isTimerRunning,
        _,
        animatedProgressValue,
        animatedProgressColor,
        pauseAnimations,
        _,
        startAnimations,
        changeToNextStep,
    ) = timerControllers
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)
    val coroutineScope = rememberCoroutineScope()
    val ambientController = LocalAmbientModeProvider.current

    val alreadyDoneWeight by Timer.rememberAlreadyDoneWeight(
        indexOfCurrentStep = allSteps.indexOf(currentStep.value),
        allSteps = allSteps,
        combineWeightState = combineWeightState,
    )
    val startButtonOnClick: () -> Unit = {
        if (currentStep.value != null) {
            if (animatedProgressValue.isRunning) {
                coroutineScope.launch { pauseAnimations() }
            } else {
                coroutineScope.launch {
                    if (currentStep.value?.time == null) {
                        changeToNextStep(false)
                    } else {
                        startAnimations()
                    }
                }
            }
        } else
            coroutineScope.launch { changeToNextStep(true) }
    }

    ListenKeyEvents { keyCode, event ->
        if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1,
                KeyEvent.KEYCODE_STEM_2,
                KeyEvent.KEYCODE_STEM_3,
                -> {
                    startButtonOnClick()
                    true
                }

                else -> false
            }
        } else {
            false
        }
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
            indicatorColor = if (ambientController.isAmbient) {
                Color.White
            } else {
                animatedProgressColor.value
            },
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
                visible = currentStep.value == null && !isDone,
            ) {
                Text(
                    text = recipe.name,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 2,
                    style = MaterialTheme.typography.title1,
                )
            }
            AnimatedVisibility(
                visible = currentStep.value != null && !isDone,
            ) {
                Column {
                    if (currentStep.value != null) {
                        TimeText(
                            currentStep = currentStep.value!!,
                            animatedProgressValue = animatedProgressValue.value,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 2,
                            style = MaterialTheme.typography.title2,
                            paddingHorizontal = 2.dp,
                            showMillis = false,
                        )
                        StepNameText(
                            currentStep = currentStep.value!!,
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.title3,
                            maxLines = 1,
                            paddingHorizontal = 2.dp,
                        )
                        TimerValue(
                            currentStep = currentStep.value!!,
                            animatedProgressValue = animatedProgressValue.value,
                            alreadyDoneWeight = alreadyDoneWeight,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,
                            style = MaterialTheme.typography.title1,
                        )
                    }
                }
            }
            AnimatedVisibility(visible = !ambientController.isAmbient) {
                Spacer(Modifier.height(6.dp))
                StartButton(isTimerRunning, startButtonOnClick)
            }
        }
    }
}


@Preview
@Composable
fun TimerPreview() {
    RecipeDetails(recipe = Recipe(name = "test"), steps = emptyList(), onTimerRunning = {})
}
