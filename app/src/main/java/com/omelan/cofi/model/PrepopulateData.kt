package com.omelan.cofi.model

import android.content.Context
import com.omelan.cofi.R
import com.omelan.cofi.utils.toMillis

class PrepopulateData(context: Context) {
    private val resources = context.resources
    private val v60Id = 1
    private var stepId = 0
    private fun autoStepId(): Int {
        stepId += 1
        return stepId
    }

    val recipes = arrayOf(
        Recipe(
            id = v60Id,
            name = resources.getString(R.string.prepopulate_v60_name),
            description = resources.getString(R.string.prepopulate_v60_description),
            recipeIcon = RecipeIcon.V60,
        )
    )
    val steps = listOf(
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_coffee),
            value = 30,
            time = 5.toMillis(),
            type = StepType.ADD_COFFEE,
            orderInRecipe = 0
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 60,
            time = 5.toMillis(),
            type = StepType.WATER,
            orderInRecipe = 1
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = 2
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 35.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = 3
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 300,
            orderInRecipe = 4
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 200,
            orderInRecipe = 5
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = 6
        ),
    )
}