package com.omelan.cofi.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.omelan.cofi.ui.Spacing

@Composable
fun Material3Dialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    onSave: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = onDismissRequest,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(28.0.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(vertical = Spacing.big)) {
                content()
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = Spacing.big)
                ) {
                    if (onCancel != null) {
                        TextButton(onClick = onCancel) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }
                    }
                    if (onSave != null) {
                        TextButton(onClick = onSave) {
                            Text(text = stringResource(id = android.R.string.ok))
                        }
                    }
                }
            }
        }
    }
}