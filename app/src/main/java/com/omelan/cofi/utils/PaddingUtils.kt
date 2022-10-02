package com.omelan.cofi.utils

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

enum class FabType(val padding: Dp) { Big(Spacing.bigFab), Normal(Spacing.fab) }

@Composable
fun getDefaultPadding(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    fabType: FabType? = null,
    skipNavigationBarPadding: Boolean = false,
    additionalBottomPadding: Dp = Spacing.big,
    additionalTopPadding: Dp = Spacing.small,
    additionalStartPadding: Dp = Spacing.big,
    additionalEndPadding: Dp = Spacing.big,
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val navigationBarPadding = if (!skipNavigationBarPadding) {
        WindowInsets.navigationBars.asPaddingValues()
    } else {
        PaddingValues(0.dp)
    }
    val fabPadding = fabType?.padding ?: 0.dp
    return PaddingValues(
        start = navigationBarPadding.calculateStartPadding(layoutDirection) +
            paddingValues.calculateStartPadding(layoutDirection) +
            additionalStartPadding,
        top = navigationBarPadding.calculateTopPadding() +
            paddingValues.calculateTopPadding() + additionalTopPadding,
        bottom = navigationBarPadding.calculateBottomPadding() +
            paddingValues.calculateBottomPadding() + fabPadding +
            additionalBottomPadding,
        end = navigationBarPadding.calculateEndPadding(layoutDirection) +
            paddingValues.calculateEndPadding(layoutDirection) +
            additionalEndPadding,
    )
}
