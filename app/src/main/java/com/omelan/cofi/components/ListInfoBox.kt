@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.omelan.cofi.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omelan.cofi.ui.Spacing

@Composable
fun LazyGridItemScope.RecipeListInfoBox(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
) {
    val dismissState = rememberDismissState()
    LaunchedEffect(key1 = dismissState.isDismissed(DismissDirection.StartToEnd)) {
        if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
            onDismiss()
        }
    }
    SwipeToDismiss(
        state = dismissState,
        background = {},
        directions = setOf(DismissDirection.StartToEnd),
    ) {
        RecipeListItemBackground(
            modifier = Modifier
                .animateItemPlacement(),
            contentPadding = PaddingValues(start = Spacing.big, bottom = Spacing.big),
            onClick = onClick,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                    title()
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "")
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
