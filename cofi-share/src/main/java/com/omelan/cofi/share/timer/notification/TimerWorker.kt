package com.omelan.cofi.share.timer.notification

import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.asFlow
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.utils.toMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

const val COFI_TIMER_NOTIFICATION_ID = 2137
const val COFI_TIMER_NOTIFICATION_TAG = "cofi_notification_timer"
const val COFI_TIMER_NOTIFICATION_RECIPE_DATA = "cofi_timer_notification_recipe_data"
const val COFI_TIMER_NOTIFICATION_START_TIME_DATA = "cofi_timer_notification_start_time_data"
const val COFI_TIMER_NOTIFICATION_PROGRESS = "cofi_timer_notification_progress"
const val COFI_TIMER_NOTIFICATION_WEIGHT_MULTIPLIER = "cofi_timer_notification_weight_multiplier"
const val COFI_TIMER_NOTIFICATION_TIME_MULTIPLIER = "cofi_timer_notification_time_multiplier"
const val COFI_TIMER_NOTIFICATION_ACTION = "cofi_timer_notification_action"
const val COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA = "cofi_timer_notification_current_step_data"
const val WORKER_PROGRESS_STEP = "cofi_worker_progress_step_id"
const val WORKER_PROGRESS_PROGRESS = "cofi_worker_progress_progress"
const val WORKER_PROGRESS_IS_PAUSED = "cofi_worker_progress_is_paused"
const val WORKER_PROGRESS_WEIGHT_MULTIPLIER = "cofi_worker_progress_weight_multiplier"
const val WORKER_PROGRESS_TIME_MULTIPLIER = "cofi_worker_progress_time_multiplier"


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


    private suspend fun postDoneNotification() {
        setProgress(
            workDataOf(
                WORKER_PROGRESS_STEP to null,
                WORKER_PROGRESS_PROGRESS to 0f,
                WORKER_PROGRESS_IS_PAUSED to false,
            ),
        )
        postTimerNotification(
            context,
            NotificationCompat.Builder(context, TIMER_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.ic_monochrome)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setOnlyAlertOnce(false)
                setAutoCancel(true)
                setOngoing(false)
                setTimeoutAfter(600.toMillis().toLong())
                color = ResourcesCompat.getColor(
                    context.resources,
                    R.color.ic_launcher_background,
                    null,
                )
                setColorized(false)
                setContentTitle(context.getString(R.string.timer_enjoy))
            },
            id = COFI_TIMER_NOTIFICATION_ID + 1,
            tag = COFI_TIMER_NOTIFICATION_TAG,
        )
    }

    override suspend fun doWork() = coroutineScope {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap[COFI_TIMER_NOTIFICATION_RECIPE_DATA] as Int
        val startingStepId = valueMap[COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA] as Int?
        val startingTime = valueMap[COFI_TIMER_NOTIFICATION_START_TIME_DATA] as Long
        val startingProgress = valueMap[COFI_TIMER_NOTIFICATION_PROGRESS] as Float? ?: 0f
        val weightMultiplier = valueMap[COFI_TIMER_NOTIFICATION_WEIGHT_MULTIPLIER] as Float? ?: 0f
        val timeMultiplier = valueMap[COFI_TIMER_NOTIFICATION_TIME_MULTIPLIER] as Float? ?: 0f
        val action = valueMap[COFI_TIMER_NOTIFICATION_ACTION] as String?
        val db = AppDatabase.getInstance(context)
        val recipe = db.recipeDao().get(recipeId).asFlow().first()
        val steps = db.stepDao().getStepsForRecipe(recipeId).asFlow().first()
        if (startingStepId == null) {
            postDoneNotification()
        }
        val isPaused = action == TimerActions.Actions.ACTION_PAUSE.name
        val initialStep =
            steps.find { it.id == startingStepId } ?: return@coroutineScope Result.failure()
        postTimerNotification(
            context,
            initialStep.toNotificationBuilder(
                context,
                recipe = recipe,
                nextStepId = steps.findNextId(initialStep),
                weightMultiplier = weightMultiplier,
                timeMultiplier = timeMultiplier,
                currentProgress = startingProgress,
                isPaused = isPaused,
            ),
            id = COFI_TIMER_NOTIFICATION_ID + initialStep.id,
            tag = COFI_TIMER_NOTIFICATION_TAG,
        )

        suspend fun startCountDown(step: Step, startingProgress: Float = 0f) {
            if (step.time == null /* step.isUserInputRequired */) {
                setProgress(
                    workDataOf(
                        WORKER_PROGRESS_STEP to step.id,
                        WORKER_PROGRESS_PROGRESS to startingProgress,
                        WORKER_PROGRESS_IS_PAUSED to true,
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
                    ),
                    id = COFI_TIMER_NOTIFICATION_ID + step.id,
                    tag = COFI_TIMER_NOTIFICATION_TAG,
                )
                return
            }

            val stepTimeCalculated = step.time.toLong() * timeMultiplier

            fun createCountDownTimer(millis: Long) = tickerFlow(millis)
                .distinctUntilChanged()
                .onEach {
                    val progress = ((stepTimeCalculated - it).toFloat() / stepTimeCalculated)
                    setProgress(
                        workDataOf(
                            WORKER_PROGRESS_STEP to step.id,
                            WORKER_PROGRESS_PROGRESS to progress,
                            WORKER_PROGRESS_IS_PAUSED to false,
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
                            progress,
                        ),
                        id = COFI_TIMER_NOTIFICATION_ID + step.id,
                        tag = COFI_TIMER_NOTIFICATION_TAG,
                    )
                }.onCompletion {
                    NotificationManagerCompat.from(context)
                        .cancel(
                            COFI_TIMER_NOTIFICATION_TAG,
                            COFI_TIMER_NOTIFICATION_ID + step.id,
                        )
                    if (steps.last().id == step.id) {
                        postDoneNotification()
                    }
                    startCountDown(steps[steps.indexOf(step) + 1])
                }
                .launchIn(this) // or lifecycleScope or other

            val currentTime = SystemClock.elapsedRealtime()
            val offset = if (step.id == initialStep.id) currentTime - startingTime else 0
            val millisToCount = (stepTimeCalculated * (1 - startingProgress) - offset).toLong()
            val countDownTimer = createCountDownTimer(millisToCount)
            countDownTimer.start()
        }
        if (!isPaused) {
            startCountDown(initialStep, startingProgress)
        } else {
            setProgress(
                workDataOf(
                    WORKER_PROGRESS_STEP to initialStep.id,
                    WORKER_PROGRESS_PROGRESS to startingProgress,
                    WORKER_PROGRESS_IS_PAUSED to true,
                    WORKER_PROGRESS_WEIGHT_MULTIPLIER to weightMultiplier,
                    WORKER_PROGRESS_TIME_MULTIPLIER to timeMultiplier,
                ),
            )
        }
        return@coroutineScope Result.success()
    }


}
