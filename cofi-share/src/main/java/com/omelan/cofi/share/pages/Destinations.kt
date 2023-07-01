package com.omelan.cofi.share.pages

object Destinations {
    const val RECIPE_LIST = "list"
    const val RECIPE_EDIT = "edit/{recipeId}"
    fun recipeEdit(recipeId: Int) = "edit/${recipeId}"
    const val RECIPE_ADD = "add_recipe"
    const val RECIPE_DETAILS = "recipe/{recipeId}"
    fun recipeDetails(recipeId: Int) = "recipe/${recipeId}"
    const val SETTINGS = "settings"
    const val SETTINGS_LIST = "settings_list"
    const val SETTINGS_ABOUT = "about"
    const val SETTINGS_BACKUP = "backup"
    const val SETTINGS_TIMER = "timer"
    const val SETTINGS_LICENSES = "licenses"
}

