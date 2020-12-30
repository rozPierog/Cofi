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
            Recipe(
                id = "1",
                name = "Ultimate v60",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pellentesque lacus nec ex pellentesque gravida. Morbi a fringilla ex. Ut lacinia velit vel diam luctus, et facilisis risus commodo. Sed suscipit tellus leo, sit amet cursus augue posuere id. Cras posuere, nibh in tempus vestibulum, justo neque rhoncus nibh, eget efficitur quam mi at enim. Ut quis luctus tellus. Proin porttitor, ex vitae tempus ornare, dolor lectus viverra turpis, ac vulputate leo nunc sit amet magna. Proin odio sapien, commodo eget justo vel, malesuada fringilla erat. Donec vitae vestibulum tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nec rutrum tortor. Ut pulvinar quis diam ac semper. Praesent ornare id leo quis porttitor. Nullam vitae augue ac nibh viverra hendrerit. Cras purus turpis, vulputate ac tincidunt non, mollis eget sapien."
            ),
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
