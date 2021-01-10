package com.omelan.cofi.utils

fun Int.toMillis() = this * 1000

fun Int.toStringDuration(
    padMinutes: Boolean = false,
    padSeconds: Boolean = true,
    showMillis: Boolean = false,
    padMillis: Boolean = true
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
    } else ""

    return "${minutesString}:${secondsString}${
        if (millisString.isNotBlank()) {
            ":${millisString}"
        } else {
            ""
        }
    }"
}
