package com.omelan.cofi.share

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

enum class StepType(
    val color: Color,
    val colorNight: Color,
    @StringRes val stringRes: Int,
    @DrawableRes val iconRes: Int,
) {
    ADD_COFFEE(
        color = brown500,
        colorNight = brown300,
        stringRes = R.string.step_type_add_coffee,
        iconRes = R.drawable.ic_coffee,
    ),
    WATER(
        color = blue600,
        colorNight = blue600,
        stringRes = R.string.step_type_water,
        iconRes = R.drawable.ic_water,
    ),
    WAIT(
        color = green600,
        colorNight = green600,
        stringRes = R.string.step_type_wait,
        iconRes = R.drawable.ic_timer,
    ),
    OTHER(
        color = greyBlue900,
        colorNight = grey300,
        stringRes = R.string.step_type_other,
        iconRes = R.drawable.ic_step_other,
    ),
    ;

    fun isNotWaitStepType(): Boolean = this != WAIT
}

open class StepTypeConverter {
    open fun stepTypeToString(type: StepType): String {
        return type.name
    }

    open fun stringToStepType(type: String): StepType {
        return when (type) {
            StepType.ADD_COFFEE.name -> StepType.ADD_COFFEE
            StepType.WATER.name -> StepType.WATER
            StepType.WAIT.name -> StepType.WAIT
            StepType.OTHER.name -> StepType.OTHER
            else -> StepType.OTHER
        }
    }
}

open class StepShared(
    open val id: Int = 0,
    open val recipeId: Int = 0,
    open val orderInRecipe: Int? = null,
    open val name: String,
    open val time: Int? = null,
    open val type: StepType,
    open val value: Int? = null,
)

private const val jsonName = "name"
private const val jsonTime = "time"
private const val jsonType = "type"
private const val jsonOrderInRecipe = "orderInRecipe"
private const val jsonValue = "value"

fun StepShared.serialize(): JSONObject = JSONObject().let {
    it.put(jsonName, name)
    it.put(jsonTime, time)
    it.put(jsonValue, value)
    it.put(jsonOrderInRecipe, orderInRecipe)
    it.put(jsonType, type.name)
    it
}

fun List<StepShared>.serialize() = JSONArray().let {
    forEach { step -> it.put(step.serialize()) }
    it
}

fun JSONObject.getIntOrNull(key: String) = try {
    getInt(key)
} catch (e: Exception) {
    null
}

fun JSONObject.toStep(recipeId: Long = 0) = StepShared(
    name = getString(jsonName),
    recipeId = recipeId.toInt(),
    time = getIntOrNull(jsonTime),
    value = getIntOrNull(jsonValue),
    orderInRecipe = getInt(jsonOrderInRecipe),
    type = StepTypeConverter().stringToStepType(getString(jsonType)),
)

fun JSONArray.toSteps(recipeId: Long = 0): List<StepShared> {
    var steps = listOf<StepShared>()
    for (i in 0 until length()) {
        steps = steps.plus(getJSONObject(i).toStep(recipeId))
    }
    return steps
}
