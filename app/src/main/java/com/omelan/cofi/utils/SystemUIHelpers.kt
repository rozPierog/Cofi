package com.omelan.cofi.utils

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

object SystemUIHelpers {
    fun setStatusBarIconsTheme(
        activity: Activity,
        darkIcons: Boolean,
    ) {
        WindowInsetsControllerCompat(
            activity.window,
            activity.window.decorView
        ).isAppearanceLightStatusBars = darkIcons
    }
}