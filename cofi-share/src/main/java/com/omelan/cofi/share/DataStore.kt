package com.omelan.cofi.share

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class DataStore(private val context: Context) {
    companion object {
        val Context.dataStore by preferencesDataStore(name = "settings")
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

    fun getDismissedInfoBoxes() = context.dataStore.data.map {
        stringToDismissedInfoBoxes(it[DISMISSED_INFO] ?: DISMISSED_INFO_DEFAULT_VALUE)
    }

    fun getSyncSettingsFromPhoneSetting() = context.dataStore.data.map { preferences ->
        preferences[SYNC_SETTINGS_FROM_PHONE] ?: SYNC_SETTINGS_FROM_PHONE_DEFAULT_VALUE
    }

    fun getAskedForSupport() = context.dataStore.data.map {
        it[ASKED_FOR_SUPPORT] ?: ASKED_FOR_SUPPORT_DEFAULT_VALUE
    }

    fun getLastSeenUpdateNoticeVersion() = context.dataStore.data.map {
        it[UPDATE_NOTICE_VERSION] ?: UPDATE_NOTICE_VERSION_DEFAULT_VALUE
    }


    suspend fun setDismissedInfoBoxes(newValue: Map<String, Boolean>) = context.dataStore.edit {
        it[DISMISSED_INFO] = JSONObject(newValue).toString()
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

    suspend fun setSyncSettingsFromPhone(value: Boolean) {
        context.dataStore.edit {
            it[SYNC_SETTINGS_FROM_PHONE] = value
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

    suspend fun setAskedForSupport() {
        context.dataStore.edit {
            it[ASKED_FOR_SUPPORT] = true
        }
    }

    suspend fun setLastSeenUpdateNoticeVersion(versionCode: Int) {
        context.dataStore.edit {
            it[UPDATE_NOTICE_VERSION] = versionCode
        }
    }
}
