package com.omelan.cofi.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.utils.IntentHelpers

@Composable
fun AppSettingsAbout(goBack: () -> Unit, openLicenses: () -> Unit) {
    val context = AmbientContext.current

    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_about_title),
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
                        Text(text = stringResource(id = R.string.settings_github_item))
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_github_icon))
                    },
                    modifier = settingsItemModifier.clickable(
                        onClick = {
                            IntentHelpers.openUri(context, "https://github.com/rozPierog/Cofi/")
                        }
                    ),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.settings_licenses_item))
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_book))
                    },
                    modifier = settingsItemModifier.clickable(onClick = openLicenses)
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = "Default recipes by James Hoffmann" )
                    },
                    secondaryText = {
                        Text(text = "https://www.youtube.com/channel/UCMb0O2CdPBNi-QqPk5T3gsQ")
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_coffee))
                    },
                    modifier = settingsItemModifier.clickable(onClick = {
                        IntentHelpers.openUri(context, "https://www.youtube.com/channel/UCMb0O2CdPBNi-QqPk5T3gsQ")
                    })
                )
            }
        }
    }
}

@Preview
@Composable
fun AboutAppSettingsPreview() {
    AppSettingsAbout(goBack = {}, openLicenses = {})
}