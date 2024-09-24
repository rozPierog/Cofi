@file:Suppress("EmptyMethod", "SameReturnValue", "UNUSED_PARAMETER")

package com.omelan.cofi.utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner

object WearUtils {
    fun observeChangesAndSendToWear(activity: AppCompatActivity) {}

    fun removeObservers(activity: AppCompatActivity) {}

    @Composable
    fun ObserveIfWearAppInstalled(onChange: (nodesIdWithoutApp: List<String>) -> Unit) {
    }

    fun openPlayStoreOnWearDevicesWithoutApp(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        nodesIdWithoutApp: List<String>,
    ) {
    }
}
