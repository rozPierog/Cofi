package com.omelan.cofi.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

val ASKED_FOR_SUPPORT = booleanPreferencesKey("asked_for_support")
val UPDATE_NOTICE_VERSION = intPreferencesKey("update_notice_version")
val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
val NEXT_STEP_ENABLED = booleanPreferencesKey("next_step_enabled")

const val ASKED_FOR_SUPPORT_DEFAULT_VALUE = false
const val UPDATE_NOTICE_VERSION_DEFAULT_VALUE = 0
const val PIP_DEFAULT_VALUE = true
const val NEXT_STEP_ENABLED_DEFAULT_VALUE = true
