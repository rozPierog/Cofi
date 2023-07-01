package com.omelan.cofi.wearos.presentation

import android.util.Log
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.omelan.cofi.share.*
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.toRecipes
import com.omelan.cofi.share.model.toSteps
import com.omelan.cofi.wearos.presentation.model.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class CofiWearableListenerService : WearableListenerService() {
    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        val channelClient = Wearable.getChannelClient(this)
        saveDataFromChannel(channelClient, channel)
    }

    private fun saveDataFromChannel(
        channelClient: ChannelClient,
        channel: ChannelClient.Channel,
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            channelClient.getInputStream(channel).await().use {
                val jsonArray = try {
                    val jsonString = String(it.readBytes(), StandardCharsets.UTF_8)
                    JSONArray(jsonString)
                } catch (e: JSONException) {
                    if (BuildConfig.DEBUG) {
                        Log.e("saveDataFromChannel", "Data isn't JSON")
                    }
                    // Ignore the issue, it will retry again later anyway
                    return@use
                }
                when (channel.path) {
                    "cofi/recipes", "cofi/steps" -> saveDBDataFromChannel(jsonArray, channel.path)
                    "cofi/settings" -> {
                        if (
                            DataStore(this@CofiWearableListenerService)
                                .getSyncSettingsFromPhoneSetting().first()
                        ) {
                            saveSettingsFromChannel(jsonArray)
                        }
                    }

                    else -> throw Exception("UNEXPECTED PATH ${channel.path}")
                }
            }
            channelClient.close(channel)
        }
    }

    private suspend fun saveDBDataFromChannel(jsonArray: JSONArray, channelPath: String) {
        val db = AppDatabase.getInstance(this, false)
        when (channelPath) {
            "cofi/steps" -> db.stepDao().deleteAndCreate(jsonArray.toSteps(true))
            "cofi/recipes" -> db.recipeDao().deleteAndCreate(jsonArray.toRecipes(true))
        }
    }

    private suspend fun saveSettingsFromChannel(jsonArray: JSONArray) {
        val dataStore = DataStore(this)
        for (i in 0 until jsonArray.length()) {
            val jsonSetting = jsonArray.get(i) as JSONObject
            jsonSetting.keys().forEach { key ->
                when (key) {
                    COMBINE_WEIGHT.name -> dataStore.selectCombineMethod(
                        stringToCombineWeight(jsonSetting.get(key) as String),
                    )

                    STEP_VIBRATION_ENABLED.name ->
                        dataStore.setStepChangeVibration(jsonSetting.get(key) as Boolean)

                    STEP_SOUND_ENABLED.name ->
                        dataStore.setStepChangeSound(jsonSetting.get(key) as Boolean)
                }
            }
        }

    }
}
