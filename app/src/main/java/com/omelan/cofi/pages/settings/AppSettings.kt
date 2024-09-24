@file:OptIn(ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages.settings

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.utils.WearUtils
import com.omelan.cofi.utils.getDefaultPadding

@Composable
fun AppSettings(
    goBack: () -> Unit,
    goToAbout: () -> Unit,
    goToTimerSettings: () -> Unit,
    goToBackupRestore: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val appBarBehavior = createAppBarBehavior()
    val activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    var wearNodesWithoutApp by remember {
        mutableStateOf(listOf<String>())
    }
    WearUtils.ObserveIfWearAppInstalled {
        wearNodesWithoutApp = it
    }
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(appBarBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentPadding = getDefaultPadding(
                paddingValues = it,
                additionalStartPadding = 0.dp,
                additionalEndPadding = 0.dp,
            ),
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_timer_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_timer),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(onClick = goToTimerSettings),
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_backup_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(onClick = goToBackupRestore),
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_about_item))
                    },
                    leadingContent = { Icon(Icons.Rounded.Info, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(onClick = goToAbout),
                )
            }
            if (wearNodesWithoutApp.isNotEmpty()) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(id = R.string.settings_wearOS_item))
                        },
                        leadingContent = {
                            Icon(
                                painterResource(id = R.drawable.ic_watch),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.settingsItemModifier(
                            onClick = {
                                WearUtils.openPlayStoreOnWearDevicesWithoutApp(
                                    lifecycleOwner,
                                    activity,
                                    wearNodesWithoutApp,
                                )
                            },
                        ),
                    )
                }
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_bug_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_bug_report),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri("https://github.com/rozPierog/Cofi/issues")
                        },
                    ),
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = { }, goToAbout = { }, goToTimerSettings = {}, goToBackupRestore = {})
}
