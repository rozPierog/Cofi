@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3BottomSheet
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.roundToDecimals
import kotlin.math.roundToInt

@Composable
fun RatioBottomSheet(
    timeMultiplier: MutableState<Float>,
    weightMultiplier: MutableState<Float>,
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
                weightMultiplier,
            )
        }
    }
}

@Composable
private fun SheetContent(
    timeMultiplier: MutableState<Float>,
    weightMultiplier: MutableState<Float>,
) {
    Title(stringResource(id = R.string.recipe_details_multiply_weight))
    SliderWithValue(value = weightMultiplier)
    Title(stringResource(id = R.string.recipe_details_multiply_time))
    SliderWithValue(value = timeMultiplier)
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
private fun SliderWithValue(value: MutableState<Float>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Slider(
            valueRange = range,
            steps = steps,
            value = value.value,
            onValueChange = {
                value.value = it.roundToDecimals()
            },
            modifier = Modifier.weight(1f, true),
        )
        Text(
            color = MaterialTheme.colorScheme.onSurface,
            text = "${value.value}",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = Spacing.normal),
        )
//        OutlinedTextField(
//            modifier = Modifier
//                .weight(1f)
//                .align(Alignment.CenterVertically),
//            value = "${value.value}",
//            onValueChange = { value.value = it.toFloat() },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Decimal,
//                autoCorrect = false,
//                capitalization = KeyboardCapitalization.None,
//                imeAction = ImeAction.None,
//            ),
//        )
    }
}
