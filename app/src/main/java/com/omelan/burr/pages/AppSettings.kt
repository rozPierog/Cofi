package com.omelan.burr.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.components.PiPAwareAppBar

@Composable
fun AppSettings(goBack: () -> Unit) {
    Scaffold(topBar = {
        PiPAwareAppBar(
            title = { Text(text = "Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(imageVector = Icons.Rounded.ArrowBack)
                }
            })
    }) {
        val commonModifier = Modifier.preferredHeight(56.dp)
        LazyColumn {
//            item {
//                ListItem(
//                    text = {
//                        Text(text = "Vibartion strength")
//                    },
//                    icon = {
//                        Icon(Icons.Rounded.Info)
//                    },
//                    modifier = commonModifier.clickable(onClick = { /*TODO*/ })
//                )
//            }
            item {
                ListItem(
                    text = {
                        Text(text = "Start on last used recipe")
                    },
                    icon = {
                        Icon(Icons.Rounded.Refresh)
                    },
                    trailing = {
                        Checkbox(checked = false, onCheckedChange = { /*TODO*/ })
                    },
                    modifier = commonModifier.clickable(onClick = { /*TODO*/ }),
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = "About App")
                    },
                    icon = {
                        Icon(Icons.Rounded.Info)
                    },
                    modifier = commonModifier.clickable(onClick = { /*TODO*/ })
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = {})
}


@Composable
fun AboutAppSettings() {
    LazyColumn(content = { /*TODO*/ })
}

@Preview
@Composable
fun AboutAppSettingsPreview() {
    AboutAppSettings()
}