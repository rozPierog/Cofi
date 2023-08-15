package com.omelan.cofi.share.timer

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.SystemClock
import com.omelan.cofi.share.model.Step

object TimerSharedPrefsHelper {
    private const val SHARED_PREFERENCES_NAME = "TIMER_PREFS"
    private const val TIMER_RECIPE_ID = "RECIPE_ID"
    private const val TIMER_CURRENT_STEP_ID = "_CURRENT_STEP_ID"
    private const val TIMER_WEIGHT_MULTIPLIER = "_WEIGHT_MULTIPLIER"
    private const val TIMER_TIME_MULTIPLIER = "_TIME_MULTIPLIER"
    private const val TIMER_ALREADY_DONE_TIME = "_ALREADY_DONE_TIME"
    private const val TIMER_START_TIME = "_START_TIME"
    private const val TIMER_IS_PAUSED = "_IS_PAUSED"

    data class TimerData(
        val recipeId: Int,
        val currentStepId: Int,
        val weightMultiplier: Float,
        val timeMultiplier: Float,
        val alreadyDoneTime: Long = 0,
        val startTime: Long = SystemClock.elapsedRealtime(),
        val isPaused: Boolean = true,
    ) {
        fun pause(): TimerData {
            val now = SystemClock.elapsedRealtime()
            return this.copy(isPaused = true, alreadyDoneTime = alreadyDoneTime + (now - startTime))
        }

        fun start(): TimerData {
            val now = SystemClock.elapsedRealtime()
            val updatedTimerData = this.copy(isPaused = false, startTime = now)
//            context.startTimerWorker(updatedTimerData)
            return updatedTimerData
        }

        fun changeToStep(step: Step): TimerData {
            val nextStepId = (step.orderInRecipe ?: 0) + 1
            return if (step.isUserInputRequired) {
                this.copy(currentStepId = nextStepId).pause()
            } else {
                this.copy(currentStepId = nextStepId).start()
            }
        }

        fun propsToMap(): Map<String, Any> = mutableMapOf(
            TIMER_RECIPE_ID to recipeId,
            "${recipeId}${TIMER_CURRENT_STEP_ID}" to currentStepId,
            "${recipeId}${TIMER_WEIGHT_MULTIPLIER}" to weightMultiplier,
            "${recipeId}${TIMER_TIME_MULTIPLIER}" to timeMultiplier,
            "${recipeId}${TIMER_ALREADY_DONE_TIME}" to alreadyDoneTime,
            "${recipeId}${TIMER_START_TIME}" to startTime,
            "${recipeId}${TIMER_IS_PAUSED}" to isPaused,
        )
    }

    fun Map<String, *>.toTimerData(): TimerData {
        val recipeId: Int = this[TIMER_RECIPE_ID] as Int
        var currentStepId: Int? = null
        var weightMultiplier: Float? = null
        var timeMultiplier: Float? = null
        var alreadyDoneTime: Long? = null
        var startTime: Long? = null
        var isPaused: Boolean? = null
        forEach { (key, value) ->
            when (key) {
                "${recipeId}${TIMER_CURRENT_STEP_ID}" -> currentStepId = value as Int
                "${recipeId}${TIMER_WEIGHT_MULTIPLIER}" -> weightMultiplier = value as Float
                "${recipeId}${TIMER_TIME_MULTIPLIER}" -> timeMultiplier = value as Float
                "${recipeId}${TIMER_ALREADY_DONE_TIME}" -> alreadyDoneTime = value as Long
                "${recipeId}${TIMER_START_TIME}" -> startTime = value as Long
                "${recipeId}${TIMER_IS_PAUSED}" -> isPaused = value as Boolean
            }
        }
        if (
            currentStepId != null &&
            weightMultiplier != null &&
            timeMultiplier != null &&
            alreadyDoneTime != null &&
            startTime != null &&
            isPaused != null
        ) {
            return TimerData(
                recipeId = recipeId,
                currentStepId = currentStepId!!,
                weightMultiplier = weightMultiplier!!,
                timeMultiplier = timeMultiplier!!,
                alreadyDoneTime = alreadyDoneTime!!,
                startTime = startTime!!,
                isPaused = isPaused!!,
            )
        }
        throw Exception("Something went wrong when converting back to TimerData")
    }

    fun saveTimerToSharedPrefs(context: Context, timerData: TimerData) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().run {
            timerData.propsToMap().forEach { (key, value) ->
                when (value) {
                    is Long -> putLong(key, value)
                    is Int -> putInt(key, value)
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    else -> throw Error("Unexpected type in params: ${value::class.simpleName}")
                }
            }
            apply()
        }
    }

    fun getTimerDataFromSharedPrefs(context: Context, recipeId: Int): TimerData {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        return sharedPrefs.all.toTimerData()
    }

    fun observeTimerPreference(
        context: Context,
        onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }
}
