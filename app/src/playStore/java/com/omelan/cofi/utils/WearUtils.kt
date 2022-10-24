package com.omelan.cofi.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.Step
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.SharedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
            val nodes = Wearable.getNodeClient(activity).connectedNodes.await()
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
}
