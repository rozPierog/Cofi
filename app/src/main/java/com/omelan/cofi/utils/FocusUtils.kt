@file:Suppress("UnusedReceiverParameter")

package com.omelan.cofi.utils

import androidx.compose.ui.focus.FocusRequester

suspend fun FocusRequester.requestFocusSafer() {
    // TODO: FIX AUTOFOCUS
//    try {
//        this.requestFocus()
//    } catch (e: Exception) {
//        Log.e("FAILED REQUEST", e.message ?: this.toString())
//        delay(100)
//        this.requestFocus()
//    }
}
