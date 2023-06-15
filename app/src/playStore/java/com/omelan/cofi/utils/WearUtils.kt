package com.omelan.cofi.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.COMBINE_WEIGHT
import com.omelan.cofi.share.DataStore
import com.omelan.cofi.share.STEP_SOUND_ENABLED
import com.omelan.cofi.share.STEP_VIBRATION_ENABLED
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.SharedData
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.utils.getActivity
import com.omelan.cofi.share.utils.verify_cofi_wear_app
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

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

    private fun ChannelClient.sendSettingsToWearOS(
        data: Map<String, Any>,
        activity: AppCompatActivity,
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val nodes = try {
                Wearable.getNodeClient(activity).connectedNodes.await()
            } catch (e: Exception) {
                return@launch
            }
            nodes.forEach { node ->
                val channel = openChannel(node.id, "cofi/settings").await()
                val outputStreamTask = Wearable.getChannelClient(activity).getOutputStream(channel)
                outputStreamTask.addOnSuccessListener { outputStream ->
                    outputStream.use {
                        val jsonArray = JSONArray()
                        data.forEach { sharedData ->
                            jsonArray.put(
                                JSONObject().apply {
                                    put(sharedData.key, sharedData.value)
                                },
                            )
                        }
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
        val dataStore = DataStore(activity)
        recipesLiveData = db.recipeDao().getAll()
        stepsLiveData = db.stepDao().getAll()
        CoroutineScope(Dispatchers.IO + Job()).launch {
            combine(
                dataStore.getStepChangeSoundSetting(),
                dataStore.getStepChangeVibrationSetting(),
                dataStore.getWeightSetting(),
            ) { isSoundEnabled: Boolean, isVibrationEnabled: Boolean, weightSetting: String ->
                Triple(isSoundEnabled, isVibrationEnabled, weightSetting)
            }.collect { (isSoundEnabled, isVibrationEnabled, weightSetting) ->
                channelClient.sendSettingsToWearOS(
                    mapOf(
                        STEP_SOUND_ENABLED.name to isSoundEnabled,
                        STEP_VIBRATION_ENABLED.name to isVibrationEnabled,
                        COMBINE_WEIGHT.name to weightSetting,
                    ),
                    activity,
                )
            }
        }
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
    fun ObserveIfWearAppInstalled(onChange: (nodesWithoutApp: List<String>) -> Unit) {
        val mainActivity = LocalContext.current.getActivity() ?: return
        val nodeClient = Wearable.getNodeClient(mainActivity)
        val coroutineScope = rememberCoroutineScope()
        DisposableEffect(LocalLifecycleOwner.current) {
            val capabilityClient = Wearable.getCapabilityClient(mainActivity)
            val listener = CapabilityClient.OnCapabilityChangedListener { capabilityInfo ->
                coroutineScope.launch {
                    val connectedNodes = try {
                        nodeClient.connectedNodes.await()
                    } catch (e: Exception) {
                        return@launch
                    }
                    onChange((connectedNodes - capabilityInfo.nodes).map { it.id })
                }
            }
            capabilityClient.addListener(listener, verify_cofi_wear_app)
            onDispose {
                capabilityClient.removeListener(listener, verify_cofi_wear_app)
            }
        }
        LaunchedEffect(Unit) {
            val connectedNodes = try {
                nodeClient.connectedNodes.await()
            } catch (e: Exception) {
                return@LaunchedEffect
            }
            val capabilityClient = Wearable.getCapabilityClient(mainActivity)
            val capabilityInfo =
                capabilityClient.getCapability(verify_cofi_wear_app, CapabilityClient.FILTER_ALL)
                    .await()
            onChange((connectedNodes - capabilityInfo.nodes).map { it.id })
        }
    }

    private const val COFI_PLAY_STORE_LINK =
        "https://play.google.com/store/apps/details?id=com.omelan.cofi"

    fun openPlayStoreOnWearDevicesWithoutApp(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        nodesIdWithoutApp: List<String>,
    ) {
        val intent = Intent(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(COFI_PLAY_STORE_LINK))
        val remoteActivityHelper = RemoteActivityHelper(activity)
        nodesIdWithoutApp.forEach { id ->
            lifecycleOwner.lifecycleScope.launch {
                try {
                    remoteActivityHelper.startRemoteActivity(
                        targetIntent = intent,
                        targetNodeId = id,
                    ).await()
                } catch (cancellationException: CancellationException) {
                    // Request was cancelled normally
                }
            }
        }
    }
}
