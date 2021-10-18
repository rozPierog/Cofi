package com.omelan.cofi

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
val DING_ENABLED = booleanPreferencesKey("ding_enabled")
val COMBINE_WEIGHT = stringPreferencesKey("combine_weight")

const val PIP_DEFAULT_VALUE = true
const val DING_DEFAULT_VALUE = true
val COMBINE_WEIGHT_DEFAULT_VALUE = CombineWeight.WATER.name

enum class CombineWeight {
    ALL {
        override val settingsStringId: Int
            get() = R.string.settings_combine_weight_all
    },
    WATER {
        override val settingsStringId: Int
            get() = R.string.settings_combine_weight_water
    },
    NONE {
        override val settingsStringId: Int
            get() = R.string.settings_combine_weight_none
    };

    abstract val settingsStringId: Int
}

fun stringToCombineWeight(string: String) =
    when (string) {
        CombineWeight.ALL.name -> CombineWeight.ALL
        CombineWeight.WATER.name -> CombineWeight.WATER
        CombineWeight.NONE.name -> CombineWeight.NONE
        else -> CombineWeight.WATER
    }