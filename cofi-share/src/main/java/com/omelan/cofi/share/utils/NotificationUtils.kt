package com.omelan.cofi.share.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.asFlow
import androidx.work.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Step
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val TIMER_CHANNEL_ID = "cofi_timer_notification"
fun Context.createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            TIMER_CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_HIGH,
        )
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}

fun createValueText(context: Context, step: Step, currentProgress: Float): String? {
    val weightMultiplier = 1f
    val alreadyDoneWeight = 0f

    val currentStepValue = step.value ?: return null
    val currentValueFromProgress = (currentStepValue * currentProgress)

    val currentValueWithMultiplier =
        (currentValueFromProgress * weightMultiplier) + alreadyDoneWeight

    val currentTargetValue = (currentStepValue * weightMultiplier) + alreadyDoneWeight
    val targetString = currentTargetValue.toStringShort()
    val shouldShowDecimals = targetString.contains(".")
    val currentValueString: Number = if (shouldShowDecimals) {
        currentValueWithMultiplier.roundToDecimals()
    } else {
        currentValueWithMultiplier.roundToInt()
    }
    return context.getString(
        R.string.timer_progress_weight,
        currentValueString,
        targetString,
    )
}

fun Step.toNotificationBuilder(
    context: Context,
    currentProgress: Float = 0f,
    isPaused: Boolean = false,
): NotificationCompat.Builder {
    val step = this
    val builder =
        NotificationCompat.Builder(context, TIMER_CHANNEL_ID).run {
            setSmallIcon(step.type.iconRes)
            setVisibility(VISIBILITY_PUBLIC)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setOnlyAlertOnce(true)
            setAutoCancel(true)
            setOngoing(true)
            color = ResourcesCompat.getColor(
                context.resources,
                R.color.ic_launcher_background,
                null,
            )
            setColorized(true)
            setContentTitle(step.name)
            setContentText(createValueText(context, step, currentProgress))
            if (step.time != null) {
                setProgress(
                    1000,
                    (currentProgress * 1000).roundToInt(),
                    false,
                )
            }
            if (step.isUserInputRequired) {
                addAction(NotificationCompat.Action(R.drawable.ic_monochrome, "Continue", null))
            }
            val bundle = Bundle()
            bundle.putFloat("animatedValue", currentProgress)
            bundle.putInt("currentStepId", step.id)

            setExtras(bundle)
        }
    return builder
}

fun postTimerNotification(
    context: Context,
    notificationBuilder: NotificationCompat.Builder,
    id: Int = System.currentTimeMillis().toInt(),
    tag: String = id.toString(),
) {
    NotificationManagerCompat.from(context).apply {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(tag, id, notificationBuilder.build())
    }
}

fun Context.startTimerWorker(recipeId: Int, stepId: Int, startingTime: Long): UUID {
    createChannel()
    val inputData = Data.Builder().apply {
        putInt(COFI_TIMER_NOTIFICATION_RECIPE_DATA, recipeId)
        putInt(COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA, stepId)
        putLong(COFI_TIMER_NOTIFICATION_START_TIME_DATA, startingTime)
    }.build()
    val timerWorker =
        OneTimeWorkRequest.Builder(TimerWorker::class.java).setInputData(inputData)
            .addTag(recipeId.toString()).build()
    val workManager = WorkManager.getInstance(this)
    workManager.enqueueUniqueWork(
        "cofi_$recipeId",
        ExistingWorkPolicy.REPLACE,
        timerWorker,
    )
    return timerWorker.id
}

fun Context.stopTimerWorker(recipeId: Int, stepId: Int, startingTime: Long): UUID {
    createChannel()
    val inputData = Data.Builder().apply {
        putInt(COFI_TIMER_NOTIFICATION_RECIPE_DATA, recipeId)
        putInt(COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA, stepId)
        putLong(COFI_TIMER_NOTIFICATION_START_TIME_DATA, startingTime)
    }.build()
    val timerWorker =
        OneTimeWorkRequest.Builder(TimerWorker::class.java).setInputData(inputData)
            .addTag(recipeId.toString()).build()
    val workManager = WorkManager.getInstance(this)
    workManager.updateWork(timerWorker)
    return timerWorker.id
}

