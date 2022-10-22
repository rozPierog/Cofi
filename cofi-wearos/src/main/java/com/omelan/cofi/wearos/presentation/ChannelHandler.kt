package com.omelan.cofi.wearos.presentation

import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.toRecipes
import com.omelan.cofi.share.toSteps
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import java.nio.charset.StandardCharsets

object ChannelHandler {
    fun listenToChanges(activity: MainActivity) {
        val channelClient = Wearable.getChannelClient(activity)
        val ioScope = CoroutineScope(Dispatchers.IO + Job())
        val db = AppDatabase.getInstance(activity)
        channelClient.registerChannelCallback(
            object : ChannelClient.ChannelCallback() {
                override fun onChannelOpened(channel: ChannelClient.Channel) {
                    super.onChannelOpened(channel)
                    ioScope.launch {
                        val inputStream = channelClient.getInputStream(channel).await()
                        val jsonString = String(inputStream.readBytes(), StandardCharsets.UTF_8)
                        when (channel.path) {
                            "steps" -> {
                                db.stepDao()
                                    .deleteAndCreate(JSONArray(jsonString).toSteps(true))
                            }

                            "recipes" -> {
                                db.recipeDao()
                                    .deleteAndCreate(JSONArray(jsonString).toRecipes(true))
                            }

                            else -> throw Exception("UNEXPECTED PATH ${channel.path}")
                        }
                        withContext(Dispatchers.IO) {
                            inputStream.close()
                        }
                        channelClient.close(channel)
                    }
                }
            },
        )
    }
}
