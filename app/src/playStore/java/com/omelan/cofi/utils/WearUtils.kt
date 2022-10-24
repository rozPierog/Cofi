package com.omelan.cofi.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
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
import java.io.IOException
import java.io.OutputStream

object WearUtils {

    private lateinit var recipesLiveData: LiveData<List<Recipe>>
    private lateinit var stepsLiveData: LiveData<List<Step>>

    fun observeChangesAndSendToWear(activity: AppCompatActivity) {
        val channelClient = Wearable.getChannelClient(activity)
        val ioScope = CoroutineScope(Dispatchers.IO + Job())
        val db = AppDatabase.getInstance(activity)
        recipesLiveData = db.recipeDao().getAll()
        stepsLiveData = db.stepDao().getAll()
        fun sendDataToWearOS(data: List<SharedData>, channelName: String) {
            ioScope.launch {
                val nodes = Wearable.getNodeClient(activity).connectedNodes.await()
                nodes.forEach { node ->
                    val channel = channelClient.openChannel(node.id, "cofi/$channelName").await()
                    val outputStreamTask: Task<OutputStream> =
                        Wearable.getChannelClient(activity).getOutputStream(channel)
                    outputStreamTask.addOnSuccessListener { outputStream ->
                        try {
                            val jsonArray = JSONArray()
                            data.forEach { jsonArray.put(it.serialize()) }
                            outputStream.write(jsonArray.toString().toByteArray())
                            outputStream.flush()
                            outputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        recipesLiveData.observe(activity) { recipes ->
            sendDataToWearOS(recipes, "recipes")
        }
        stepsLiveData.observe(activity) { steps ->
            sendDataToWearOS(steps, "steps")
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
