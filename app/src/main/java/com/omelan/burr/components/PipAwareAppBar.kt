package com.omelan.burr.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.burr.AmbientPiPState
import com.omelan.burr.R
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun PiPAwareAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {
        Text(
            text = stringResource(id = R.string.app_name),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp
) {
    if (!AmbientPiPState.current) {
        InsetAwareTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            elevation = elevation,
        )
    }
}

@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = Modifier.statusBarsPadding()
        )
    }
}

@Composable
@Preview
fun PiPAwareAppBarPreview() {
    PiPAwareAppBar()
}