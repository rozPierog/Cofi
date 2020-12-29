package com.omelan.burr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navigate
import com.omelan.burr.components.RecipeList
import com.omelan.burr.model.Recipe
import com.omelan.burr.pages.RecipeTimerPage
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listOfRecipes = listOf(
            Recipe(id = "1", name = "Ultimate v60", description = "Hoffman"),
            Recipe(id = "2", name = "Ultimate French Press", description = "Hoffman"),
            Recipe(id = "3", name = "Ultimate Coś tam coś tam", description = "Hoffman"),
        )

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "list") {
                composable("list") {
                    RecipeList(recipes = listOfRecipes, navigateToRecipe = { recipeId ->
                        navController.navigate("recipe/${recipeId}")
                    })
                }
                composable("recipe/{recipeId}") { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")
                    val pickedRecipe = listOfRecipes.find { it.id == recipeId }
                        ?: throw IllegalArgumentException("No recipeId on transition!")
                    RecipeTimerPage(pickedRecipe)
                }
            }

        }
    }
}
