@file:OptIn(ExperimentalHorologistApi::class)

package com.omelan.cofi.wearos.presentation.pages.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.omelan.cofi.share.model.Dependency
import com.omelan.cofi.share.model.License
import com.omelan.cofi.share.utils.parseJsonToDependencyList
import com.omelan.cofi.wearos.presentation.components.OpenOnPhoneConfirm
import com.omelan.cofi.wearos.presentation.utils.WearUtils.openLinkOnPhone

@Composable
fun LicensesList() {
    val context = LocalContext.current
    val dependencyList = context.assets.open("open_source_licenses.json").bufferedReader().use {
        it.readText()
    }.parseJsonToDependencyList()
    var showConfirmation by remember { mutableStateOf(false) }

    val scalingLazyListState = rememberScalingLazyListState(0, 0)
    val focusRequester = remember {
        FocusRequester()
    }

    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState)
        },
        timeText = {
            TimeText(Modifier.scrollAway(scalingLazyListState, 0))
        },
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .rotaryWithScroll(
                    focusRequester = focusRequester,
                    scrollableState = scalingLazyListState,
                ),
            state = scalingLazyListState,
            autoCentering = AutoCenteringParams(0, 0),
        ) {
            items(dependencyList) { dependency ->
                DependencyItem(dependency = dependency, afterOpen = { showConfirmation = true })
            }
        }
        OpenOnPhoneConfirm(isVisible = showConfirmation, onTimeout = { showConfirmation = false })
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DependencyItem(dependency: Dependency, afterOpen: () -> Unit = {}) {
    val activity = LocalContext.current as ComponentActivity

    Card(
        onClick = {
            dependency.url?.let {
                openLinkOnPhone(it, activity, afterOpen)
            }
        },
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = "${dependency.project} (${dependency.version})")
            Row {
                dependency.licenses.forEach {
                    Text(text = it.license, fontWeight = FontWeight.Light)
                }
            }
        }
    }

}


@Preview
@Composable
fun DependencyItemPreview() {
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
