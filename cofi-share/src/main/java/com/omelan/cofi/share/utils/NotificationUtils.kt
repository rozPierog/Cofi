package com.omelan.cofi.share.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.work.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

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
    currentProgress: Float,
): NotificationCompat.Builder {
    val step = this
    val builder =
        NotificationCompat.Builder(context, "timer").run {
            setSmallIcon(step.type.iconRes)
            setVisibility(VISIBILITY_PUBLIC)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1,
                )
            }
        }
        notify(tag, id, notificationBuilder.build())
    }
}

fun Context.startTimerWorker(
    recipeId: Int,
    currentProgress: Float,
    currentStepId: Int,
) {
    val inputData = Data.Builder().apply {
        putInt("recipeId", recipeId)
        putFloat("currentProgress", currentProgress)
        putInt("currentStepId", currentStepId)
    }.build()
    val timerWorker =
        OneTimeWorkRequest.Builder(TimerWorker::class.java).setInputData(inputData).build()
    val workManager = WorkManager.getInstance(this)
    workManager.enqueue(timerWorker)
}

const val COFI_TIMER_NOTIFICATION_ID = 2137
const val COFI_TIMER_NOTIFICATION_TAG = "cofi_notification_timer"
const val COFI_TIMER_NOTIFICATION_PROGRESS_DATA = "cofi_timer_notification_progress_data"
const val COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA = "cofi_timer_notification_current_step_data"

class TimerWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap["recipeId"] as Int
        val startingStepId = valueMap["currentStepId"] as Int
        val initialProgress = valueMap["currentProgress"] as Float
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.Main) {
            db.stepDao().getStepsForRecipe(recipeId).observeForever { steps ->
                val initialStep = steps.find { it.id == startingStepId } ?: return@observeForever
                postTimerNotification(
                    context,
                    initialStep.toNotificationBuilder(context, initialProgress),
                    id = COFI_TIMER_NOTIFICATION_ID + initialStep.id,
                    tag = COFI_TIMER_NOTIFICATION_TAG,
                )
                fun startCountDown(step: Step) {
                    fun goToNextStep() {
                        startCountDown(steps[steps.indexOf(step) + 1])
                    }
                    if (step.time == null) {
                        // TODO: Handle timeless steps (button to resume)
                        goToNextStep()
                        return
                    }
                    val millisToCount = step.time.toLong() *
                            (if (step.id == initialStep.id) initialProgress.toLong() else 1)
                    val countDownTimer = object : CountDownTimer(millisToCount, 1) {
                        override fun onTick(millisUntilFinished: Long) {
                            val currentProgress = 1f - (millisUntilFinished.toFloat() / step.time)
                            postTimerNotification(
                                context,
                                step.toNotificationBuilder(
                                    context,
                                    currentProgress,
                                ),
                                id = COFI_TIMER_NOTIFICATION_ID + step.id,
                                tag = COFI_TIMER_NOTIFICATION_TAG,
                            )
                            launch {
                                setProgress(
                                    workDataOf(
                                        COFI_TIMER_NOTIFICATION_PROGRESS_DATA to currentProgress,
                                        COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA to step.id,
                                    ),
                                )
                            }
                        }

                        override fun onFinish() {
                            NotificationManagerCompat.from(context)
                                .cancel(
                                    COFI_TIMER_NOTIFICATION_TAG,
                                    COFI_TIMER_NOTIFICATION_ID + step.id,
                                )
                            if (steps.last().id == step.id) {
                                postTimerNotification(
                                    context,
                                    NotificationCompat.Builder(context, "timer").apply {
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
                                return
                            }
                            goToNextStep()
                        }
                    }
                    countDownTimer.start()
                }
                startCountDown(initialStep)
            }
        }
        return Result.success()
    }
}
