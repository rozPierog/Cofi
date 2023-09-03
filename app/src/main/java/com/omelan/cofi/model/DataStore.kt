package com.omelan.cofi.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import com.omelan.cofi.share.DataStore as DataStoreShared

class DataStore(val context: Context) : DataStoreShared(context) {
    fun getPiPSetting() = context.dataStore.data.map { preferences ->
        preferences[PIP_ENABLED] ?: PIP_DEFAULT_VALUE
    }

    fun getNextStepSetting() = context.dataStore.data.map { preferences ->
        preferences[NEXT_STEP_ENABLED] ?: NEXT_STEP_ENABLED_DEFAULT_VALUE
    }
    fun getAskedForSupport() = context.dataStore.data.map {
        it[ASKED_FOR_SUPPORT] ?: ASKED_FOR_SUPPORT_DEFAULT_VALUE
    }

    fun getLastSeenUpdateNoticeVersion() = context.dataStore.data.map {
        it[UPDATE_NOTICE_VERSION] ?: UPDATE_NOTICE_VERSION_DEFAULT_VALUE
    }

    fun getDismissedInfoBoxes() = context.dataStore.data.map {
        stringToDismissedInfoBoxes(it[DISMISSED_INFO] ?: DISMISSED_INFO_DEFAULT_VALUE)
    }

    suspend fun setDismissedInfoBoxes(newValue: Map<String, Boolean>) = context.dataStore.edit {
        it[DISMISSED_INFO] = JSONObject(newValue).toString()
    }

    suspend fun toggleNextStepEnabled() {
        context.dataStore.edit {
            val currentNextStepEnabledState =
                it[NEXT_STEP_ENABLED] ?: NEXT_STEP_ENABLED_DEFAULT_VALUE
            it[NEXT_STEP_ENABLED] = !currentNextStepEnabledState
        }
    }
    suspend fun togglePipSetting() {
        context.dataStore.edit {
            val currentPiPState = it[PIP_ENABLED] ?: PIP_DEFAULT_VALUE
            it[PIP_ENABLED] = !currentPiPState
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
