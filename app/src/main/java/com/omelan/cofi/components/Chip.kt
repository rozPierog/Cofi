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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omelan.cofi.ui.Spacing

private const val ANIMATION_DURATION = 300
private val animationSpecDp =
    tween<Dp>(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing)
private val animationSpecColor =
    tween<Color>(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing)

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    value: String,
    onCheck: (Boolean) -> Unit,
    isChecked: Boolean,
) {
    val fabShape by animateDpAsState(
        targetValue = if (!isChecked) 12.dp else 14.dp,
        animationSpec = animationSpecDp,
        label = "Chip Shape",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0f)
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = animationSpecColor,
        label = "Chip border color",
    )
    val containerColor by animateColorAsState(
        targetValue = if (!isChecked) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0f)
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = animationSpecColor,
        label = "Chip container color",
    )
    val contentColor by animateColorAsState(
        targetValue = if (!isChecked) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        },
        animationSpec = animationSpecColor,
        label = "Chip content color",
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
