@file:OptIn(ExperimentalMaterial3Api::class)

package com.omelan.cofi.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Material3BottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismissRequest: () -> Unit,
    content: (dismiss: () -> Unit) -> @Composable ColumnScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        coroutineScope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }
    val modalBottomSheetShapeDp by animateDpAsState(
        targetValue = if (sheetState.targetValue == SheetValue.Hidden) 0.dp else 28.dp,
        animationSpec = tween(),
        label = "Bottom Sheet top radii",
    )
    val modalBottomSheetShape =
        RoundedCornerShape(topEnd = modalBottomSheetShapeDp, topStart = modalBottomSheetShapeDp)

    val hideSheet: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
            onDismissRequest()
        }

    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = modalBottomSheetShape,
        sheetState = sheetState,
        content = content(hideSheet),
        contentWindowInsets = { WindowInsets(0) },
    )
}
