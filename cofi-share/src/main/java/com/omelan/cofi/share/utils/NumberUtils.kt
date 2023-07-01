package com.omelan.cofi.share.utils

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

fun ensureNumbersOnly(newValue: String, oldValue: String, allowFloat: Boolean): String {
    val maxInt: Int = Int.MAX_VALUE / 1000
    if (newValue.isEmpty()) {
        return newValue
    }
    if (!allowFloat) {
        val convertedNumber = newValue.toIntOrNull()
        return if (convertedNumber != null && convertedNumber in 0..maxInt) {
            newValue
        } else {
            oldValue
        }
    }

    val decimalSeparator = DecimalFormat().decimalFormatSymbols.decimalSeparator

    // Case when user clicked ⌫ before '.'
    if (newValue == oldValue.filter { it != decimalSeparator }) {
        return oldValue
    }
    // Check if there is more than one decimal separator and try to rescue value
    val fixedNewValue = if (newValue.count { it == decimalSeparator } > 1) {
        newValue.substringBeforeLast(".", missingDelimiterValue = "")
    } else newValue

    val convertedNumber = fixedNewValue.toDoubleOrNull()
    return if (convertedNumber != null && convertedNumber in 0.0..maxInt.toDouble()) {
        fixedNewValue
    } else {
        oldValue
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
fun Float.roundToDecimals(scale: Int = 1) = try {
    this.toBigDecimal()
        .setScale(scale, RoundingMode.HALF_EVEN).toFloat()
} catch (e: Exception) {
    this
}
