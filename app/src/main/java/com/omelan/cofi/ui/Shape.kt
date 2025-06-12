package com.omelan.cofi.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val shapes = Shapes()

private val small = 0.dp
private val medium = 12.dp
private val large = 16.dp

val firstItem =
    RoundedCornerShape(topEnd = large, topStart = large, bottomEnd = small, bottomStart = small)
val lastItem =
    RoundedCornerShape(topEnd = small, topStart = small, bottomEnd = large, bottomStart = large)
val middleItem =
    RoundedCornerShape(topEnd = small, topStart = small, bottomEnd = small, bottomStart = small)
val aloneItem =
    RoundedCornerShape(topEnd = large, topStart = large, bottomEnd = large, bottomStart = large)
