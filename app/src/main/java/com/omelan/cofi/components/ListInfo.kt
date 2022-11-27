@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.omelan.cofi.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

@Composable
fun LazyGridItemScope.RecipeListInfoBox(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val swipeableState = rememberSwipeableState(initialValue = 1) {
        when (it) {
            0 -> onDismiss()
            1 -> {}
        }
        true
    }
    val localDensity = LocalDensity.current
    val offset by remember(swipeableState.offset) {
        derivedStateOf {
            with(localDensity) {
                swipeableState.offset.value.toDp()
            }
        }
    }
    val screenWidthPx by remember(screenWidth) {
        derivedStateOf {
            with(localDensity) {
                screenWidth.dp.toPx()
            }
        }
    }
    val alpha by remember(screenWidth, offset) {
        derivedStateOf {
            (screenWidth - offset.value) / screenWidth
        }
    }
    RecipeListItemBackground(
        modifier = Modifier
            .animateItemPlacement()
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Horizontal,
                anchors = mapOf(0f to 1, screenWidthPx to 0),
            )
            .offset(x = offset)
            .alpha(alpha),
        contentPadding = PaddingValues(start = Spacing.medium, bottom = Spacing.medium),
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
        ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
            text()
        }
    }
}

// @Preview
// @Composable
// fun PreviewRecipeListInfoBox() {
//    RecipeListInfoBox(onClick = {}, onDismiss = {}) {
//        Text(text = "BLEH BLEH")
//    }
// }
