package com.omelan.cofi.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.instantapps.InstantApps

fun showInstallPrompt(activity: Activity, id: String) {
    val postInstall = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_DEFAULT)
        .setPackage(id)

    InstantApps.showInstallPrompt(activity, postInstall, 1000, null)
}

fun isInstantApp(context: Context) = context.packageManager.isInstantApp
