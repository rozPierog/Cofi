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
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.share.utils.toStringDuration
import com.omelan.cofi.share.utils.toStringShort
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

const val TIMER_CHANNEL_ID = "cofi_timer_notification"
fun Context.createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            TIMER_CHANNEL_ID,
            resources.getString(R.string.notification_channel_timer_name),
            NotificationManager.IMPORTANCE_HIGH,
        )
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}

fun createContentText(
    context: Context,
    step: Step,
    currentProgress: Float,
    weightMultiplier: Float,
    timeMultiplier: Float,
    alreadyDoneWeight: Float,
): String? {
    val valueText = step.value?.let {
        val currentValueFromProgress = (it * currentProgress)

        val currentValueWithMultiplier =
            (currentValueFromProgress * weightMultiplier) + alreadyDoneWeight

        val currentTargetValue = (it * weightMultiplier) + alreadyDoneWeight
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
        step.time?.let { " (${(it * timeMultiplier).roundToInt().toStringDuration()})" } ?: ""
    return "$valueText$timeText".trim().ifBlank { null }
}

fun createDoneNotification(recipe: Recipe, context: Context) =
    NotificationCompat.Builder(context, TIMER_CHANNEL_ID).apply {
        setContentTitle(context.getString(R.string.timer_enjoy))
        setSubText(recipe.name)
        setSmallIcon(R.drawable.ic_monochrome)
        setVisibility(VISIBILITY_PUBLIC)
        setOnlyAlertOnce(false)
        setAutoCancel(true)
        setOngoing(false)
        setVibrate(longArrayOf(300))
        setTimeoutAfter(10.minutes.inWholeMilliseconds)
        color = ResourcesCompat.getColor(
            context.resources,
            R.color.ic_launcher_background,
            null,
        )
        setColorized(false)
    }

fun Step.toNotificationBuilder(
    context: Context,
    recipe: Recipe,
    weightMultiplier: Float,
    timeMultiplier: Float,
    nextStepId: Int?,
    alreadyDoneWeight: Float,
    currentProgress: Float = 0f,
    isPaused: Boolean = false,
): NotificationCompat.Builder {
    val step = this
    return NotificationCompat.Builder(context, TIMER_CHANNEL_ID).run {
        setSubText(recipe.name)
        setContentTitle(step.name)
        setContentText(
            createContentText(
                context,
                step,
                currentProgress,
                weightMultiplier,
                timeMultiplier,
                alreadyDoneWeight,
            ),
        )
        setSmallIcon(step.type.iconRes)
        setVisibility(VISIBILITY_PUBLIC)
        setCategory(NotificationCompat.CATEGORY_ALARM)
        setOnlyAlertOnce(true)
        setAutoCancel(true)
        // TODO: Make it optional to allow for bridging notification
        // https://developer.android.com/training/wearables/notifications/bridger
        setOngoing(true)
        setVibrate(longArrayOf(300))
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
        when {
            step.isUserInputRequired -> {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        context.resources.getString(R.string.notification_action_continue),
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_NEXT,
                            TimerData(
                                recipeId,
                                stepId = nextStepId,
                                alreadyDoneProgress = 0f,
                                weightMultiplier = weightMultiplier,
                                timeMultiplier = timeMultiplier,
                            ),
                        ),
                    ),
                )
            }

            isPaused -> {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        context.resources.getString(R.string.notification_action_continue),
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_RESUME,
                            TimerData(
                                recipeId,
                                stepId = step.id,
                                alreadyDoneProgress = currentProgress,
                                weightMultiplier = weightMultiplier,
                                timeMultiplier = timeMultiplier,
                            ),
                        ),
                    ),
                )
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        context.resources.getString(R.string.notification_action_stop),
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_STOP,
                            TimerData(
                                recipeId,
                                stepId = step.id,
                                alreadyDoneProgress = currentProgress,
                                weightMultiplier = weightMultiplier,
                                timeMultiplier = timeMultiplier,
                            ),
                        ),
                    ),
                )
            }

            else -> {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_monochrome,
                        context.resources.getString(R.string.notification_action_pause),
                        TimerActions.createPendingIntent(
                            context,
                            TimerActions.Actions.ACTION_PAUSE,
                            TimerData(
                                recipeId,
                                stepId = step.id,
                                alreadyDoneProgress = currentProgress,
                                weightMultiplier = weightMultiplier,
                                timeMultiplier = timeMultiplier,
                            ),
                        ),
                    ),
                )
            }
        }
    }
}

fun postTimerNotification(
    context: Context,
    notificationBuilder: NotificationCompat.Builder,
    id: Int = System.currentTimeMillis().toInt(),
    tag: String = id.toString(),
) {
    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED
    ) {
        context.createChannel()
        NotificationManagerCompat.from(context).notify(tag, id, notificationBuilder.build())
    }
}
