package com.omelan.cofi.pages.list

import android.app.Activity
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.omelan.cofi.BuildConfig
import com.omelan.cofi.R
import com.omelan.cofi.components.RecipeListInfoBox
import com.omelan.cofi.model.DataStore
import com.omelan.cofi.utils.WearUtils
import kotlinx.coroutines.launch

@Composable
fun createRecipeListHeaderInfo(animateToTop: suspend () -> Unit): (LazyGridScope.() -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dataStore = DataStore(context)
    val dismissedBoxes by dataStore.getDismissedInfoBoxes().collectAsState(initial = null)
    val lastSeenUpdateBoxVersion by dataStore.getLastSeenUpdateNoticeVersion().collectAsState(
        initial = Int.MAX_VALUE,
    )
    var wearNodesWithoutApp by remember {
        mutableStateOf(listOf<String>())
    }
    WearUtils.ObserveIfWearAppInstalled {
        wearNodesWithoutApp = it
        if (it.isNotEmpty()) {
            coroutineScope.launch {
                animateToTop()
            }
        }
    }

    val showWearOSNotice by remember {
        derivedStateOf {
            wearNodesWithoutApp.isNotEmpty() && dismissedBoxes != null &&
                dismissedBoxes?.containsKey("wearOS") == false
        }
    }
    val showUpdateNotice by remember {
        derivedStateOf {
            BuildConfig.VERSION_CODE > lastSeenUpdateBoxVersion
        }
    }

    return when {
        showUpdateNotice -> {
            {
                item {
                    NewUpdateNotice(
                        onDismiss = {
                            coroutineScope.launch {
                                dataStore.setLastSeenUpdateNoticeVersion(BuildConfig.VERSION_CODE)
                            }
                        },
                    )
                }
            }
        }

        showWearOSNotice -> {
            {
                item {
                    WatchOsNotice(
                        wearNodesWithoutApp = wearNodesWithoutApp,
                        onDismiss = {
                            val newMap = dismissedBoxes?.toMutableMap() ?: mutableMapOf()
                            newMap["wearOS"] = true
                            coroutineScope.launch {
                                dataStore.setDismissedInfoBoxes(newMap)
                            }
                        },
                    )
                }
            }
        }

        else -> {
            {}
        }
    }
}

@Composable
fun LazyGridItemScope.WatchOsNotice(wearNodesWithoutApp: List<String>, onDismiss: () -> Unit) {
    val activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    RecipeListInfoBox(
        title = {
            Text(
                text = stringResource(id = R.string.infoBox_wearOS_title),
                fontWeight = FontWeight.Bold,
            )
        },
        icon = {
            Icon(
                painterResource(id = R.drawable.ic_watch),
                "",
                modifier = Modifier.size(28.dp),
            )
        },
        text = { Text(text = stringResource(id = R.string.infoBox_wearOS_body)) },
        onClick = {
            WearUtils.openPlayStoreOnWearDevicesWithoutApp(
                lifecycleOwner,
                activity,
                wearNodesWithoutApp,
            )
        },
        onDismiss = onDismiss,
    )
}

@Composable
fun LazyGridItemScope.NewUpdateNotice(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    RecipeListInfoBox(
        title = {
            Text(
                text = stringResource(id = R.string.infoBox_update_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = { Text(stringResource(id = R.string.infoBox_update_body)) },
        onClick = {
            uriHandler.openUri(
                "https://github.com/rozPierog/Cofi/blob/main/docs/Changelog.md",
            )
            onDismiss()
        },
        onDismiss = onDismiss,
    )
}
