package com.omelan.cofi.components

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createAppBarBehavior(): TopAppBarScrollBehavior {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }
    return scrollBehavior
}

@Composable
fun PiPAwareAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {
        Text(
            text = stringResource(id = R.string.list_header),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    elevation: Dp = 0.dp
) {
    if (!LocalPiPState.current) {
        InsetAwareTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            elevation = elevation,
            scrollBehavior = scrollBehavior,
        )
    }
}

@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    elevation: Dp = 4.dp
) {
    val scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    val colors = TopAppBarDefaults.largeTopAppBarColors()
    val appBarContainerColor by colors.containerColor(scrollFraction)
    Surface(
        color = Color.Transparent,
        shadowElevation = elevation * scrollFraction,
        modifier = modifier.drawBehind {
            drawRect(appBarContainerColor)
        }
    ) {
        LargeTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    WindowInsets.navigationBars
                        .only(WindowInsetsSides.End + WindowInsetsSides.Start)
                        .asPaddingValues()
                ),
            scrollBehavior = scrollBehavior,
        )
    }
}

@Composable
@Preview
fun PiPAwareAppBarPreview() {
    InsetAwareTopAppBar(title = { Text(text = "test") })
}