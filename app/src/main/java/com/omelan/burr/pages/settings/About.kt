package com.omelan.burr.pages.settings

import android.content.Intent
import android.net.Uri
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
import androidx.core.content.ContextCompat
import com.omelan.burr.R
import com.omelan.burr.components.PiPAwareAppBar

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
                            val browserIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/rozPierog/Burr/")
                                )
                            ContextCompat.startActivity(context, browserIntent, null)
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
        }
    }
}

@Preview
@Composable
fun AboutAppSettingsPreview() {
    AppSettingsAbout(goBack = {}, openLicenses = {})
}