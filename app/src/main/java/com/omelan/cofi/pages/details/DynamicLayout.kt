@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.FabType
import com.omelan.cofi.utils.getDefaultPadding

@Composable
fun rememberIsPhoneLayout(
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1920.dp, 1080.dp)),
): Boolean {
    val configuration = LocalConfiguration.current
    val isPhoneLayout by remember(
        windowSizeClass.widthSizeClass,
        configuration.screenHeightDp,
        configuration.screenWidthDp,
    ) {
        derivedStateOf {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
                (configuration.screenHeightDp / configuration.screenWidthDp.toFloat() > 1.3)
        }
    }
    return isPhoneLayout
}

@Composable
fun TabletLayout(
    paddingValues: PaddingValues,
    description: (@Composable (Modifier) -> Unit)? = null,
    timer: @Composable (Modifier) -> Unit,
    upNext: LazyListScope.() -> Unit,
    steps: LazyListScope.() -> Unit,
    isInPiP: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                if (isInPiP) {
                    PaddingValues(0.dp)
                } else {
                    paddingValues
                },
            ),

        horizontalArrangement = Arrangement.Center,
    ) {
        timer(
            Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterVertically)
                .padding(getDefaultPadding(additionalBottomPadding = 0.dp)),
        )
        if (!isInPiP) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = Spacing.normal),
                contentPadding = PaddingValues(bottom = Spacing.bigFab, top = Spacing.big),
            ) {
                description?.let {
                    item("description") {
                        description(Modifier.animateItem())
                    }
                }
                upNext()
                steps()
            }
        }
    }
}

@Composable
fun PhoneLayout(
    paddingValues: PaddingValues,
    description: (@Composable (Modifier) -> Unit)? = null,
    timer: @Composable (Modifier) -> Unit,
    upNext: LazyListScope.() -> Unit,
    steps: LazyListScope.() -> Unit,
    isInPiP: Boolean,
    lazyListState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = if (isInPiP) {
            PaddingValues(0.dp)
        } else {
            getDefaultPadding(paddingValues = paddingValues, FabType.Big)
        },
        state = lazyListState,
    ) {
        if (!isInPiP && (description != null)) {
            item("description") {
                description(Modifier.animateItem())
            }
        }
        item("timer") {
            timer(Modifier.animateItem())
        }
        upNext()
        if (!isInPiP) {
            steps()
        }
    }
}
