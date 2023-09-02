package com.omelan.cofi.share.timer.notification

import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asFlow
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
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

    private fun tickerFlow(duration: Long, period: Duration = 500.milliseconds) = flow {
        for (i in duration / 500 downTo 0L) {
            emit(i * 500)
            delay(period)
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

    override suspend fun doWork() = coroutineScope {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap[COFI_TIMER_NOTIFICATION_RECIPE_ID] as Int
        val startingStepId = valueMap[COFI_TIMER_NOTIFICATION_CURRENT_STEP_ID] as Int?
        val startingTime = valueMap[COFI_TIMER_NOTIFICATION_START_TIME_DATA] as Long
        val startingProgress = valueMap[COFI_TIMER_NOTIFICATION_PROGRESS] as Float? ?: 0f
        val weightMultiplier = valueMap[COFI_TIMER_NOTIFICATION_WEIGHT_MULTIPLIER] as Float? ?: 0f
        val timeMultiplier = valueMap[COFI_TIMER_NOTIFICATION_TIME_MULTIPLIER] as Float? ?: 0f
        val action = valueMap[COFI_TIMER_NOTIFICATION_ACTION] as String?
        val db = AppDatabase.getInstance(context)
        val recipe = db.recipeDao().get(recipeId).asFlow().first()
        val steps = db.stepDao().getStepsForRecipe(recipeId).asFlow().first()
        if (startingStepId == null) {
            postDoneNotification(recipe)
        }
        val isPaused = action == TimerActions.Actions.ACTION_PAUSE.name
        val initialStep =
            steps.find { it.id == startingStepId } ?: return@coroutineScope Result.failure()

        suspend fun postNotificationWithProgress(
            step: Step,
            progress: Float = 1f,
            isPaused: Boolean = false,
        ) {
            setProgress(
                workDataOf(
                    WORKER_PROGRESS_STEP to step.id,
                    WORKER_PROGRESS_PROGRESS to progress,
                    WORKER_PROGRESS_IS_PAUSED to isPaused,
                    WORKER_PROGRESS_WEIGHT_MULTIPLIER to weightMultiplier,
                    WORKER_PROGRESS_TIME_MULTIPLIER to timeMultiplier,
                ),
            )
            postTimerNotification(
                context,
                step.toNotificationBuilder(
                    context,
                    recipe = recipe,
                    weightMultiplier = weightMultiplier,
                    timeMultiplier = timeMultiplier,
                    nextStepId = steps.findNextId(step),
                    currentProgress = progress,
                ),
                id = COFI_TIMER_NOTIFICATION_ID + step.id,
                tag = COFI_TIMER_NOTIFICATION_TAG,
            )
        }

        suspend fun startCountDown(step: Step, startingProgress: Float = 0f) {
            if (step.time == null /* step.isUserInputRequired */) {
                postNotificationWithProgress(step, startingProgress, isPaused = true)
                return
            }

            val stepTimeCalculated = step.time.toLong() * timeMultiplier

            fun createCountDownTimer(millis: Long) = tickerFlow(millis)
                .distinctUntilChanged()
                .onEach {
                    val progress = ((stepTimeCalculated - it) / stepTimeCalculated)
                    postNotificationWithProgress(step, progress)
                }.onCompletion {
                    NotificationManagerCompat.from(context)
                        .cancel(COFI_TIMER_NOTIFICATION_TAG, COFI_TIMER_NOTIFICATION_ID + step.id)
                    if (steps.last().id == step.id) {
                        postDoneNotification(recipe)
                    }
                    startCountDown(steps[steps.indexOf(step) + 1])
                }
                .launchIn(this)

            val currentTime = SystemClock.elapsedRealtime()
            val offset = if (step.id == initialStep.id) currentTime - startingTime else 0
            val millisToCount = (stepTimeCalculated * (1 - startingProgress) - offset).toLong()
            val countDownTimer = createCountDownTimer(millisToCount)
            countDownTimer.start()
        }
        if (!isPaused) {
            startCountDown(initialStep, startingProgress)
        } else {
            postNotificationWithProgress(initialStep, startingProgress, isPaused = true)
        }
        return@coroutineScope Result.success()
    }


}
