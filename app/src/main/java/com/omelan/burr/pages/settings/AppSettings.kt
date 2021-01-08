package com.omelan.burr.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.components.PiPAwareAppBar

@Composable
fun AppSettings(
    goBack: () -> Unit,
    goToAbout: () -> Unit,
) {
    Scaffold(topBar = {
        PiPAwareAppBar(
            title = { Text(text = "Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(imageVector = Icons.Rounded.ArrowBack)
                }
            })
    }) {
        LazyColumn {
            item {
                ListItem(
                    text = {
                        Text(text = "About App")
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