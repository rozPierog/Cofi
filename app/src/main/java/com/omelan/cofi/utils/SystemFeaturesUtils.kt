package com.omelan.cofi.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.os.Build

fun checkPiPPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
            android.os.Process.myUid(),
            context.packageName,
        ) == AppOpsManager.MODE_ALLOWED
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
            android.os.Process.myUid(),
            context.packageName,
        ) == AppOpsManager.MODE_ALLOWED
        else -> false
    }
}

@SuppressLint("DiscouragedApi")
fun isUsingGestures(context: Context): Boolean {
    val resources = context.resources
    val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
    return if (resourceId > 0) resources.getInteger(resourceId) == 2 else false
}
