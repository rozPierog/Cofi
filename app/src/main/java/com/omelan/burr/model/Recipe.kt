package com.omelan.burr.model

import androidx.annotation.DrawableRes
import com.omelan.burr.R

data class Recipe(
    val name: String,
    val description: String,
    @DrawableRes val iconName: Int = R.drawable.ic_drip
)