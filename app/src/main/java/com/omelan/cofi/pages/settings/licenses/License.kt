package com.omelan.cofi.pages.settings.licenses

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Shapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
fun DependencyItem(dependency: Dependency) {
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
        Text(
            text = "${dependency.project} @${dependency.version}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = dependency.description, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Copyright © $year ${dependency.developers.joinToString()}",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
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
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider()
    }
}

@Preview
@Composable
fun DependencyPreview() {
    DependencyItem(
        dependency = Dependency(
            project = "Nice package",
            description = "Contains Guava\\u0027s com.google.common.util.concurrent.ListenableFuture class,\\n    without any of its other classes -- but is also available in a second\\n    \\\"version\\\" that omits the class to avoid conflicts with the copy in Guava\\n    itself. The idea is:\\n\\n    - If users want only ListenableFuture, they depend on listenablefuture-1.0.\\n\\n    - If users want all of Guava, they depend on guava, which, as of Guava\\n    27.0, depends on\\n    listenablefuture-9999.0-empty-to-avoid-conflict-with-guava. The 9999.0-...\\n    version number is enough for some build systems (notably, Gradle) to select\\n    that empty artifact over the \\\"real\\\" listenablefuture-1.0 -- avoiding a\\n    conflict with the copy of ListenableFuture in guava itself. If users are\\n    using an older version of Guava or a build system other than Gradle, they\\n    may see class conflicts. If so, they can solve them by manually excluding\\n    the listenablefuture artifact or manually forcing their build systems to\\n    use 9999.0-....\",\n",
            version = "3.2.1",
            developers = listOf("Leon Omelan"),
            url = "jsonObject.getString( url )",
            year = "null",
            licenses = listOf(License(license = "WTFPL", license_url = "http://www.wtfpl.net/")),
            dependency = "com.omelan.cofi.super.library.of.awesomeness.lorem.impsum ",
        )
    )
}