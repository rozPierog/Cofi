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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.omelan.cofi.BuildConfig
import com.omelan.cofi.share.R

@Composable
fun SupportCofi(onDismissRequest: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Material3Dialog(
        onDismissRequest = onDismissRequest,
        onCancel = null,
        onSave = onDismissRequest,
    ) {
        Column(Modifier.padding(24.dp)) {
            CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                Box(
                    Modifier
                        .padding(PaddingValues(bottom = 16.dp))
                        .align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_monochrome),
                        contentDescription = stringResource(id = R.string.app_name),
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
                        Text(text = stringResource(id = R.string.support_dialog_title))
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
                        Text(text = stringResource(id = R.string.support_dialog_body))
                    }
                }
            }
        }
        ListItem(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/rozPierog/Cofi")
            },
            leadingContent = { Text("⭐") },
            headlineContent = { Text(text = stringResource(id = R.string.support_dialog_github)) },
        )
        ListItem(
            modifier = Modifier.clickable {
                uriHandler.openUri("https://fosstodon.org/@LeonOmelan")
            },
            leadingContent = { Text("🐘") },
            headlineContent = { Text(text = stringResource(id = R.string.support_dialog_mastodon)) },
        )
        when (BuildConfig.FLAVOR) {
            "full" -> ListItem(
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://ko-fi.com/leonomelan")
                },
                leadingContent = { Text("☕️") },
                headlineContent = { Text(text = stringResource(id = R.string.support_dialog_kofi)) },
            )

            "instant", "playStore" -> ListItem(
                modifier = Modifier.clickable {
                    uriHandler.openUri(
                        "https://play.google.com/store/apps/details?id=com.omelan.cofi",
                    )
                },
                leadingContent = { Text("✍️") },
                headlineContent = { Text(text = stringResource(id = R.string.support_dialog_review)) },
            )

            else -> {}
        }
    }
}
