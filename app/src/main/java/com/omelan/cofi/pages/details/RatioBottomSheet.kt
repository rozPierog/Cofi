@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3BottomSheet
import com.omelan.cofi.share.components.slideUpDown
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.ui.Spacing
import kotlin.math.roundToInt

@Composable
fun RatioBottomSheet(
    timeMultiplier: Float,
    setTimeMultiplier: (Float) -> Unit,
    weightMultiplier: Float,
    setWeightMultiplier: (Float) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Material3BottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .waterfallPadding()
                .padding(horizontal = Spacing.big),
        ) {
            SheetContent(
                timeMultiplier,
                setTimeMultiplier,
                weightMultiplier,
                setWeightMultiplier,
            )
        }
    }
}

@Composable
private fun SheetContent(
    timeMultiplier: Float,
    setTimeMultiplier: (Float) -> Unit,
    weightMultiplier: Float,
    setWeightMultiplier: (Float) -> Unit,
) {
    Title(stringResource(id = R.string.recipe_details_multiply_weight))
    SliderWithValue(weightMultiplier, setWeightMultiplier)
    Title(stringResource(id = R.string.recipe_details_multiply_time))
    SliderWithValue(timeMultiplier, setTimeMultiplier)
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

const val step = 0.1f
val range = 0f..3f
val steps = (range.endInclusive / step).roundToInt() + 1

@Composable
private fun SliderWithValue(value: Float, setValue: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Slider(
            valueRange = range,
            steps = steps,
            value = value,
            onValueChange = { setValue(it.roundToDecimals()) },
            modifier = Modifier.weight(1f, true),
        )
        AnimatedContent(
            targetState = value,
            transitionSpec = slideUpDown { target, initial -> target > initial },
            label = "slider value",
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = it.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = Spacing.normal),
            )
        }
    }
}
