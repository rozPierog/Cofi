package com.omelan.burr.model

import androidx.annotation.DrawableRes
import com.omelan.burr.R

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<Step> = listOf(
        Step(id = 1, name = "Add Coffee", value = 30, time = 5 * 1000, type = StepType.ADD_COFFEE),
        Step(id = 2, name = "Add water", value = 60, time = 5 * 1000, type = StepType.WATER),
        Step(id = 3, name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
        Step(id = 4, name = "Wait", time = 35 * 1000, type = StepType.WAIT),
        Step(id = 5, name = "Add Water", time = 30 * 1000, type = StepType.WATER, value = 300),
        Step(
            id = 6, name = "Add Water", time = 30 * 1000, type = StepType.WATER, value = 200
        ),
        Step(id = 7, name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
        ),
    @DrawableRes
    val iconName: Int = R.drawable.ic_coffee
)