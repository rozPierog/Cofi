package com.omelan.cofi.share.model

import android.content.Context
import com.omelan.cofi.share.R
import com.omelan.cofi.share.utils.toMillis

class PrepopulateData(context: Context) {
    private val resources = context.resources
    private val v60Id = 1
    private val frenchPressId = 2
    private val chemexId = 3
    private val aeroPress = 4
    private val cleverDripperId = 5
    private val v601CupId = 6
    private var _stepId = 0
    private fun autoStepId(): Int {
        _stepId += 1
        return _stepId
    }

    private val _orderInRecipeMap = mutableMapOf(
        v60Id to 0,
        frenchPressId to 0,
        chemexId to 0,
        aeroPress to 0,
        cleverDripperId to 0,
        v601CupId to 0,
    )
    private fun autoOrderInRecipe(recipeId: Int): Int {
        val currentOrder = _orderInRecipeMap[recipeId] ?: 0
        _orderInRecipeMap[recipeId] = currentOrder + 1
        return currentOrder
    }

    val recipes = arrayOf(
        Recipe(
            id = v60Id,
            name = resources.getString(R.string.prepopulate_v60_name),
            description = resources.getString(R.string.prepopulate_v60_description),
            recipeIcon = RecipeIcon.V60,
        ),
        Recipe(
            id = v601CupId,
            name = resources.getString(R.string.prepopulate_v60_1cup_name),
            description = resources.getString(R.string.prepopulate_v60_1cup_description),
            recipeIcon = RecipeIcon.V60,
        ),
        Recipe(
            id = frenchPressId,
            name = resources.getString(R.string.prepopulate_frenchPress_name),
            description = resources.getString(R.string.prepopulate_frenchPress_description),
            recipeIcon = RecipeIcon.FrenchPress,
        ),
        Recipe(
            id = chemexId,
            name = resources.getString(R.string.prepopulate_chemex_name),
            description = resources.getString(R.string.prepopulate_chemex_description),
            recipeIcon = RecipeIcon.Chemex,
        ),
        Recipe(
            id = aeroPress,
            name = resources.getString(R.string.prepopulate_aero_name),
            description = resources.getString(R.string.prepopulate_aero_description),
            recipeIcon = RecipeIcon.AeroPress,
        ),
        Recipe(
            id = cleverDripperId,
            name = resources.getString(R.string.prepopulate_clever_dripper_name),
            description = resources.getString(R.string.prepopulate_clever_dripper_description),
            recipeIcon = RecipeIcon.V60,
        ),
    )
    val steps = listOf(
        // V60
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_coffee),
            value = 30.0f,
            time = 5.toMillis(),
            type = StepType.ADD_COFFEE,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 60.0f,
            time = 5.toMillis(),
            type = StepType.WATER,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 35.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 240.0f,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 200.0f,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        Step(
            id = autoStepId(),
            recipeId = v60Id,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(v60Id),
        ),
        // V60 1 cup
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            name = resources.getString(R.string.prepopulate_step_coffee),
            time = 10.toMillis(),
            value = 15f,
            type = StepType.ADD_COFFEE,
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 10.toMillis(),
            type = StepType.WATER,
            value = 50f,
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.OTHER,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WAIT,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 30.toMillis()
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WATER,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 50f,
            time = 15.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WAIT,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 10.toMillis()
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WATER,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 50f,
            time = 10.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WAIT,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 10.toMillis()
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WATER,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 50f,
            time = 10.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WAIT,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 10.toMillis()
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WATER,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 50f,
            time = 10.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.WAIT,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 10.toMillis()
        ),
        Step(
            id = autoStepId(),
            recipeId = v601CupId,
            type = StepType.OTHER,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis()
        ),
        // French Press
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_coffee),
            time = 5.toMillis(),
            type = StepType.ADD_COFFEE,
            value = 30.0f,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 500.0f,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = (4 * 60).toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_stir_crust),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_scoop_coffee),
            time = 15.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = (7 * 60).toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        Step(
            id = autoStepId(),
            recipeId = frenchPressId,
            name = resources.getString(R.string.prepopulate_step_plunge),
            time = 10.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(frenchPressId),
        ),
        // Chemex
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            orderInRecipe = autoOrderInRecipe(chemexId),
            name = resources.getString(R.string.prepopulate_step_coffee),
            type = StepType.ADD_COFFEE,
            time = 5.toMillis(),
            value = 30.0f,
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            orderInRecipe = autoOrderInRecipe(chemexId),
            name = resources.getString(R.string.prepopulate_step_water),
            type = StepType.WATER,
            time = 5.toMillis(),
            value = 60.0f,
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(chemexId),
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            orderInRecipe = autoOrderInRecipe(chemexId),
            name = resources.getString(R.string.prepopulate_step_wait),
            type = StepType.WAIT,
            time = 45.toMillis(),
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 240.0f,
            orderInRecipe = autoOrderInRecipe(chemexId),
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            name = resources.getString(R.string.prepopulate_step_water),
            time = 30.toMillis(),
            type = StepType.WATER,
            value = 200.0f,
            orderInRecipe = autoOrderInRecipe(chemexId),
        ),
        Step(
            id = autoStepId(),
            recipeId = chemexId,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(chemexId),
        ),
        // AeroPress
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_step_coffee),
            value = 11.0f,
            time = 5.toMillis(),
            type = StepType.ADD_COFFEE,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 200.0f,
            time = 8.toMillis(),
            type = StepType.WATER,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_aero_step_piston),
            time = 10.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 120.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 5.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 30.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        Step(
            id = autoStepId(),
            recipeId = aeroPress,
            name = resources.getString(R.string.prepopulate_aero_step_press),
            time = 30.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(aeroPress),
        ),
        // Clever Dripper
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_clever_dripper_step_rinse),
            time = 10.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_step_water),
            value = 300.0f,
            time = 15.toMillis(),
            type = StepType.WATER,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_step_coffee),
            value = 18.0f,
            time = 5.toMillis(),
            type = StepType.ADD_COFFEE,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_step_swirl),
            time = 10.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 120.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_clever_dripper_step_break_crust),
            time = 10.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_step_wait),
            time = 30.toMillis(),
            type = StepType.WAIT,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
        Step(
            id = autoStepId(),
            recipeId = cleverDripperId,
            name = resources.getString(R.string.prepopulate_clever_dripper_step_draw_down),
            time = 60.toMillis(),
            type = StepType.OTHER,
            orderInRecipe = autoOrderInRecipe(cleverDripperId),
        ),
    )
}
