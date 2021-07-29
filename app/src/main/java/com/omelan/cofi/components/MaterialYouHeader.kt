package com.omelan.cofi.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.omelan.cofi.R
import kotlin.math.roundToInt


private val AppBarHorizontalPadding = 4.dp
val AppBarHeight = 56.dp
private val TitleIconModifier = Modifier
    .fillMaxHeight()
    .width(72.dp - AppBarHorizontalPadding)

private const val bigMultiplier = 2

val MaterialYouHeaderTotalHeight = AppBarHeight * (bigMultiplier + 1)

@Composable
fun createValues(): Pair<Float, NestedScrollConnection> {
    val toolbarHeightPx =
        with(LocalDensity.current) { MaterialYouHeaderTotalHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember<NestedScrollConnection> {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                if (newOffset <= toolbarHeightPx) {
                    toolbarOffsetHeightPx.value = newOffset.coerceIn(-Float.MAX_VALUE, 0f)
                }
                return Offset.Zero
            }
        }
    }
    return Pair(toolbarOffsetHeightPx.value, nestedScrollConnection)
}

@Composable
fun createLazyColumnPaddings(
    additionalContentStart: Dp = 0.dp,
    additionalContentBottom: Dp = 0.dp,
    additionalContentEnd: Dp = 0.dp ,
): Pair<PaddingValues, PaddingValues> {
    val contentPadding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.systemBars,
        additionalTop = MaterialYouHeaderTotalHeight - AppBarHeight,
        additionalStart = additionalContentStart,
        additionalBottom = additionalContentBottom,
        additionalEnd = additionalContentEnd ,
    )
    val padding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.statusBars,
        additionalTop = AppBarHeight
    )
    return Pair(contentPadding, padding)
}

@Composable
fun MaterialYouHeader(
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
    backgroundColor: Color = MaterialTheme.colors.error,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 0.dp,
    firstItemOffset: Float = 0f,
) {
    val firstItemModified = firstItemOffset / 4
    val textAlpha = -firstItemModified / AppBarHeight.value * bigMultiplier

    Box {
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            elevation = elevation,
            modifier = modifier
                .padding(top = AppBarHeight.times(bigMultiplier))
                .absoluteOffset {
                    IntOffset(
                        x = 0,
                        y = firstItemOffset.roundToInt()
                    )
                },
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppBarHorizontalPadding * bigMultiplier)
                    .alpha(1 - textAlpha),
                verticalAlignment = Alignment.Bottom,
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h3) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title
                    )
                }
            }
        }
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            elevation = elevation,
            modifier = modifier
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        PaddingValues(
                            start = AppBarHorizontalPadding,
                            end = AppBarHorizontalPadding
                        )
                    )
                    .height(AppBarHeight),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (navigationIcon == null) {
                    Spacer(Modifier.width(16.dp - AppBarHorizontalPadding))
                } else {
                    Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = navigationIcon
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .alpha(textAlpha),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.h6) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = title
                        )
                    }
                }

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        Modifier.fillMaxHeight(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
            }
        }
    }

}

@Composable
@Preview
fun PreviewHeader() {
    MaterialYouHeader()
}