package com.omelan.cofi.share.timer.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.work.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.share.utils.toStringShort
import java.util.UUID
import kotlin.math.roundToInt

const val TIMER_CHANNEL_ID = "cofi_timer_notification"
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
    workerId: UUID,
    nextStepId: Int?,
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
            val workManager = WorkManager.getInstance(context)

            if (step.isUserInputRequired) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        "Continue",
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_NEXT,
                            recipeId = recipeId,
                            stepId = nextStepId,
                            alreadyDoneProgress = 0f,
                        ),
                    ),
                )
            } else if (isPaused) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        "Continue",
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_RESUME,
                            recipeId = recipeId,
                            stepId = step.id,
                            alreadyDoneProgress = currentProgress,
                        ),
                    ),
                )
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        "Stop",
                        workManager.createCancelPendingIntent(workerId),
                    ),
                )
            } else {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        "Pause",
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_PAUSE,
                            recipeId = recipeId,
                            stepId = step.id,
                            alreadyDoneProgress = currentProgress,
                        ),
                    ),
                )
            }
//            val bundle = Bundle()
//            bundle.putFloat("animatedValue", currentProgress)
//            bundle.putInt("currentStepId", step.id)
//            setExtras(bundle)
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
    val inputData = workDataOf(
        COFI_TIMER_NOTIFICATION_RECIPE_DATA to recipeId,
        COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA to stepId,
        COFI_TIMER_NOTIFICATION_START_TIME_DATA to startingTime,
    )
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

