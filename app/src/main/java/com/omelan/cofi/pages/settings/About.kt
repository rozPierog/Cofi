package com.omelan.cofi.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar

@ExperimentalMaterialApi
@Composable
fun AppSettingsAbout(goBack: () -> Unit, openLicenses: () -> Unit) {
    val uriHandler = LocalUriHandler.current

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
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
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
                        Icon(
                            painterResource(id = R.drawable.ic_github_icon),
                            contentDescription = null
                        )
                    },
                    modifier = settingsItemModifier.clickable(
                        onClick = {
                            uriHandler.openUri("https://github.com/rozPierog/Cofi/")
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
                        Icon(painterResource(id = R.drawable.ic_book), contentDescription = null)
                    },
                    modifier = settingsItemModifier.clickable(onClick = openLicenses)
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.hoffmann_credits_title))
                    },
                    secondaryText = {
                        Text(text = stringResource(id = R.string.hoffmann_credits_subtitle))
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_coffee), contentDescription = null)
                    },
                    modifier = settingsItemModifier.clickable(
                        onClick = {
                            uriHandler.openUri(
                                "https://www.youtube.com/channel/UCMb0O2CdPBNi-QqPk5T3gsQ"
                            )
                        }
                    )
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun AboutAppSettingsPreview() {
    AppSettingsAbout(goBack = {}, openLicenses = {})
}