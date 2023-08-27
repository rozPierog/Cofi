package com.omelan.cofi.share.timer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.SystemClock
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
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
import com.omelan.cofi.share.utils.getActivity
import com.omelan.cofi.share.utils.roundToDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun <R, T> suspendCompat(block: suspend (T) -> R): suspend (T) -> R = block

data class TimerControllers(
    val currentStep: Step?,
    val changeCurrentStep: (Step?) -> Unit,
    val isDone: Boolean,
    val isTimerRunning: Boolean,
    val indexOfCurrentStep: Int,
    val animatedProgressValue: Animatable<Float, AnimationVector1D>,
    val animatedProgressColor: Animatable<Color, AnimationVector4D>,
    val pauseAnimations: suspend () -> Unit,
    val progressAnimation: suspend (Unit) -> Unit,
    val startAnimations: suspend () -> Unit,
    val changeToNextStep: suspend (Boolean) -> Unit,
)

object Timer {
    @Composable
    fun rememberAlreadyDoneWeight(
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
        return remember(combineWeightState, indexOfCurrentStep, weightMultiplier) {
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

                    CombineWeight.NONE.name -> 0f
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
        timeMultiplier: Float = 1f,
    ): TimerControllers {
        val isStepChangeSoundEnabled by dataStore.getStepChangeSoundSetting()
            .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
        val isStepChangeVibrationEnabled by dataStore.getStepChangeVibrationSetting()
            .collectAsState(initial = STEP_VIBRATION_DEFAULT_VALUE)
        var currentStep by remember { mutableStateOf<Step?>(null) }
        var isDone by remember { mutableStateOf(false) }
        val isDarkMode = isSystemInDarkTheme()
        val animatedProgressColor =
            remember { Animatable(if (isDarkMode) Color.LightGray else Color.DarkGray) }
        val animatedProgressValue = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()

        val context = LocalContext.current
        val haptics = remember { Haptics(context) }
        val mediaPlayer = remember { MediaPlayer.create(context, R.raw.ding) }

        val lifecycleOwner = LocalLifecycleOwner.current

        val indexOfCurrentStep by remember(steps, currentStep) {
            derivedStateOf { steps.indexOf(currentStep) }
        }
        val indexOfLastStep by remember(steps) {
            derivedStateOf { steps.lastIndex }
        }
        val pauseAnimations = suspend {
            context.sendBroadcast(
                TimerActions.createIntent(
                    context,
                    TimerActions.Actions.ACTION_PAUSE,
                    recipeId = recipe.id,
                    stepId = currentStep?.id,
                    alreadyDoneProgress = animatedProgressValue.value,
                )
            )
            animatedProgressColor.stop()
            animatedProgressValue.stop()
        }

        val changeToNextStep = suspendCompat { silent: Boolean ->
            animatedProgressValue.snapTo(0f)
            if (indexOfCurrentStep != indexOfLastStep) {
                currentStep = steps[indexOfCurrentStep + 1]
                if (indexOfCurrentStep == 0) {
                    context.startTimerWorker(
                        recipeId = recipe.id,
                        stepId = currentStep!!.id,
                        startingTime = SystemClock.elapsedRealtime(),
                    )
                }
            } else {
                currentStep = null
                isDone = true
                onRecipeEnd()
            }
            if (silent) {
                return@suspendCompat
            }
            if (isStepChangeSoundEnabled) {
                mediaPlayer?.start()
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
            context.sendBroadcast(
                TimerActions.createIntent(
                    context,
                    TimerActions.Actions.ACTION_RESUME,
                    recipeId = recipe.id,
                    stepId = currentStep?.id,
                    alreadyDoneProgress = animatedProgressValue.value,
                )
            )
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
                    Lifecycle.Event.ON_CREATE -> {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ActivityCompat.requestPermissions(
                                    context.getActivity() as Activity,
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    1,
                                )
                            }
                        }
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        val workManager = WorkManager.getInstance(context)
                        val workInfoByIdLiveData = workManager
                            .getWorkInfosForUniqueWorkLiveData("cofi_${recipe.id}")
                        workInfoByIdLiveData.observe(
                            lifecycleOwner,
                            object : Observer<List<WorkInfo>> {
                                override fun onChanged(value: List<WorkInfo>) {
                                    if (value.isEmpty()) {
                                        workInfoByIdLiveData.removeObserver(this)
                                        return
                                    }
                                    val workerInfo = value.firstOrNull() ?: return
                                    val progress = workerInfo.progress
                                    if (workerInfo.state != WorkInfo.State.RUNNING) {
                                        workInfoByIdLiveData.removeObserver(this)
                                        return
                                    }
                                    val stepID = progress.getInt(WORKER_PROGRESS_STEP, 0)
                                    val stepProgress =
                                        progress.getFloat(WORKER_PROGRESS_PROGRESS, 0f)
                                    val isPaused =
                                        progress.getBoolean(WORKER_PROGRESS_IS_PAUSED, false)
                                    currentStep = steps.firstOrNull { it.id == stepID }
                                    coroutineScope.launch {
                                        animatedProgressValue.snapTo(stepProgress)
                                        if (isPaused) {
                                            pauseAnimations()
                                        } else {
                                            resumeAnimations()
                                        }
                                    }
                                    workInfoByIdLiveData.removeObserver(this)
                                }
                            },
                        )
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
            currentStep,
            { currentStep = it },
            isDone,
            animatedProgressValue.isRunning,
            indexOfCurrentStep,
            animatedProgressValue,
            animatedProgressColor,
            pauseAnimations,
            progressAnimation,
            resumeAnimations,
            changeToNextStep,
        )
    }
}
