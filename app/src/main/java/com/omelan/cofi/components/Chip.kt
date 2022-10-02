package com.omelan.cofi.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    value: String,
    onCheck: (Boolean) -> Unit,
    isChecked: Boolean,
) {
    val fabShape by animateDpAsState(
        targetValue = if (!isChecked) 12.dp else 14.dp,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0f)
        } else {
            MaterialTheme.colorScheme.outline
        },
    )
    val containerColor by animateColorAsState(
        targetValue = if (!isChecked) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0f)
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
    )
    val contentColor by animateColorAsState(
        targetValue = if (!isChecked) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        },
    )
    Button(
        onClick = { onCheck(!isChecked) },
        shape = RoundedCornerShape(fabShape),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = modifier.padding(Spacing.xSmall),
    ) {
        Text(text = value, color = contentColor)
    }
}

@Preview
@Composable
fun PreviewChip() {
    var isSelected by remember { mutableStateOf(false) }
    Chip(value = "Chip the dog", onCheck = { isSelected = it }, isChecked = isSelected)
}