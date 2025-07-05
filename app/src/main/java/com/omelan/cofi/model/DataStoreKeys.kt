package com.omelan.cofi.model

import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import org.json.JSONObject

val ASKED_FOR_SUPPORT = booleanPreferencesKey("asked_for_support")
val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
val WAVY_TIMER = booleanPreferencesKey("wavy_timer")
val UPDATE_NOTICE_VERSION = intPreferencesKey("update_notice_version")
val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
val NEXT_STEP_ENABLED = booleanPreferencesKey("next_step_enabled")
val DISMISSED_INFO = stringPreferencesKey("dismissed_info_boxes")
val CUSTOM_MULTIPLIER = stringSetPreferencesKey("custom_multiplier")

const val ASKED_FOR_SUPPORT_DEFAULT_VALUE = false
const val DYNAMIC_THEME_DEFAULT_VALUE = true
const val UPDATE_NOTICE_VERSION_DEFAULT_VALUE = 0
const val PIP_DEFAULT_VALUE = true
val NEXT_STEP_ENABLED_DEFAULT_VALUE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
const val WAVY_TIMER_DEFAULT_VALUE = true
const val DISMISSED_INFO_DEFAULT_VALUE = "{}"
val CUSTOM_MULTIPLIER_DEFAULT_VALUE = setOf("0.5", "1", "2", "3")

fun stringToDismissedInfoBoxes(string: String): Map<String, Boolean> {
    val jsonObject = JSONObject(string)
    val map = mutableMapOf<String, Boolean>()
    jsonObject.keys().forEach {
        map[it] = jsonObject.get(it) as Boolean
    }
    return map
}
