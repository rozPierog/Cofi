package com.omelan.cofi.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

@Composable
fun RecipeListInfoBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.StartToEnd) {
            onDismiss()
        }
        true
    })

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = {},
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
    ) {
        RecipeListItemBackground(
            contentPadding = PaddingValues(start = Spacing.big, bottom = Spacing.big),
            onClick = onClick,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    title()
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
            ) {
                icon?.let { it() }
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    text()
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeListInfoBoxPreview() {
    RecipeListInfoBox(
        title = {
            Text(
                text = stringResource(id = com.omelan.cofi.R.string.infoBox_wearOS_title),
                fontWeight = FontWeight.Bold,
            )
        },
        icon = {
            Icon(
                painterResource(id = com.omelan.cofi.R.drawable.ic_watch),
                "",
                modifier = Modifier.size(28.dp),
            )
        },
        text = { Text(text = stringResource(id = com.omelan.cofi.R.string.infoBox_wearOS_body)) },
        onClick = {
        },
        onDismiss = { },
    )
}
