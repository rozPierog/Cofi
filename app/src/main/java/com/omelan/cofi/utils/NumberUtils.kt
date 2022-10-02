package com.omelan.cofi.utils

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
        ":$millisString"
    } else {
        ""
    }
}

fun ensureNumbersOnly(string: String): String? {
    if (string.isEmpty()) {
        return string
    }
    val maxInt: Int = Int.MAX_VALUE / 1000
    return try {
        val stringValue = string.trim().toInt()
        if (stringValue > maxInt || stringValue < 0) {
            null
        } else {
            stringValue.toString()
        }
    } catch (e: NumberFormatException) {
        null
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
