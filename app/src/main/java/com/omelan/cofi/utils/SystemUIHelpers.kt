package com.omelan.cofi.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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

    private fun setNavigationBarTheme(light: Boolean, activity: Activity) {
        WindowInsetsControllerCompat(
            activity.window,
            activity.window.decorView
        ).isAppearanceLightNavigationBars = light
    }

    fun setNavigationBarColor(color: Color, activity: Activity?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.runOnUiThread {
                val lightIcons = color.luminance() > 0.5
                setNavigationBarTheme(lightIcons, activity)
                val (red, green, blue, alpha) = color
                val compatColor = android.graphics.Color.argb(alpha, red, green, blue)
                val fromBar = activity.window.navigationBarColor
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val fromDivider = activity.window.navigationBarDividerColor
                    val dividerAnimation = ValueAnimator.ofArgb(fromDivider, compatColor)
                    val barAnimation = ValueAnimator.ofArgb(fromBar, compatColor)

                    dividerAnimation.addUpdateListener { animator ->
                        activity.window.navigationBarDividerColor = animator.animatedValue as Int
                    }
                    barAnimation.addUpdateListener { valueAnimator ->
                        activity.window.navigationBarColor = valueAnimator.animatedValue as Int
                    }
                    dividerAnimation.start()
                    barAnimation.start()
                } else {
                    val colorAnimation = ValueAnimator.ofArgb(fromBar, compatColor)
                    colorAnimation.addUpdateListener { animator ->
                        activity.window.navigationBarColor = animator.animatedValue as Int
                    }
                    colorAnimation.start()
                }
            }
        }
    }
}