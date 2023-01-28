package com.omelan.cofi.utils

import android.icu.text.DecimalFormat
import java.math.RoundingMode

fun Int.toMillis() = this * 1000

fun Int.toStringDuration(
    padMinutes: Boolean = false,
    padSeconds: Boolean = true,
    showMillis: Boolean = false,
    padMillis: Boolean = true,
): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    val millis = this % 1000 / 10
    val minutesString: String = if (padMinutes) {
        minutes.toString().padStart(2, '0')
    } else {
        minutes.toString()
    }
    val secondsString: String = if (padSeconds) {
        seconds.toString().padStart(2, '0')
    } else {
        seconds.toString()
    }

    val millisString: String = if (showMillis) {
        if (padMillis) {
            millis.toString().padStart(2, '0')
        } else {
            millis.toString()
        }
    } else {
        ""
    }

    return "$minutesString:$secondsString" + if (millisString.isNotBlank()) {
        ".$millisString"
    } else {
        ""
    }
}

fun ensureNumbersOnly(string: String, allowFloatingPoint: Boolean = false): String? {
    if (string.isEmpty()) {
        return string
    }
    val maxInt: Int = Int.MAX_VALUE / 1000
    val trimmedText = string.trim()
    try {
        if (allowFloatingPoint) {
            if (trimmedText.toFloat() in 0f..maxInt.toFloat()) {
                val decimalSeparator = DecimalFormat().decimalFormatSymbols.decimalSeparator
                val decimalPlace = string.split(decimalSeparator).getOrNull(1)
                if (decimalPlace != null && decimalPlace.length > 1) {
                    return null
                }
                return string
            } else {
                return null
            }
        } else {
            return if (trimmedText.toInt() in 0..maxInt) {
                string
            } else {
                null
            }
        }
    } catch (e: NumberFormatException) {
        return null
    }
}

fun String.safeToInt(): Int {
    return when {
        this.isBlank() -> 0
        else -> {
            try {
                this.trim().toInt()
            } catch (e: Exception) {
                0
            }
        }
    }
}

fun Float.toStringShort(): String = DecimalFormat("0.#").format(this)
fun Float.roundToDecimals(scale: Int = 1) = this.toBigDecimal()
    .setScale(scale, RoundingMode.HALF_EVEN).toFloat()
