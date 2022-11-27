package com.omelan.cofi.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.instantapps.InstantApps

object InstantUtils {
    fun showInstallPrompt(activity: Activity) {
        val postInstall = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage(activity.packageName)

        InstantApps.showInstallPrompt(activity, postInstall, 1000, null)
    }

    fun isInstantApp(context: Context) = context.packageManager.isInstantApp
}
