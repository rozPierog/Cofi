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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing
import java.math.RoundingMode

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
    Text(
        text = "Weight multiplier",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
    )
    SliderWithValue(value = weightMultiplier)
    Text(
        text = "Time multiplier",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
    )
    SliderWithValue(value = timeMultiplier)
}

@Composable
private fun SliderWithValue(value: MutableState<Float>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Slider(
            valueRange = 0f..5f,
            steps = 51,
            value = value.value,
            onValueChange = {
                val rounded = it.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
                value.value = rounded
            },
            modifier = Modifier.weight(1f, true),
        )
        Text(
            text = "${value.value}", textAlign = TextAlign.Center,
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
