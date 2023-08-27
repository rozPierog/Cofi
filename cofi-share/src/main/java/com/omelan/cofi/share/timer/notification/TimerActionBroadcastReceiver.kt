package com.omelan.cofi.share.timer.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object TimerActions {
    enum class Actions {
        ACTION_PAUSE, ACTION_NEXT, ACTION_RESUME, ACTION_STOP
    }

    fun createIntent(
        context: Context,
        notificationAction: Actions,
        recipeId: Int,
        stepId: Int?,
        alreadyDoneProgress: Float,
    ): Intent {
        val actionIntent = Intent(context, TimerActionBroadcastReceiver::class.java).apply {
            action = notificationAction.name
        }
        actionIntent.putExtra(ACTION_RECIPE_ID, recipeId)
        actionIntent.putExtra(ACTION_ALREADY_DONE_PROGRESS, alreadyDoneProgress)
        actionIntent.putExtra(ACTION_STEP_ID, stepId)
        return actionIntent
    }

    fun createPendingIntent(
        context: Context,
        notificationAction: Actions,
        recipeId: Int,
        stepId: Int?,
        alreadyDoneProgress: Float,
    ): PendingIntent? = PendingIntent.getBroadcast(
        context,
        SystemClock.elapsedRealtime().toInt(),
        createIntent(context, notificationAction, recipeId, stepId, alreadyDoneProgress),
        PendingIntent.FLAG_MUTABLE,
    )
}

private const val ACTION_RECIPE_ID = "ACTION_RECIPE_ID"
private const val ACTION_STEP_ID = "ACTION_STEP_ID"
private const val ACTION_ALREADY_DONE_PROGRESS = "ACTION_ALREADY_DONE_PROGRESS"

class TimerActionBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val recipeId = intent.getIntExtra(ACTION_RECIPE_ID, -1)
        val stepId = intent.getIntExtra(ACTION_STEP_ID, -1)
        val alreadyDoneProgress = intent.getFloatExtra(ACTION_ALREADY_DONE_PROGRESS, -1f)
        if (recipeId < 0) {
            throw Exception("recipeId is ${recipeId}, that shouldn't happen")
        }
        val workManager = WorkManager.getInstance(context)

        if (intent.action == TimerActions.Actions.ACTION_STOP.name) {
            workManager.cancelUniqueWork("cofi_$recipeId")
            NotificationManagerCompat.from(context)
                .cancel(COFI_TIMER_NOTIFICATION_TAG, COFI_TIMER_NOTIFICATION_ID + stepId)
            return
        }

        val inputData = workDataOf(
            COFI_TIMER_NOTIFICATION_RECIPE_DATA to recipeId,
            COFI_TIMER_NOTIFICATION_CURRENT_STEP_DATA to stepId,
            COFI_TIMER_NOTIFICATION_START_TIME_DATA to SystemClock.elapsedRealtime(),
            COFI_TIMER_NOTIFICATION_PROGRESS to alreadyDoneProgress,
            COFI_TIMER_NOTIFICATION_ACTION to intent.action,
        )
        val updatedWorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "cofi_$recipeId",
            ExistingWorkPolicy.REPLACE,
            updatedWorkRequest,
        )
    }
}
