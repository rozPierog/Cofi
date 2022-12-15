package com.omelan.cofi.wearos.presentation.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.utils.getActivity
import com.omelan.cofi.share.utils.verify_cofi_phone_app
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object WearUtils {
    private const val COFI_PLAY_STORE_LINK =
        "https://play.google.com/store/apps/details?id=com.omelan.cofi"

    private suspend fun checkIfPhoneHasApp(activity: ComponentActivity): Boolean {
        val capabilityClient = Wearable.getCapabilityClient(activity)
        try {
            val nodesWithCofi = capabilityClient
                .getCapability(verify_cofi_phone_app, CapabilityClient.FILTER_ALL).await().nodes
            val allNodes = Wearable.getNodeClient(activity).connectedNodes.await()
            if (nodesWithCofi.isEmpty() && allNodes.isEmpty()) {
                // No phone connected at a time, cannot check if user has Cofi on a phone
                return true
            }
            return nodesWithCofi.isNotEmpty()
        } catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        }
        return false
    }

    fun openLinkOnPhone(
        link: String,
        activity: ComponentActivity,
        onSuccess: () -> Unit,
    ) {
        val remoteActivityHelper = RemoteActivityHelper(activity)
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(link))

        activity.lifecycleScope.launch {
            try {
                remoteActivityHelper.startRemoteActivity(intent).await()
                onSuccess()
            } catch (cancellationException: CancellationException) {
                // Request was cancelled normally
                throw cancellationException
            }
        }
    }

    fun openAppInStoreOnPhone(activity: ComponentActivity, onSuccess: () -> Unit) {
        openLinkOnPhone(COFI_PLAY_STORE_LINK, activity, onSuccess)
    }

    @Composable
    fun ObserveIfPhoneAppInstalled(onChange: (hasPhoneApp: Boolean) -> Unit) {
        val mainActivity = LocalContext.current.getActivity() ?: return
        DisposableEffect(LocalLifecycleOwner.current) {
            val listener = CapabilityClient.OnCapabilityChangedListener {
                onChange(it.nodes.isNotEmpty())
            }
            val capabilityClient = Wearable.getCapabilityClient(mainActivity)

            capabilityClient.addListener(listener, verify_cofi_phone_app)
            onDispose {
                capabilityClient.removeListener(listener, verify_cofi_phone_app)
            }
        }
        LaunchedEffect(Unit) {
            onChange(checkIfPhoneHasApp(mainActivity))
        }
    }
}
