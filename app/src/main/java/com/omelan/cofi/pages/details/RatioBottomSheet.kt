@file:OptIn(ExperimentalMaterialApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.roundToDecimals
import kotlin.math.roundToInt

@Composable
fun RatioBottomSheet(
    sheetState: ModalBottomSheetState,
    timeMultiplier: MutableState<Float>,
    weightMultiplier: MutableState<Float>,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp),
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        sheetContent = {
            Column(modifier = Modifier.safeContentPadding()) {
                SheetContent(
                    timeMultiplier,
                    weightMultiplier,
                )
            }
        },
        content = content,
    )
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
