package com.omelan.cofi.share.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class Haptics(context: Context) {

    private val vibrator =
        context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//
//    fun click() {
//        vibrateCompat(VibrationEffect.EFFECT_CLICK)
//    }
//
    fun heavyClick() {
        vibrator.vibrateCompat(VibrationEffect.EFFECT_HEAVY_CLICK)
    }
//
//    fun doubleClick() {
//        vibrateCompat(VibrationEffect.EFFECT_DOUBLE_CLICK)
//    }
//
//    fun tick() {
//        vibrateCompat(VibrationEffect.EFFECT_TICK)
//    }

    fun progress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrateCompat(vibrationEffect = VibrationEffect.createOneShot(300, -1))
            return
        }
        vibrator.vibrateCompat()
    }

    private fun Vibrator.vibrateCompat(
        effectId: Int? = null,
        vibrationEffect: VibrationEffect? = null,
    ) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            when {
                effectId != null -> {
                    this.vibrate(VibrationEffect.createPredefined(effectId))
                }
                vibrationEffect != null -> {
                    this.vibrate(vibrationEffect)
                }
                else -> {
                    this.vibrate(300)
                }
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            when {
                vibrationEffect != null -> {
                    this.vibrate(vibrationEffect)
                }
                else -> {
                    this.vibrate(300)
                }
            }
        else ->
            this.vibrate(300)
    }
}
