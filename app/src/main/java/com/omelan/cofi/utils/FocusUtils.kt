package com.omelan.cofi.utils

import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay

suspend fun FocusRequester.requestFocusSafer() {
    try {
        this.requestFocus()
    } catch (e: Exception) {
        Log.e("FAILED REQUEST", e.message ?: this.toString())
        delay(100)
        this.requestFocus()
    }
}
