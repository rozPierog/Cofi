package com.omelan.burr.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

@SuppressLint("InlinedApi")
class Haptics(context: Context) {

    private val vibrator =
        context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun click() {
        createCompatVibration(VibrationEffect.EFFECT_CLICK)
    }

    fun error() {
        createCompatVibration(VibrationEffect.EFFECT_HEAVY_CLICK)
    }

    fun warning() {
        createCompatVibration(VibrationEffect.EFFECT_DOUBLE_CLICK)
    }

    fun tick() {
        createCompatVibration(VibrationEffect.EFFECT_TICK)
    }

    private fun createCompatVibration(effectId: Int) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        else -> vibrator.vibrate(25)
    }
}
