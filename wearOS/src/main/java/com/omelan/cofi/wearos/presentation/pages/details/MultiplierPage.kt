package com.omelan.cofi.wearos.presentation.pages.details

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.wearos.R
import kotlin.math.roundToInt

const val step = 0.1f
val range = 0f..3f
val steps = (range.endInclusive / step).roundToInt() + 1

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MultiplierPage(
    multiplier: Float,
    changeMultiplier: (Float) -> Unit,
    requestFocus: Boolean = false,
    content: @Composable RowScope.(multiplier: Float) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Stepper(
        modifier = Modifier
            .onRotaryInputAccumulated {
                when {
                    it > 0 -> changeMultiplier(
                        (multiplier + step)
                            .roundToDecimals()
                            .coerceIn(range),
                    )

                    it < 0 -> changeMultiplier(
                        (multiplier - step)
                            .roundToDecimals()
                            .coerceIn(range),
                    )
                }
            }
            .focusRequester(focusRequester)
            .focusable(),
        value = multiplier,
        onValueChange = {
            changeMultiplier(it.roundToDecimals())
        },
        steps = steps - 2,
        valueRange = range,
        increaseIcon = {
            Icon(
                painterResource(id = R.drawable.ic_add),
                contentDescription = "",
            )
        },
        decreaseIcon = {
            Icon(
                painterResource(id = R.drawable.ic_remove),
                contentDescription = "",
            )
        },
    ) {
        AnimatedContent(
            targetState = multiplier,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                } else {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                }.using(
                    SizeTransform(clip = false),
                )
            },
            label = "Multiplier ticker",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content(it)
            }
        }
    }
    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ParamWithIcon(@DrawableRes iconRes: Int, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "",
        )
        Text(text = value)
    }
}


@Preview(widthDp = 200, heightDp = 200)
@Composable
fun MultiplierPagePreview() {
    var multi by remember {
        mutableFloatStateOf(1f)
    }
    MultiplierPage(multiplier = multi, changeMultiplier = { multi = it }) {
        Text(text = it.toString())
        ParamWithIcon(iconRes = R.drawable.ic_coffee, value = "${15 * it}g")
    }
}
