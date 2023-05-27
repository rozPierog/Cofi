package com.omelan.cofi.wearos.presentation.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import com.omelan.cofi.share.DataStore as DataStoreShared

class DataStore(private val context: Context): DataStoreShared(context) {
    fun getSyncSettingsFromPhoneSetting() = context.dataStore.data.map { preferences ->
        preferences[SYNC_SETTINGS_FROM_PHONE] ?: SYNC_SETTINGS_FROM_PHONE_DEFAULT_VALUE
    }

    suspend fun setSyncSettingsFromPhone(value: Boolean) {
        context.dataStore.edit {
            it[SYNC_SETTINGS_FROM_PHONE] = value
        }
    }
}
