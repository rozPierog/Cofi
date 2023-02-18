package com.omelan.cofi.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import org.json.JSONObject

val ASKED_FOR_SUPPORT = booleanPreferencesKey("asked_for_support")
val UPDATE_NOTICE_VERSION = intPreferencesKey("update_notice_version")
val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
val NEXT_STEP_ENABLED = booleanPreferencesKey("next_step_enabled")
val DISMISSED_INFO = stringPreferencesKey("dismissed_info_boxes")

const val ASKED_FOR_SUPPORT_DEFAULT_VALUE = false
const val UPDATE_NOTICE_VERSION_DEFAULT_VALUE = 0
const val PIP_DEFAULT_VALUE = true
const val NEXT_STEP_ENABLED_DEFAULT_VALUE = true
const val DISMISSED_INFO_DEFAULT_VALUE = "{}"

fun stringToDismissedInfoBoxes(string: String): Map<String, Boolean> {
    val jsonObject = JSONObject(string)
    val map = mutableMapOf<String, Boolean>()
    jsonObject.keys().forEach {
        map[it] = jsonObject.get(it) as Boolean
    }
    return map
}
