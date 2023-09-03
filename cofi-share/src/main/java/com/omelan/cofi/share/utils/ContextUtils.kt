package com.omelan.cofi.share.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.omelan.cofi.share.timer.notification.TIMER_CHANNEL_ID


fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun Context.askForPermission(permissions: String) {
    if (ActivityCompat.checkSelfPermission(
            this,
            permissions,
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this.getActivity() as Activity,
            arrayOf(permissions),
            1,
        )
    }
}

fun Context.askForNotificationPermission() =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> askForPermission(Manifest.permission.POST_NOTIFICATIONS)

        !hasNotificationPermission() -> {
            val intent = Intent()
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            ContextCompat.startActivity(this, intent, null)
        }

        else -> {}
    }

fun Context.hasNotificationPermission(): Boolean {
    val notificationManagerCompat = NotificationManagerCompat.from(this)
    val allNotificationSwitch = notificationManagerCompat.areNotificationsEnabled()
    val channelNotificationSwitch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManagerCompat.getNotificationChannel(TIMER_CHANNEL_ID)?.importance !=
                NotificationManager.IMPORTANCE_NONE
    } else {
        true
    }
    return allNotificationSwitch && channelNotificationSwitch
}

