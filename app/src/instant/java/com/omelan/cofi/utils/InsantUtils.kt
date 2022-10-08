package com.omelan.cofi.utils

import android.app.Activity
import android.content.Intent
import com.google.android.gms.instantapps.InstantApps
import com.omelan.cofi.BuildConfig

fun showInstallPrompt(activity: Activity) {
    val postInstall = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_DEFAULT)
        .setPackage(BuildConfig.APPLICATION_ID)

    InstantApps.showInstallPrompt(activity, postInstall, 1000, null)
}

fun isInstantApp() : Boolean {
    return true
}