const val COFI_TIMER_NOTIFICATION_ID = 2137
const val COFI_TIMER_NOTIFICATION_TAG = "cofi_notification_timer"
const val COFI_TIMER_NOTIFICATION_RECIPE_DATA = "cofi_timer_notification_recipe_data"
const val COFI_TIMER_NOTIFICATION_START_TIME_DATA = "cofi_timer_notification_start_time_data"
const val COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA = "cofi_timer_notification_current_step_data"

const val WORKER_PROGRESS_STEP = "cofi_worker_progress_step_id"
const val WORKER_PROGRESS_PROGRESS = "cofi_worker_progress_progress"
const val WORKER_PROGRESS_IS_PAUSED = "cofi_worker_progress_is_paused"

class TimerWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private fun tickerFlow(duration: Long, period: Duration = 50.milliseconds) = flow {
        for (i in duration / 50 downTo 0L) {
            emit(i * 50)
            delay(period)
        }
    }

    override suspend fun doWork() = coroutineScope {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap[COFI_TIMER_NOTIFICATION_RECIPE_DATA] as Int
        val startingStepId = valueMap[COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA] as Int
        val startingTime = valueMap[COFI_TIMER_NOTIFICATION_START_TIME_DATA] as Long
        val db = AppDatabase.getInstance(context)
        val steps = db.stepDao().getStepsForRecipe(recipeId).asFlow().first()

        val initialStep =
            steps.find { it.id == startingStepId } ?: return@coroutineScope Result.failure()
        postTimerNotification(
            context,
            initialStep.toNotificationBuilder(context, 0f),
            id = COFI_TIMER_NOTIFICATION_ID + initialStep.id,
            tag = COFI_TIMER_NOTIFICATION_TAG,
        )
        suspend fun startCountDown(step: Step) {
            suspend fun goToNextStep() {
                startCountDown(steps[steps.indexOf(step) + 1])
            }
            if (step.time == null /* step.isUserInputRequired */) {
                postTimerNotification(
                    context,
                    step.toNotificationBuilder(context),
                    id = COFI_TIMER_NOTIFICATION_ID + step.id,
                    tag = COFI_TIMER_NOTIFICATION_TAG,
                )
                return
            }
            var millisLeft = 0L
            fun createCountDownTimer(millis: Long) = tickerFlow(millis)
                .distinctUntilChanged { old, new -> old == new }
                .onEach {
                    val progress = (millis - it).toFloat() / millis
                    setProgress(
                        workDataOf(
                            WORKER_PROGRESS_STEP to step.id,
                            WORKER_PROGRESS_PROGRESS to progress,
                            WORKER_PROGRESS_IS_PAUSED to false,
                        ),
                    )
                    millisLeft = it
                    postTimerNotification(
                        context,
                        step.toNotificationBuilder(
                            context,
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
                        postTimerNotification(
                            context,
                            NotificationCompat.Builder(context, TIMER_CHANNEL_ID).apply {
                                setSmallIcon(R.drawable.ic_monochrome)
                                setVisibility(VISIBILITY_PUBLIC)
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
                            id = COFI_TIMER_NOTIFICATION_ID + step.id + 1,
                            tag = COFI_TIMER_NOTIFICATION_TAG,
                        )
                    }
                    goToNextStep()
                }
                .launchIn(this) // or lifecycleScope or other

            val currentTime = SystemClock.elapsedRealtime()
            val offset = if (step.id == initialStep.id) startingTime - currentTime else 0
            val millisToCount = step.time.toLong() - offset
            val countDownTimer = createCountDownTimer(millisToCount)
            countDownTimer.start()
        }
        startCountDown(initialStep)
        return@coroutineScope Result.success()
    }
}
