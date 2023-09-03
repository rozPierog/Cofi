package com.omelan.cofi.share

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

open class DataStore(private val context: Context) {
    companion object {
        val Context.dataStore by preferencesDataStore(name = "settings")
    }

    fun getWeightSetting() = context.dataStore.data.map { preferences ->
        preferences[COMBINE_WEIGHT] ?: COMBINE_WEIGHT_DEFAULT_VALUE
    }

    fun getStepChangeSoundSetting() = context.dataStore.data.map { preferences ->
        preferences[STEP_SOUND_ENABLED] ?: STEP_SOUND_DEFAULT_VALUE
    }

    fun getStepChangeVibrationSetting() = context.dataStore.data.map { preferences ->
        preferences[STEP_VIBRATION_ENABLED] ?: STEP_VIBRATION_DEFAULT_VALUE
    }

    fun getBackgroundTimerSetting() = context.dataStore.data.map { preferences: Preferences ->
        preferences[BACKGROUND_TIMER_ENABLED]
    }

    suspend fun setStepChangeSound(value: Boolean) {
        context.dataStore.edit {
            it[STEP_SOUND_ENABLED] = value
        }
    }

    suspend fun setStepChangeVibration(value: Boolean) {
        context.dataStore.edit {
            it[STEP_VIBRATION_ENABLED] = value
        }
    }

    suspend fun toggleBackgroundTimerEnabled() {
        context.dataStore.edit {
            val currentBackgroundTimerState =
                it[BACKGROUND_TIMER_ENABLED] ?: false
            it[BACKGROUND_TIMER_ENABLED] = !currentBackgroundTimerState
        }
    }

    suspend fun setBackgroundTimerEnabled(newValue: Boolean) {
        context.dataStore.edit {
            it[BACKGROUND_TIMER_ENABLED] = newValue
        }
    }


    suspend fun toggleStepChangeSound() {
        context.dataStore.edit {
            val currentStepSoundState = it[STEP_SOUND_ENABLED] ?: STEP_SOUND_DEFAULT_VALUE
            it[STEP_SOUND_ENABLED] = !currentStepSoundState
        }
    }

    suspend fun toggleStepChangeVibration() {
        context.dataStore.edit {
            val currentStepVibrationState =
                it[STEP_VIBRATION_ENABLED] ?: STEP_VIBRATION_DEFAULT_VALUE
            it[STEP_VIBRATION_ENABLED] = !currentStepVibrationState
        }
    }

    suspend fun selectCombineMethod(combineMethod: CombineWeight) {
        context.dataStore.edit {
            it[COMBINE_WEIGHT] = combineMethod.name
        }
    }
}
