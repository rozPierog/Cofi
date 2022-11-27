package com.omelan.cofi.share

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStore(private val context: Context) {
    companion object {
        val Context.dataStore by preferencesDataStore(
            name = "settings",
        )
    }

    fun getPiPSetting() = context.dataStore.data.map { preferences ->
        preferences[PIP_ENABLED] ?: PIP_DEFAULT_VALUE
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

    suspend fun togglePipSetting() {
        context.dataStore.edit {
            val currentPiPState = it[PIP_ENABLED] ?: PIP_DEFAULT_VALUE
            it[PIP_ENABLED] = !currentPiPState
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
