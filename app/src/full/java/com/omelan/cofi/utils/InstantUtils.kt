@file:Suppress("EmptyMethod", "SameReturnValue")

package com.omelan.cofi.utils

import android.app.Activity
import android.content.Context

object InstantUtils {
    fun isInstantApp(@Suppress("UNUSED_PARAMETER") context: Context) = false

    fun showInstallPrompt(@Suppress("UNUSED_PARAMETER") activity: Activity) {}
}
