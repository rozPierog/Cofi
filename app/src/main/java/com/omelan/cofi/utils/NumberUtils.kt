package com.omelan.cofi.utils

import android.content.res.Resources
import androidx.compose.ui.unit.dp
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

fun Int.toMillis() = this * 1000

@ExperimentalTime
fun Int.toStringDuration(): String {
    val duration = this.toDuration(DurationUnit.MILLISECONDS)
    return "${duration.inMinutes.toInt()}:${duration.inSeconds.toInt().toString().padStart(2, '0')}"
}

fun Int.pixelsToDp(resources: Resources?) = (this / (resources?.displayMetrics?.density ?: 1f)).dp