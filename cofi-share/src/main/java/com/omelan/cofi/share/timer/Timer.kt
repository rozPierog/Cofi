package com.omelan.cofi.share.timer

import android.media.MediaPlayer
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.omelan.cofi.share.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.timer.notification.*
import com.omelan.cofi.share.utils.Haptics
import com.omelan.cofi.share.utils.roundToDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun <R, T> suspendCompat(block: suspend (T) -> R): suspend (T) -> R = block

data class AnimationControllers(
    val animatedProgressValue: Animatable<Float, AnimationVector1D>,
    val animatedProgressColor: Animatable<Color, AnimationVector4D>,
    val pauseAnimations: suspend () -> Unit,
    val progressAnimation: suspend (Unit) -> Unit,
    val resumeAnimations: suspend () -> Unit,
)

data class MultiplierControllers(
    val weightMultiplier: Float,
    val changeWeightMultiplier: (Float) -> Unit,
    val timeMultiplier: Float,
    val changeTimeMultiplier: (Float) -> Unit,
)

data class TimerControllers(
    val animationControllers: AnimationControllers,
    val currentStep: Step?,
    val indexOfCurrentStep: Int,
    val changeCurrentStep: (Step?) -> Unit,
    val changeToNextStep: suspend (Boolean) -> Unit,
    val isDone: Boolean,
    val isTimerRunning: Boolean,
    val alreadyDoneWeight: Float,
    val multiplierControllers: MultiplierControllers,
)

object Timer {
    @Composable
    private fun rememberAlreadyDoneWeight(
        indexOfCurrentStep: Int,
        allSteps: List<Step>,
        combineWeightState: String,
        weightMultiplier: Float = 1f,
    ): State<Float> {
        val doneSteps = if (indexOfCurrentStep == -1) {
            listOf()
        } else {
            allSteps.subList(0, indexOfCurrentStep)
        }
        return remember(combineWeightState, indexOfCurrentStep, weightMultiplier, doneSteps) {
            derivedStateOf {
                when (combineWeightState) {
                    CombineWeight.ALL.name ->
                        (doneSteps.sumOf { it.value?.toDouble() ?: 0.0 } * weightMultiplier)
                            .toFloat().roundToDecimals()

                    CombineWeight.WATER.name -> (
                            doneSteps.sumOf {
                                if (it.type === StepType.WATER && it.value != null) {
                                    it.value.toDouble()
                                } else {
                                    0.0
                                }
                            } * weightMultiplier
                            ).toFloat().roundToDecimals()

                    else -> 0f
                }
            }
        }
    }

    @Composable
    fun createTimerControllers(
        recipe: Recipe,
        steps: List<Step>,
        onRecipeEnd: () -> Unit,
        dataStore: DataStore,
        doneTrackColor: Color,
    ): TimerControllers {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val isDarkMode = isSystemInDarkTheme()
        val haptics = remember { Haptics(context) }
        val mediaPlayer = remember { MediaPlayer.create(context, R.raw.ding) }
        val isStepChangeSoundEnabled by dataStore.getStepChangeSoundSetting()
            .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
        val isStepChangeVibrationEnabled by dataStore.getStepChangeVibrationSetting()
            .collectAsState(initial = STEP_VIBRATION_DEFAULT_VALUE)
        var currentStep by remember { mutableStateOf<Step?>(null) }
        var weightMultiplier by remember { mutableStateOf(1.0f) }
        var timeMultiplier by remember { mutableStateOf(1.0f) }
        var isDone by remember { mutableStateOf(false) }
        val animatedProgressColor =
            remember { Animatable(if (isDarkMode) Color.LightGray else Color.DarkGray) }
        val animatedProgressValue = remember { Animatable(0f) }

        val indexOfCurrentStep by remember(steps, currentStep) {
            derivedStateOf { steps.indexOf(currentStep) }
        }
        val indexOfLastStep by remember(steps) {
            derivedStateOf { steps.lastIndex }
        }

        val combineWeightState by dataStore.getWeightSetting()
            .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)

        val backgroundTimerState by dataStore.getBackgroundTimerSetting()
            .collectAsState(initial = null)

        val alreadyDoneWeight by rememberAlreadyDoneWeight(
            indexOfCurrentStep = indexOfCurrentStep,
            allSteps = steps,
            combineWeightState = combineWeightState,
            weightMultiplier = weightMultiplier,
        )

        val pauseAnimations = suspend {
            animatedProgressColor.stop()
            animatedProgressValue.stop()
        }

        val changeToNextStep = suspendCompat { silent: Boolean ->
            animatedProgressValue.snapTo(0f)
            if (indexOfCurrentStep != indexOfLastStep) {
                currentStep = steps[indexOfCurrentStep + 1]
            } else {
                currentStep = null
                isDone = true
                onRecipeEnd()
            }
            if (silent) {
                return@suspendCompat
            }
            if (isStepChangeSoundEnabled) {
                mediaPlayer.start()
            }
            if (isStepChangeVibrationEnabled) {
                haptics.progress()
            }
        }

