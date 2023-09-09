package com.omelan.cofi.share.timer.notification

import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asFlow
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.omelan.cofi.share.CombineWeight
import com.omelan.cofi.share.DataStore
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.roundToDecimals
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private fun List<Step>.findNextId(currentStep: Step) = try {
    this[this.indexOf(currentStep) + 1].id
} catch (e: IndexOutOfBoundsException) {
    null
}

class TimerWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private fun tickerFlow(duration: Long, step: Duration = 500.milliseconds) = flow {
        val stepMs = step.inWholeMilliseconds
        emit(0)
        for (i in 1L..(duration / stepMs)) {
            delay(step)
            emit(i * stepMs)
        }
        val rest = duration % stepMs
        if (rest > 0) {
            delay(rest)
            emit(duration)
        }
    }

    private suspend fun postDoneNotification(recipe: Recipe) {
        setProgress(
            workDataOf(
                WORKER_PROGRESS_STEP to null,
                WORKER_PROGRESS_PROGRESS to 0f,
                WORKER_PROGRESS_IS_PAUSED to false,
            ),
        )
        postTimerNotification(
            context,
            createDoneNotification(recipe, context),
            id = COFI_TIMER_NOTIFICATION_ID + 1,
            tag = COFI_TIMER_NOTIFICATION_TAG,
        )
    }


    /**
     *  TODO: DRY [Timer.rememberAlreadyDoneWeight]
     */
    private suspend fun calculateAlreadyDoneWeight(
        indexOfCurrentStep: Int,
        steps: List<Step>,
        weightMultiplier: Float,
    ): Float {
        val doneSteps = if (indexOfCurrentStep == -1) {
            listOf()
        } else {
            steps.subList(0, indexOfCurrentStep)
        }
        return when (DataStore(context).getWeightSetting().first()) {
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

    override suspend fun doWork() = coroutineScope {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap[COFI_TIMER_NOTIFICATION_RECIPE_ID] as Int
        val startingStepId = valueMap[COFI_TIMER_NOTIFICATION_CURRENT_STEP_ID] as Int?
        val startingRealtime = valueMap[COFI_TIMER_NOTIFICATION_START_TIME_DATA] as Long
        val startingProgress = valueMap[COFI_TIMER_NOTIFICATION_PROGRESS] as Float? ?: 0f
        val weightMultiplier = valueMap[COFI_TIMER_NOTIFICATION_WEIGHT_MULTIPLIER] as Float? ?: 0f
        val timeMultiplier = valueMap[COFI_TIMER_NOTIFICATION_TIME_MULTIPLIER] as Float? ?: 0f
        val db = AppDatabase.getInstance(context)
        val recipe = db.recipeDao().get(recipeId).asFlow().first()
        val steps = db.stepDao().getStepsForRecipe(recipeId).asFlow().first()
        val initialStep =
            steps.find { it.id == startingStepId } ?: return@coroutineScope Result.failure()

        suspend fun calculateAlreadyDoneWeight(indexOfCurrentStep: Int) =
            calculateAlreadyDoneWeight(indexOfCurrentStep, steps, weightMultiplier)

        val action = valueMap[COFI_TIMER_NOTIFICATION_ACTION] as String?

        if (action == TimerActions.Actions.ACTION_STOP.name) {
            NotificationManagerCompat.from(context)
                .cancel(
                    COFI_TIMER_NOTIFICATION_TAG,
                    COFI_TIMER_NOTIFICATION_ID +
                            (startingStepId ?: 1),
                )
            return@coroutineScope Result.failure()
        }

        val isPaused = action == TimerActions.Actions.ACTION_PAUSE.name

        if (startingStepId == null) {
            postDoneNotification(recipe)
        }

        suspend fun postNotificationWithProgress(
            step: Step,
            progress: Float = 1f,
            isPaused: Boolean = false,
        ): Data {
            val progressData = workDataOf(
                WORKER_PROGRESS_STEP to step.id,
                WORKER_PROGRESS_PROGRESS to progress,
                WORKER_PROGRESS_IS_PAUSED to isPaused,
                WORKER_PROGRESS_WEIGHT_MULTIPLIER to weightMultiplier,
                WORKER_PROGRESS_TIME_MULTIPLIER to timeMultiplier,
            )
            setProgress(progressData)
            postTimerNotification(
                context,
                step.toNotificationBuilder(
                    context,
                    recipe = recipe,
                    weightMultiplier = weightMultiplier,
                    timeMultiplier = timeMultiplier,
                    nextStepId = steps.findNextId(step),
                    currentProgress = progress,
                    isPaused = isPaused,
                    alreadyDoneWeight = calculateAlreadyDoneWeight(steps.indexOf(step)),
                ),
                id = COFI_TIMER_NOTIFICATION_ID + step.id,
                tag = COFI_TIMER_NOTIFICATION_TAG,
            )
            return progressData
        }

        suspend fun startCountDown(step: Step, startingProgress: Float = 0f) {
            if (step.time == null /* step.isUserInputRequired */) {
                postNotificationWithProgress(step, startingProgress, isPaused = true)
                return
            }

            val stepTimeCalculated = step.time.toLong() * timeMultiplier

            suspend fun createCountDownTimer(millis: Long) = tickerFlow(millis)
                .distinctUntilChanged()
                .buffer(1)
                .collect { currentValue ->
                    if (currentValue == millis) {
                        NotificationManagerCompat.from(context)
                            .cancel(
                                COFI_TIMER_NOTIFICATION_TAG,
                                COFI_TIMER_NOTIFICATION_ID + step.id,
                            )
                        if (steps.last().id == step.id) {
                            postDoneNotification(recipe)
                            return@collect
                        }
                        startCountDown(steps[steps.indexOf(step) + 1])
                    } else {
                        postNotificationWithProgress(
                            step,
                            progress = (currentValue / stepTimeCalculated) + startingProgress,
                        )
                    }
                }

            val currentTime = SystemClock.elapsedRealtime()
            val offset = if (step.id == initialStep.id) currentTime - startingRealtime else 0
            val millisToCount = (stepTimeCalculated * (1 - startingProgress) - offset).toLong()
            createCountDownTimer(millisToCount)
        }
        if (!isPaused) {
            startCountDown(initialStep, startingProgress)
        } else {
            val progressData =
                postNotificationWithProgress(initialStep, startingProgress, isPaused = true)
            return@coroutineScope Result.success(progressData)
        }
        return@coroutineScope Result.success()
    }
}
