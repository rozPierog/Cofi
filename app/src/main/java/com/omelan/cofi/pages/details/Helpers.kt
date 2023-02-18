@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.omelan.cofi.pages.details

import android.app.Activity
import android.app.PictureInPictureParams
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import com.omelan.cofi.MainActivity
import com.omelan.cofi.model.DataStore
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
suspend fun setPiPSettings(activity: Activity?, isTimerRunning: Boolean, sourceRectHint: Rect?) {
    if (activity !is MainActivity) {
        return
    }
    val isPiPEnabled = DataStore(activity).getPiPSetting().first()
    if (!isPiPEnabled) {
        return
    }
    if (!isTimerRunning && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        activity.setPictureInPictureParams(
            PictureInPictureParams.Builder().setAutoEnterEnabled(false).build(),
        )
    } else {
        activity.setPictureInPictureParams(
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(1, 1))
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setAutoEnterEnabled(true)
                        setSourceRectHint(sourceRectHint)
                        setSeamlessResizeEnabled(false)
                    }
                }.build(),
        )
    }
}
