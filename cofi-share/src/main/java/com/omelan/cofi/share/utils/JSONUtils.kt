package com.omelan.cofi.share.utils

import com.omelan.cofi.share.model.Dependency
import com.omelan.cofi.share.model.License
import org.json.JSONArray
import org.json.JSONObject

private fun JSONArray.toAuthorList(): List<String> {
    val list = mutableListOf<String>()
    (0 until this.length()).forEach {
        list.add(this.getString(it))
    }
    return if (list.isEmpty()) listOf("Original author or authors") else list
}

private fun JSONArray.toLicensesList(): List<License> {
    val list = mutableListOf<License>()
    (0 until this.length()).forEach {
        val jsonObject = this.getJSONObject(it)
        list.add(
            License(
                license = jsonObject.getString("license"),
                license_url = jsonObject.getString("license_url"),
            ),
        )
    }
    return list
}

fun String.parseJsonToDependencyList(): List<Dependency> {
    val jsonArray = JSONArray(this)
    val dependencyList = mutableListOf<Dependency>()
    (0 until jsonArray.length()).forEach {
        val jsonObject = jsonArray[it] as JSONObject
        dependencyList.add(
            Dependency(
                project = jsonObject.getString("project"),
                description = jsonObject.getString("description"),
                version = jsonObject.getString("version"),
                developers = jsonObject.getJSONArray("developers").toAuthorList(),
                url = jsonObject.getString("url"),
                year = jsonObject.getString("year"),
                licenses = jsonObject.getJSONArray("licenses").toLicensesList(),
            ),
        )
    }
    return dependencyList
}
