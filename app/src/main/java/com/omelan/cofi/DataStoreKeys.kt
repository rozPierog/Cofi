package com.omelan.cofi

import androidx.datastore.preferences.core.preferencesKey

val PIP_ENABLED = preferencesKey<Boolean>("pip_enabled")
val COMBINE_WEIGHT = preferencesKey<String>("combine_weight")

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