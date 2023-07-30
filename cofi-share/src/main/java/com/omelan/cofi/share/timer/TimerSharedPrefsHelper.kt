package com.omelan.cofi.share.timer

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.SystemClock
import com.omelan.cofi.share.model.Step

object TimerSharedPrefsHelper {
    private const val SHARED_PREFERENCES_NAME = "TIMER_PREFS"
    private const val START_TIME_KEY = "_START_TIME"
    private const val STEP_ID_KEY = "_STEP_ID"
    private const val STEP_PROGRESS_KEY = "_STEP_PROGRESS"
    fun saveTimerToSharedPrefs(
        context: Context,
        recipeId: Int,
        currentStep: Step,
        currentProgress: Float,
    ) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().run {
            putLong("$recipeId$START_TIME_KEY", SystemClock.elapsedRealtime())
            putFloat("$recipeId$STEP_PROGRESS_KEY", currentProgress)
            putInt("$recipeId$STEP_ID_KEY", currentStep.id)
            apply()
        }
    }

    data class TimerData(
        val startTime: Long,
        val startingStepId: Int,
        val startingStepProgress: Float,
    )

    fun getTimerDataFromSharedPrefs(context: Context, recipeId: Int): TimerData {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        return TimerData(
            sharedPrefs.getLong("$recipeId$START_TIME_KEY", 0),
            sharedPrefs.getInt("$recipeId$STEP_ID_KEY", 0),
            sharedPrefs.getFloat("$recipeId$STEP_PROGRESS_KEY", 0f),
        )
    }
}
