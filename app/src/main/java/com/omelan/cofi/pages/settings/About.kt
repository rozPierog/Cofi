package com.omelan.cofi.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.BuildConfig
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.SupportCofi
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.utils.getDefaultPadding

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppSettingsAbout(goBack: () -> Unit, openLicenses: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val appBarBehavior = createAppBarBehavior()
    var showSupportCofi by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_about_title),
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
                    text = {
                        Text(text = stringResource(id = R.string.settings_github_item))
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_github_icon),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri("https://github.com/rozPierog/Cofi/")
                        },
                    ),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.settings_licenses_item))
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_gavel), contentDescription = null)
                    },
                    modifier = Modifier.settingsItemModifier(onClick = openLicenses),
                )
            }
            item {
                ListItem(
                    overlineText = {
                        Text(
                            text = stringResource(id = R.string.hoffmann_credits_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.hoffmann_credits_subtitle),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Light,
                        )
                    },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_coffee), contentDescription = null)
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri(
                                "https://www.youtube.com/channel/UCMb0O2CdPBNi-QqPk5T3gsQ",
                            )
                        },
                    ),
                )
            }
            item {
                ListItem(
                    overlineText = {
                        Text(
                            text = stringResource(id = R.string.tereszkiewicz_credits_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.tereszkiewicz_credits_subtitle),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Light,
                        )
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.recipe_icon_coffee_grinder),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri(
                                "https://dribbble.com/hubert-tereszkiewicz",
                            )
                        },
                    ),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.support_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Face,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showSupportCofi = true },
                    ),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.settings_translate_item),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_translate),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri("https://hosted.weblate.org/engage/cofi/")
                        },
                    ),
                )
            }
            item {
                ListItem(
                    overlineText = {
                        Text(
                            text = stringResource(R.string.app_version),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    text = {
                        Text(
                            text = BuildConfig.VERSION_NAME,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Light,
                        )
                    },
                    icon = { Icon(Icons.Rounded.Build, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri(
                                "https://github.com/rozPierog/Cofi/blob/main/docs/Changelog.md",
                            )
                        },
                    ),
                )
            }
        }
        if (showSupportCofi) {
            SupportCofi(onDismissRequest = { showSupportCofi = false })
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Preview
@Composable
fun AboutAppSettingsPreview() {
    AppSettingsAbout(goBack = {}, openLicenses = {})
}
