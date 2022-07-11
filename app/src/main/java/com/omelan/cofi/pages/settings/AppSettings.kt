@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.utils.getDefaultPadding

@Composable
fun AppSettings(
    goBack: () -> Unit,
    goToAbout: () -> Unit,
    goToTimerSettings: () -> Unit,
    goToBackupRestore: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val snackbarState = SnackbarHostState()
    val appBarBehavior = createAppBarBehavior()
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier.padding(getDefaultPadding())
            ) {
                Snackbar(shape = RoundedCornerShape(50)) {
                    Text(text = it.visuals.message)
                }
            }
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
            )
        ) {
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_timer_item)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_timer),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(onClick = goToTimerSettings)
                )
            }
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_backup_item)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(onClick = goToBackupRestore)
                )
            }
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_bug_item)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_bug_report),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri("https://github.com/rozPierog/Cofi/issues")
                        }
                    ),
                )
            }
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_about_item)) },
                    icon = { Icon(Icons.Rounded.Info, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(onClick = goToAbout)
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = { }, goToAbout = { }, goToTimerSettings = {}, goToBackupRestore = {})
}