package com.omelan.cofi.pages.settings.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.pages.settings.settingsItemModifier
import com.omelan.cofi.share.model.Dependency
import com.omelan.cofi.share.model.License
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.linkSpanStyle

@Composable
fun DependencyItem(dependency: Dependency) {
    val uriHandler = LocalUriHandler.current
    val year = if (dependency.year.isNullOrBlank() || dependency.year == "null") {
        "20XX"
    } else {
        dependency.year
    }
    val licenses = buildAnnotatedString {
        dependency.licenses.forEachIndexed { index, license ->
            withLink(
                LinkAnnotation.Url(
                    license.license_url,
                    styles = linkSpanStyle(MaterialTheme.colorScheme.secondary),
                ),
            ) {
                append(license.license)
            }
            if (index != dependency.licenses.size - 1) {
                append(", ")
            }
        }
    }
    Column(
        modifier = Modifier
            .settingsItemModifier(
                onClick = {
                    uriHandler.openUri(
                        dependency.url ?: dependency.licenses.first().license_url,
                    )
                },
                enabled = !dependency.url.isNullOrBlank() && dependency.url != "null",
                unlimitedHeight = true,
            )
            .padding(vertical = Spacing.normal, horizontal = Spacing.big)
            .fillMaxWidth()
            .testTag("dependency_column"),
    ) {
        Text(
            text = "${dependency.project} @${dependency.version}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = dependency.description,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = Spacing.normal),
        )
        Text(
            text = "Copyright © $year ${dependency.developers.joinToString()}",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = licenses,
            modifier = Modifier.padding(bottom = Spacing.normal),
        )
        HorizontalDivider()
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
        ),
    )
}
