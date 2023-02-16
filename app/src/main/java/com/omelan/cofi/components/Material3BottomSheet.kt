@file:OptIn(ExperimentalMaterial3Api::class)

package com.omelan.cofi.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

@Composable
fun Material3BottomSheet(
    sheetState: SheetState = rememberSheetState(skipHalfExpanded = true),
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val modalBottomSheetShapeDp by animateDpAsState(
        targetValue = if (sheetState.targetValue == SheetValue.Hidden) 0.dp else 28.dp,
        animationSpec = tween(),
    )
    val modalBottomSheetShape =
        RoundedCornerShape(topEnd = modalBottomSheetShapeDp, topStart = modalBottomSheetShapeDp)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = modalBottomSheetShape,
        sheetState = sheetState,
        content = content,
    )
}
