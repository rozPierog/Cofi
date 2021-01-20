package com.omelan.cofi

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
val COMBINE_WEIGHT = stringPreferencesKey("combine_weight")

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