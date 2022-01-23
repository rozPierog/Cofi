package com.omelan.cofi.pages.settings.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.omelan.cofi.pages.settings.settingsItemModifier
import org.json.JSONArray
import org.json.JSONObject


class License(val license: String, val license_url: String)

class Dependency(
    val project: String,
    val description: String,
    val version: String,
    val developers: List<String>,
    val url: String,
    val year: String?,
    val licenses: List<License>,
    val dependency: String,
)

fun JSONArray.toStringList(): List<String>? {
    val list = mutableListOf<String>()
    (0 until this.length()).forEach {
        list.add(this[it] as String)
    }
    return if (list.isEmpty()) null else list
}

fun JSONArray.toLicensesList(): List<License> {
    val list = mutableListOf<License>()
    (0 until this.length()).forEach {
        val jsonObject = this[it] as JSONObject
        list.add(
            License(
                license = jsonObject.getString("license"),
                license_url = jsonObject.getString("license_url")
            )
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
                developers = jsonObject.getJSONArray("developers").toStringList()
                    ?: listOf("Original author or authors"),
                url = jsonObject.getString("url"),
                year = jsonObject.getString("year"),
                licenses = jsonObject.getJSONArray("licenses").toLicensesList(),
                dependency = jsonObject.getString("dependency"),
            )
        )
    }
    return dependencyList
}

@Composable
fun Dependency(dependency: Dependency) {
    val uriHandler = LocalUriHandler.current
    val year = if (dependency.year == null || dependency.year == "null") {
        "20XX"
    } else {
        dependency.year
    }
    val licenses =
        buildAnnotatedString {
            append(dependency.licenses.joinToString { license -> license.license })
            var lastPosition = 0
            dependency.licenses.forEach {
                val positionOfUrl = it.license.indexOf(it.license, startIndex = lastPosition)
                lastPosition = positionOfUrl
                addStringAnnotation(
                    tag = "URL",
                    annotation = it.license_url,
                    start = positionOfUrl,
                    end = positionOfUrl + it.license.length,
                )
                addStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline
                    ),
                    positionOfUrl,
                    positionOfUrl + it.license.length
                )
            }
        }
    Column(
        modifier = Modifier
            .settingsItemModifier(
                onClick = { uriHandler.openUri(dependency.url) },
                unlimitedHeight = true
            )
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = dependency.dependency, color = MaterialTheme.colorScheme.onBackground)
        Text(
            text = "Copyright © $year ${dependency.developers.joinToString()}",
            color = MaterialTheme.colorScheme.onBackground
        )
        ClickableText(
            text = licenses,
            onClick = {
                licenses
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                        return@ClickableText
                    }
            }
        )
    }
}