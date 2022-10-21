package com.omelan.cofi.share

import androidx.annotation.DrawableRes
import org.json.JSONObject


enum class RecipeIcon(@DrawableRes val icon: Int) {
    V60(R.drawable.ic_drip),
    FrenchPress(R.drawable.ic_french_press),
    Grinder(R.drawable.ic_coffee_grinder),
    Chemex(R.drawable.ic_chemex),
    Aeropress(R.drawable.ic_aeropress),
}

open class RecipeIconTypeConverter {
    open fun recipeIconToString(type: RecipeIcon): String {
        return type.name
    }

    open fun stringToRecipeIcon(type: String): RecipeIcon {
        return when (type) {
            RecipeIcon.V60.name -> RecipeIcon.V60
            RecipeIcon.FrenchPress.name -> RecipeIcon.FrenchPress
            RecipeIcon.Grinder.name -> RecipeIcon.Grinder
            RecipeIcon.Chemex.name -> RecipeIcon.Chemex
            RecipeIcon.Aeropress.name -> RecipeIcon.Aeropress
            else -> RecipeIcon.Grinder
        }
    }
}

open class RecipeShared(
    open val id: Int = 0,
    open val name: String,
    open val description: String = "",
    open val lastFinished: Long = 0L,
    open val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
)

private const val jsonName = "name"
private const val jsonDescription = "description"
private const val jsonRecipeIcon = "recipeIcon"
const val jsonSteps = "steps"

fun RecipeShared.serialize(steps: List<StepShared>? = null): JSONObject = JSONObject().run {
    put(jsonName, name)
    put(jsonDescription, description)
    put(jsonRecipeIcon, recipeIcon.name)
    put(jsonSteps, steps?.serialize())
}

fun JSONObject.toRecipe() =
    RecipeShared(
        name = getString(jsonName),
        description = getString(jsonDescription),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon)),
    )
