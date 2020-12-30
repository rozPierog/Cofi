package com.omelan.burr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
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
        var topPaddingInPx = 0
        setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            topPaddingInPx = insets.systemWindowInsetTop
            insets.consumeSystemWindowInsets()
        }
        setContent {
            val navController = rememberNavController()
            Column {

                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    backgroundColor = colorResource(id = R.color.navigationBar),
                    contentColor = colorResource(id = R.color.textPrimary),
                    modifier = Modifier.padding(
                        top = (topPaddingInPx / (
                                resources?.displayMetrics?.density
                                    ?: 1f
                                )).dp
                    )
                )
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

}
