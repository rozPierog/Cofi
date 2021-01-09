package com.omelan.cofi.utils

import android.view.View
import android.view.Window

object SystemUIHelpers {
    fun setStatusBarIconsTheme(
        window: Window,
        darkIcons: Boolean,
    ) {
        @Suppress("DEPRECATION")
        if (darkIcons) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}