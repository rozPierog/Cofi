package com.omelan.cofi.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

object IntentHelpers {
    fun openUri(context: Context, uri: Uri) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = uri
        ContextCompat.startActivity(context, openURL, null)
    }

    fun openUri(context: Context, uri: String) {
        openUri(context, Uri.parse(uri))
    }
}