package com.omelan.cofi.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
    backgroundColor: Color = MaterialTheme.colors.background,
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