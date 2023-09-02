package com.omelan.cofi.share.timer.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.os.SystemClock
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.parcelize.Parcelize


@Parcelize
data class TimerData(
    val recipeId: Int,
    val stepId: Int?,
    val alreadyDoneProgress: Float,
    val weightMultiplier: Float,
    val timeMultiplier: Float,
) : Parcelable

object TimerActions {
    enum class Actions {
        ACTION_PAUSE, ACTION_NEXT, ACTION_RESUME, ACTION_STOP
    }

    fun createIntent(
        context: Context,
        notificationAction: Actions,
        timerData: TimerData,
    ): Intent {
        val actionIntent = Intent(context, TimerActionBroadcastReceiver::class.java).apply {
            action = notificationAction.name
        }
        actionIntent.putExtra(ACTION_DATA, timerData)
        return actionIntent
    }

    fun createPendingIntent(
        context: Context,
        notificationAction: Actions,
        timerData: TimerData,
    ): PendingIntent? = PendingIntent.getBroadcast(
        context,
        SystemClock.elapsedRealtime().toInt(),
        createIntent(context, notificationAction, timerData),
        PendingIntent.FLAG_MUTABLE,
    )
}

private const val ACTION_DATA = "ACTION_DATA"

class TimerActionBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ACTION_DATA, TimerData::class.java)
        } else {
            intent.getParcelableExtra(ACTION_DATA) as TimerData?
        }
        if (timerData == null) {
            return
        }
        if (timerData.recipeId < 0) {
            throw Exception("recipeId is ${timerData.recipeId}, that shouldn't happen")
        }
        val workManager = WorkManager.getInstance(context)

        if (intent.action == TimerActions.Actions.ACTION_STOP.name && timerData.stepId != null) {
            workManager.cancelUniqueWork("cofi_${timerData.recipeId}")
            NotificationManagerCompat.from(context)
                .cancel(COFI_TIMER_NOTIFICATION_TAG, COFI_TIMER_NOTIFICATION_ID + timerData.stepId)
            return
        }
        val inputData = workDataOf(
            COFI_TIMER_NOTIFICATION_RECIPE_DATA to timerData.recipeId,
            COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA to timerData.stepId,
            COFI_TIMER_NOTIFICATION_START_TIME_DATA to SystemClock.elapsedRealtime(),
            COFI_TIMER_NOTIFICATION_PROGRESS to timerData.alreadyDoneProgress,
            COFI_TIMER_NOTIFICATION_WEIGHT_MULTIPLIER to timerData.weightMultiplier,
            COFI_TIMER_NOTIFICATION_TIME_MULTIPLIER to timerData.timeMultiplier,
            COFI_TIMER_NOTIFICATION_ACTION to intent.action,
        )
        val updatedWorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "cofi_${timerData.recipeId}",
            ExistingWorkPolicy.REPLACE,
            updatedWorkRequest,
        )
    }
}
