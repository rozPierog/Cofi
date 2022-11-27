package com.omelan.cofi.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable

object WearUtils {
    fun observeChangesAndSendToWear(activity: AppCompatActivity) {}

    fun removeObservers(activity: AppCompatActivity) {}

    @Composable
    fun ObserveIfWearAppInstalled(onChange: (nodesIdWithoutApp: List<String>) -> Unit) {}
    fun openPlayStoreOnWearDevicesWithoutApp(
        activity: AppCompatActivity,
        nodesIdWithoutApp: List<String>,
    ) { }
}
