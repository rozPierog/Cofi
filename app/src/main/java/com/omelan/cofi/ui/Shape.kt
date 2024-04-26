package com.omelan.cofi.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp),
)

val Shapes.full: RoundedCornerShape
    get() = RoundedCornerShape(percent = 50)

val Shapes.card: RoundedCornerShape
    get() = RoundedCornerShape(12.dp)

val Shapes.modal: RoundedCornerShape
    get() = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 0.dp,
        bottomStart = 0.dp,
    )
