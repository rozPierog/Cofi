package com.omelan.cofi.share

import androidx.annotation.StringRes
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val STEP_SOUND_ENABLED = booleanPreferencesKey("ding_enabled")
val STEP_VIBRATION_ENABLED = booleanPreferencesKey("step_vibration_enabled")
val COMBINE_WEIGHT = stringPreferencesKey("combine_weight")
val BACKGROUND_TIMER_ENABLED = booleanPreferencesKey("background_timer_enabled")

const val STEP_SOUND_DEFAULT_VALUE = true
const val STEP_VIBRATION_DEFAULT_VALUE = true
val COMBINE_WEIGHT_DEFAULT_VALUE = CombineWeight.WATER.name

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
