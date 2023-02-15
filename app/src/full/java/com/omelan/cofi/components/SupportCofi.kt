package com.omelan.cofi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.iconContentColor
import androidx.compose.material3.AlertDialogDefaults.textContentColor
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.omelan.cofi.share.R

@Composable
fun SupportCofi(onDismissRequest: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Material3Dialog(onDismissRequest = onDismissRequest) {
        Column(Modifier.padding(24.dp)) {
            CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                Box(
                    Modifier
                        .padding(PaddingValues(bottom = 16.dp))
                        .align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_monochrome),
                        contentDescription = "",
                    )
                }
            }
            CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                val textStyle = MaterialTheme.typography.headlineSmall
                ProvideTextStyle(textStyle) {
                    Box(
                        Modifier
                            .padding(PaddingValues(bottom = 16.dp))
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Text(text = "Enjoying Cofi?")
                    }
                }
            }
            CompositionLocalProvider(LocalContentColor provides textContentColor) {
                val textStyle = MaterialTheme.typography.bodyMedium
                ProvideTextStyle(textStyle) {
                    Box(
                        Modifier
                            .weight(weight = 1f, fill = false)
                            .padding(PaddingValues(bottom = 16.dp))
                            .align(Alignment.Start),
                    ) {
                        Text(text = "I would greatly appreciate your support! You can help me continue doing what I love by choosing one or more of the following")
                    }
                }
            }
        }
        ListItem(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/rozPierog/Cofi/stargazers")
            },
            leadingContent = { Text("⭐") },
            headlineText = { Text(text = "Star Cofi on Github") },
        )
        ListItem(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://fosstodon.org/@LeonOmelan")
            },
            leadingContent = { Text("🐘") },
            headlineText = { Text(text = "Follow me on Mastodon") },
        )
        ListItem(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://ko-fi.com/leonomelan")
            },
            leadingContent = { Text("☕️") },
            headlineText = { Text(text = "Buy me a coffee") },
        )
    }
}
