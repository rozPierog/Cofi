package com.omelan.cofi.pages.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.preferences.core.edit
import com.omelan.cofi.AmbientSettingsDataStore
import com.omelan.cofi.PIP_ENABLED
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun AppSettings(
    goBack: () -> Unit,
    goToAbout: () -> Unit,
) {
    val dataStore = AmbientSettingsDataStore.current
    suspend fun togglePiPSetting() {
        dataStore.edit { settings ->
            val currentPiPState = settings[PIP_ENABLED] ?: true
            settings[PIP_ENABLED] = !currentPiPState
        }
    }

    val isPiPEnabledFlow = dataStore.data.map { preferences ->
        preferences[PIP_ENABLED] ?: true
    }
    val isPiPEnabled = isPiPEnabledFlow.collectAsState(initial = true)
    val coroutineScope = rememberCoroutineScope()
    val context = AmbientContext.current
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
                        Icon(imageVector = Icons.Rounded.ArrowBack)
                    }
                }
            )
        }
    ) {
        LazyColumn {
            item {
                ListItem(
                    text = {
                        Text(text = "Picture in picture")
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_picture_in_picture))
                    },
                    modifier = settingsItemModifier.clickable(
                        onClick = {
                            coroutineScope.launch {
                                togglePiPSetting()
                            }
                        }
                    ),
                    trailing = {
                        Checkbox(
                            checked = isPiPEnabled.value,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    togglePiPSetting()
                                }
                            }
                        )
                    }
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = "Report a bug or suggest a feature")
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_bug_report))
                    },
                    modifier = settingsItemModifier.clickable(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("rozPierog@Gmail.com"))
                                putExtra(Intent.EXTRA_SUBJECT, "Cofi FR/Bug")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                startActivity(context, intent, null)
                            }
                        }
                    ),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.settings_about_item))
                    },
                    icon = {
                        Icon(Icons.Rounded.Info)
                    },
                    modifier = settingsItemModifier.clickable(onClick = goToAbout)
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = { }, goToAbout = { })
}