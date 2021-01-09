package com.omelan.burr.pages.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.omelan.burr.components.PiPAwareAppBar

@Composable
fun AppSettingsAbout(goBack: () -> Unit, openLicenses: () -> Unit) {
    val context = AmbientContext.current

    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = { Text(text = "About", maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
                        Text(text = "Github Repository")
                    },
                    icon = {
                        Icon(Icons.Rounded.Share)
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
                        Text(text = "Acknowledgments")
                    },
                    icon = {
                        Icon(Icons.Rounded.Build)
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