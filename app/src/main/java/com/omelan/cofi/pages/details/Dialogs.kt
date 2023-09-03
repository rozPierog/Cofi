package com.omelan.cofi.pages.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.R
import com.omelan.cofi.appDeepLinkUrl
import kotlinx.coroutines.launch

@Composable
fun rememberCopyAutomateLink(snackbarState: SnackbarHostState, recipeId: Int): () -> Unit {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarMessage = stringResource(id = R.string.snackbar_copied)

    return {
        coroutineScope.launch {
            clipboardManager.setText(AnnotatedString(text = "$appDeepLinkUrl/recipe/$recipeId"))
            snackbarState.showSnackbar(message = snackbarMessage)
        }
    }
}

@Composable
fun DirectLinkDialog(dismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.button_copy))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_link),
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_text))
        },
    )
}

@Composable
fun NotificationPermissionDialog(dismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.button_no))
            }
        },
        icon = {
            Icon(
                Icons.Rounded.Notifications,
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(id = R.string.recipe_detials_notification_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.recipe_detials_notification_dialog_text))
        },
    )
}

@Preview
@Composable
fun NotificationPermissionDialogPreview() {
    NotificationPermissionDialog(dismiss = { }, onConfirm = { })
}

@Preview
@Composable
fun DirectLinkDialogDialogPreview() {
    DirectLinkDialog(dismiss = { }, onConfirm = { })
}
