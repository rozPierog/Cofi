package com.omelan.cofi.share

import androidx.annotation.StringRes
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import org.json.JSONObject

val STEP_SOUND_ENABLED = booleanPreferencesKey("ding_enabled")
val STEP_VIBRATION_ENABLED = booleanPreferencesKey("step_vibration_enabled")
val COMBINE_WEIGHT = stringPreferencesKey("combine_weight")
val DISMISSED_INFO = stringPreferencesKey("dismissed_info_boxes")
val SYNC_SETTINGS_FROM_PHONE = booleanPreferencesKey("sync_settings_from_phone")

const val STEP_SOUND_DEFAULT_VALUE = true
const val STEP_VIBRATION_DEFAULT_VALUE = true
const val DISMISSED_INFO_DEFAULT_VALUE = "{}"
val COMBINE_WEIGHT_DEFAULT_VALUE = CombineWeight.WATER.name
const val SYNC_SETTINGS_FROM_PHONE_DEFAULT_VALUE = true

enum class CombineWeight(@StringRes val settingsStringId: Int) {
    ALL(R.string.settings_combine_weight_all),
    WATER(R.string.settings_combine_weight_water),
    NONE(R.string.settings_combine_weight_none),
}

fun stringToCombineWeight(string: String) =
    when (string) {
        CombineWeight.ALL.name -> CombineWeight.ALL
        CombineWeight.WATER.name -> CombineWeight.WATER
        CombineWeight.NONE.name -> CombineWeight.NONE
        else -> CombineWeight.WATER
    }

fun stringToDismissedInfoBoxes(string: String): Map<String, Boolean> {
    val jsonObject = JSONObject(string)
    val map = mutableMapOf<String, Boolean>()
    jsonObject.keys().forEach {
        map[it] = jsonObject.get(it) as Boolean
    }
    return map
}
