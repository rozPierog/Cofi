package com.omelan.cofi.pages.settings.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.pages.settings.settingsItemModifier
import com.omelan.cofi.utils.URL_ANNOTATION
import com.omelan.cofi.utils.addLink
import org.json.JSONArray
import org.json.JSONObject

data class License(val license: String, val license_url: String)

data class Dependency(
    val project: String,
    val description: String,
    val version: String,
    val developers: List<String>,
    val url: String?,
    val year: String?,
    val licenses: List<License>,
)

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
                developers = jsonObject.getJSONArray("developers").toAuthorList(),
                url = jsonObject.getString("url"),
                year = jsonObject.getString("year"),
                licenses = jsonObject.getJSONArray("licenses").toLicensesList(),
            )
        )
    }
    return dependencyList
}

@Composable
fun DependencyItem(dependency: Dependency) {
    val uriHandler = LocalUriHandler.current
    val year = if (dependency.year.isNullOrBlank() || dependency.year == "null") {
        "20XX"
    } else {
        dependency.year
    }
    val licenses =
        buildAnnotatedString {
            val licenses = dependency.licenses.joinToString { license -> license.license }
            append(licenses)
            var lastPosition = 0
            dependency.licenses.forEach {
                val positionOfUrl = licenses.indexOf(it.license, startIndex = lastPosition)
                lastPosition = positionOfUrl
                addLink(positionOfUrl = positionOfUrl, text = it.license, url = it.license_url)
            }
        }
    Column(
        modifier = Modifier
            .settingsItemModifier(
                onClick = {
                    uriHandler.openUri(
                        dependency.url ?: dependency.licenses.first().license_url
                    )
                },
                enabled = !dependency.url.isNullOrBlank() && dependency.url != "null",
                unlimitedHeight = true
            )
            .padding(8.dp)
            .fillMaxWidth()
            .testTag("dependency_column")
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
                    .getStringAnnotations(URL_ANNOTATION, it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                        return@ClickableText
                    }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(0.12f))
    }
}

@Preview
@Composable
fun DependencyPreview() {
    DependencyItem(
        dependency = Dependency(
            project = "Nice package",
            description = "Contains Guava\\u0027s " +
                "com.google.common.util.concurrent.ListenableFuture" +
                " class,\\n    without any of its other classes -- but is also available in " +
                "a second\\n    \\\"version\\\" that omits the class to avoid conflicts with " +
                "the copy in Guava\\n    itself. The idea is:\\n\\n    - If users want only " +
                "ListenableFuture, they depend on listenablefuture-1.0.\\n\\n    " +
                "- If users want all of Guava, they depend on guava, which, as of Guava\\n   " +
                " 27.0, depends on\\n   " +
                " listenablefuture-9999.0-empty-to-avoid-conflict-with-guava. " +
                "The 9999.0-...\\n    version number is enough for some build systems" +
                " (notably, Gradle) to select\\n    that empty artifact over the " +
                "\\\"real\\\" listenablefuture-1.0 -- avoiding a\\n    " +
                "conflict with the copy of ListenableFuture in guava itself. If users are\\n " +
                "   using an older version of Guava or a build system other than Gradle," +
                " they\\n    may see class conflicts. If so, they can solve them by manually" +
                " excluding\\n    the listenablefuture artifact or manually forcing their " +
                "build systems to\\n    use 9999.0-....\",\n",
            version = "3.2.1",
            developers = listOf("Leon Omelan"),
            url = "jsonObject.getString( url )",
            year = "null",
            licenses = listOf(License(license = "WTFPL", license_url = "http://www.wtfpl.net/")),
        )
    )
}