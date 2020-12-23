package com.omelan.burr.model

import androidx.annotation.DrawableRes
import com.omelan.burr.R

data class Recipe(
    val name: String,
    val description: String,
    val steps: List<Step> = listOf(
        Step(id = 1, name = "Rinse paper", time = 4 * 1000),
        Step(id = 2, name = "Rinse paper", time = 4 * 1000),
        Step(id = 3, name = "Rinse paper", time = 4 * 1000),
        Step(id = 4, name = "Rinse paper", time = 4 * 1000),
    ),
    @DrawableRes val iconName: Int = R.drawable.ic_drip
)