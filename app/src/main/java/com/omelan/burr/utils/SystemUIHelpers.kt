package com.omelan.burr.utils

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.WindowInsetsController

object SystemUIHelpers {
    fun setSystemBarsColors(view: View, resources: Resources) {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setDarkModeSystemBars(view)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setDarkModeSystemBars(view)
            }
        }
    }

    private fun setLightModeSystemBars(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            view.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                view.systemUiVisibility = flags
            }
        }
    }

    private fun setDarkModeSystemBars(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            view.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                view.systemUiVisibility = flags
            }
        }
    }
}