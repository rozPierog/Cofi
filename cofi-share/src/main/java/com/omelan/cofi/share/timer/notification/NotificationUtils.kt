package com.omelan.cofi.share.timer.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.utils.appDeepLinkUrl
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.share.utils.toStringDuration
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

fun createValueText(context: Context, step: Step, currentProgress: Float): String {
    val weightMultiplier = 1f
    val alreadyDoneWeight = 0f
    val valueText = step.value?.let {
        val currentValueFromProgress = (it * currentProgress)

        val currentValueWithMultiplier =
            (currentValueFromProgress * weightMultiplier) + alreadyDoneWeight

        val currentTargetValue = (it * weightMultiplier)
        val targetString = currentTargetValue.toStringShort()
        val shouldShowDecimals = targetString.contains(".")
        val currentValueString: Number = if (shouldShowDecimals) {
            currentValueWithMultiplier.roundToDecimals()
        } else {
            currentValueWithMultiplier.roundToInt()
        }
        context.getString(
            R.string.timer_progress_weight,
            currentValueString,
            targetString,
        )
    } ?: ""

    val timeText =
        step.time?.let { " (${it.toStringDuration()})" } ?: ""
    return "$valueText$timeText"
}

fun Step.toNotificationBuilder(
    context: Context,
    recipe: Recipe,
    nextStepId: Int?,
    currentProgress: Float = 0f,
    isPaused: Boolean = false,
): NotificationCompat.Builder {
    val step = this
    val builder =
        NotificationCompat.Builder(context, TIMER_CHANNEL_ID).run {
            setContentTitle("${recipe.name}: ${step.name}")
            setContentText(createValueText(context, step, currentProgress))
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
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_STOP,
                            recipeId = recipeId,
                            stepId = step.id,
                            alreadyDoneProgress = currentProgress,
                        ),
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
        }
    val taskDetailIntent = Intent(
        Intent.ACTION_VIEW,
        "$appDeepLinkUrl/recipe/$recipeId".toUri(),
    )
    val pendingIntent =
        PendingIntent.getActivity(context, 0, taskDetailIntent, PendingIntent.FLAG_IMMUTABLE)
    builder.setContentIntent(pendingIntent)
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