        val progressAnimation = suspendCompat<Unit, Unit> {
            val safeCurrentStep = currentStep ?: return@suspendCompat
            isDone = false
            val currentStepTime = safeCurrentStep.time?.times(timeMultiplier)
            if (currentStepTime == null) {
                animatedProgressValue.snapTo(1f)
                animatedProgressColor.snapTo(
                    if (isDarkMode) {
                        safeCurrentStep.type.colorNight
                    } else {
                        safeCurrentStep.type.color
                    },
                )
                return@suspendCompat
            }
            val duration =
                (currentStepTime - (currentStepTime * animatedProgressValue.value)).toInt()
            withContext(
                object : MotionDurationScale {
                    override val scaleFactor: Float = 1f
                },
            ) {
                launch(Dispatchers.Default) {
                    animatedProgressColor.animateTo(
                        targetValue = if (isDarkMode) {
                            safeCurrentStep.type.colorNight
                        } else {
                            safeCurrentStep.type.color
                        },
                        animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                    )
                }
                val result = animatedProgressValue.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                )
                if (result.endReason != AnimationEndReason.Finished) {
                    return@withContext
                }
                changeToNextStep(false)
            }
        }

        val resumeAnimations: suspend () -> Unit = suspend {
            coroutineScope.launch {
                progressAnimation(Unit)
            }
        }

        LaunchedEffect(isDone) {
            if (isDone) {
                animatedProgressColor.animateTo(doneTrackColor, tween())
            }
        }

        DisposableEffect(lifecycleOwner, steps) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        if (backgroundTimerState != true) {
                            return@LifecycleEventObserver
                        }
                        val workManager = WorkManager.getInstance(context)
                        val workInfoByIdLiveData = workManager
                            .getWorkInfosForUniqueWorkLiveData("cofi_${recipe.id}")
                        workInfoByIdLiveData.observe(
                            lifecycleOwner,
                            object : Observer<List<WorkInfo>> {
                                override fun onChanged(value: List<WorkInfo>) {
                                    val workerInfo = value.firstOrNull()
                                    if (
                                        workerInfo == null ||
                                        !arrayOf(WorkInfo.State.RUNNING, WorkInfo.State.SUCCEEDED)
                                            .contains(workerInfo.state)
                                    ) {
                                        workInfoByIdLiveData.removeObserver(this)
                                        return
                                    }
                                    val progress =
                                        if (workerInfo.state == WorkInfo.State.RUNNING) {
                                            workerInfo.progress
                                        } else {
                                            workerInfo.outputData
                                        }
                                    val stepID = progress.getInt(WORKER_PROGRESS_STEP, -1)
                                    weightMultiplier = progress.getFloat(
                                        WORKER_PROGRESS_TIME_MULTIPLIER, 1f,
                                    )
                                    timeMultiplier = progress.getFloat(
                                        WORKER_PROGRESS_WEIGHT_MULTIPLIER, 1f,
                                    )
                                    val stepProgress =
                                        progress.getFloat(WORKER_PROGRESS_PROGRESS, 0f)
                                    val isPaused =
                                        progress.getBoolean(
                                            WORKER_PROGRESS_IS_PAUSED,
                                            workerInfo.state.isFinished,
                                        )
                                    currentStep = steps.firstOrNull { it.id == stepID }

                                    coroutineScope.launch {
                                        animatedProgressValue.snapTo(stepProgress)
                                        // TODO: race condition!
                                        //  Sometimes LaunchedEffect(currentStep) is done after this
                                        //  and animation doesn't stop
                                        if (isPaused) {
                                            pauseAnimations()
                                        } else {
                                            resumeAnimations()
                                        }
                                    }
                                    workInfoByIdLiveData.removeObserver(this)
                                    context.sendBroadcast(
                                        TimerActions.createIntent(
                                            context,
                                            TimerActions.Actions.ACTION_STOP,
                                            TimerData(
                                                recipeId = recipe.id,
                                                stepId = stepID,
                                                alreadyDoneProgress = stepProgress,
                                                weightMultiplier = weightMultiplier,
                                                timeMultiplier = timeMultiplier,
                                            ),
                                        ),
                                    )
                                }
                            },
                        )
                    }

                    Lifecycle.Event.ON_STOP -> {
                        if (
                            backgroundTimerState == true &&
                            animatedProgressValue.isRunning &&
                            currentStep != null
                        ) {
                            context.sendBroadcast(
                                TimerActions.createIntent(
                                    context,
                                    TimerActions.Actions.ACTION_RESUME,
                                    TimerData(
                                        recipeId = recipe.id,
                                        stepId = currentStep?.id,
                                        alreadyDoneProgress = animatedProgressValue.value,
                                        weightMultiplier = weightMultiplier,
                                        timeMultiplier = timeMultiplier,
                                    ),
                                ),
                            )
                        }
                    }

                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        return TimerControllers(
            animationControllers = AnimationControllers(
                animatedProgressValue = animatedProgressValue,
                animatedProgressColor = animatedProgressColor,
                pauseAnimations = pauseAnimations,
                progressAnimation = progressAnimation,
                resumeAnimations = resumeAnimations,
            ),
            currentStep = currentStep,
            indexOfCurrentStep = indexOfCurrentStep,
            changeCurrentStep = { currentStep = it },
            changeToNextStep = changeToNextStep,
            isDone = isDone,
            isTimerRunning = animatedProgressValue.isRunning,
            alreadyDoneWeight = alreadyDoneWeight,
            multiplierControllers = MultiplierControllers(
                weightMultiplier,
                { weightMultiplier = it },
                timeMultiplier,
                { timeMultiplier = it },
            ),
        )
    }
}
