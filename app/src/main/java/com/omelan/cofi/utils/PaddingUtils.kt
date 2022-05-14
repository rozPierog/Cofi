package com.omelan.cofi.utils

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

val defaultFabPadding = 76.dp
val defaultBigFabPadding = 112.dp

enum class FabType(val padding: Dp) { Big(defaultBigFabPadding), Normal(defaultFabPadding) }

@Composable
fun getDefaultPadding(paddingValues: PaddingValues, fabType: FabType? = null): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val fabPadding = fabType?.padding ?: 0.dp
    return PaddingValues(
        start = navigationBarPadding.calculateStartPadding(layoutDirection) +
            paddingValues.calculateStartPadding(layoutDirection) + Spacing.big,
        top = navigationBarPadding.calculateTopPadding() +
            paddingValues.calculateTopPadding() + Spacing.small,
        bottom = navigationBarPadding.calculateBottomPadding() +
            paddingValues.calculateBottomPadding() + Spacing.big + fabPadding,
        end = navigationBarPadding.calculateEndPadding(layoutDirection) +
            paddingValues.calculateEndPadding(layoutDirection) + Spacing.big
    )
}