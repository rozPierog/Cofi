package com.omelan.cofi.utils

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.Step
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.SharedData
import com.omelan.cofi.share.utils.getActivity
import com.omelan.cofi.share.utils.verify_cofi_wear_app
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

object WearUtils {

    private lateinit var recipesLiveData: LiveData<List<Recipe>>
    private lateinit var stepsLiveData: LiveData<List<Step>>

    private fun ChannelClient.sendDataToWearOS(
        data: List<SharedData>,
        channelName: String,
        activity: AppCompatActivity,
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val nodes = try {
                Wearable.getNodeClient(activity).connectedNodes.await()
            } catch (e: Exception) {
                return@launch
            }
            nodes.forEach { node ->
                val channel = openChannel(node.id, "cofi/$channelName").await()
                val outputStreamTask = Wearable.getChannelClient(activity).getOutputStream(channel)
                outputStreamTask.addOnSuccessListener { outputStream ->
                    outputStream.use {
                        val jsonArray = JSONArray()
                        data.forEach { sharedData -> jsonArray.put(sharedData.serialize()) }
                        it.write(jsonArray.toString().toByteArray())
                        it.flush()
                    }
                }
            }
        }
    }

    fun observeChangesAndSendToWear(activity: AppCompatActivity) {
        val channelClient = Wearable.getChannelClient(activity)
        val db = AppDatabase.getInstance(activity)
        recipesLiveData = db.recipeDao().getAll()
        stepsLiveData = db.stepDao().getAll()

        recipesLiveData.observe(activity) { recipes ->
            channelClient.sendDataToWearOS(recipes, "recipes", activity)
        }
        stepsLiveData.observe(activity) { steps ->
            channelClient.sendDataToWearOS(steps, "steps", activity)
        }
    }

    fun removeObservers(activity: AppCompatActivity) {
        if (::recipesLiveData.isInitialized) {
            recipesLiveData.removeObservers(activity)
        }
        if (::stepsLiveData.isInitialized) {
            stepsLiveData.removeObservers(activity)
        }
    }

    @Composable
    fun ObserveIfWearAppInstalled(onChange: (nodesWithoutApp: List<Node>) -> Unit) {
        val mainActivity = LocalContext.current.getActivity() ?: return
        val nodeClient = Wearable.getNodeClient(mainActivity)
        val coroutineScope = rememberCoroutineScope()
        DisposableEffect(LocalLifecycleOwner.current) {
            val capabilityClient = Wearable.getCapabilityClient(mainActivity)
            coroutineScope.launch {
                val connectedNodes = try {
                    nodeClient.connectedNodes.await()
                } catch (e: Exception) {
                    return@launch
                }
                val listener = CapabilityClient.OnCapabilityChangedListener {
                    onChange(connectedNodes - it.nodes)
                }

                capabilityClient.addListener(listener, verify_cofi_wear_app)
                onDispose {
                    capabilityClient.removeListener(listener, verify_cofi_wear_app)
                }
            }
            onDispose { }
        }
        LaunchedEffect(Unit) {
            val connectedNodes = try {
                nodeClient.connectedNodes.await()
            } catch (e: Exception) {
                return@LaunchedEffect
            }
            val capabilityClient = Wearable.getCapabilityClient(mainActivity)
            val capabilityInfo = capabilityClient
                .getCapability(verify_cofi_wear_app, CapabilityClient.FILTER_ALL)
                .await()
            onChange(connectedNodes - capabilityInfo.nodes)
        }
    }

    private const val COFI_PLAY_STORE_LINK =
        "https://play.google.com/store/apps/details?id=com.omelan.cofi"

    fun openPlayStoreOnWearDevicesWithoutApp(
        activity: AppCompatActivity,
        nodesWithoutApp: List<Node>,
    ) {
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(COFI_PLAY_STORE_LINK))
        val remoteActivityHelper = RemoteActivityHelper(activity)
        nodesWithoutApp.forEach { node ->
            activity.lifecycleScope.launch {
                try {
                    remoteActivityHelper.startRemoteActivity(
                        targetIntent = intent,
                        targetNodeId = node.id,
                    ).await()
                } catch (cancellationException: CancellationException) {
                    // Request was cancelled normally
                }
            }
        }
    }
}
