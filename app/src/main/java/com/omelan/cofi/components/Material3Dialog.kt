package com.omelan.cofi.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.omelan.cofi.R
import com.omelan.cofi.share.CombineWeight
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.Spacing

@Composable
fun Material3Dialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    onSave: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = onDismissRequest,
    title: (@Composable BoxScope.() -> Unit)? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
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
                icon?.let {
                    CompositionLocalProvider(
                        LocalContentColor provides AlertDialogDefaults.iconContentColor,
                    ) {
                        Box(
                            Modifier
                                .padding(PaddingValues(bottom = Spacing.small))
                                .align(Alignment.CenterHorizontally),
                        ) {
                            icon()
                        }
                    }
                }
                title?.let {
                    CompositionLocalProvider(
                        LocalContentColor provides AlertDialogDefaults.titleContentColor,
                    ) {
                        val textStyle = MaterialTheme.typography.headlineSmall
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .padding(horizontal = Spacing.big, vertical = Spacing.normal)
                                    .align(Alignment.Start),
                            ) {
                                title()
                            }
                        }
                    }
                }
                content()
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = Spacing.big),
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

@Composable
@Preview
fun DialogPreview() {
    CofiTheme {
        Material3Dialog(
            onDismissRequest = { },
            onSave = {},
            title = {
                Text(text = stringResource(id = R.string.settings_combine_weight_item))
            },
        ) {
            CombineWeight.entries.forEach {
                Text(
                    it.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp),
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewSupportCofi2() {
    CofiTheme {
        SupportCofi {
        }
    }
}
