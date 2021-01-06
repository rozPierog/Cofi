package com.omelan.burr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.MainActivityViewModel
import com.omelan.burr.R
import dev.chrisbanes.accompanist.insets.statusBarsPadding


@Composable
fun PiPAwareAppBar(isInPiP: Boolean) {
    if (!isInPiP) {
        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
//            backgroundColor = colorResource(id = R.color.navigationBar),
            contentColor = colorResource(id = R.color.white),
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
    PiPAwareAppBar(isInPiP = false)
}