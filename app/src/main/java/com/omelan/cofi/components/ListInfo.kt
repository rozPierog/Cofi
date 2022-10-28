@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.omelan.cofi.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun LazyGridItemScope.RecipeListInfoBox(onClick: () -> Unit, onDismiss: () -> Unit, text: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val swipeableState = rememberSwipeableState(initialValue = 1) {
        when (it) {
            0 -> onDismiss()
            1 -> {}
        }
        true
    }
    val alpha by remember(screenWidth, swipeableState.offset) {
        derivedStateOf {
            (screenWidth - swipeableState.offset.value) / screenWidth
        }
    }
    RecipeListItemBackground(
        modifier = Modifier.animateItemPlacement()
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Horizontal,
                anchors = mapOf(0f to 1, 10000f to 0),
            )
            .offset(x = swipeableState.offset.value.dp)
            .alpha(alpha),
        onClick = onClick,
    ) {
        text()
    }
}

//@Preview
//@Composable
//fun PreviewRecipeListInfoBox() {
//    RecipeListInfoBox(onClick = {}, onDismiss = {}) {
//        Text(text = "BLEH BLEH")
//    }
//}
