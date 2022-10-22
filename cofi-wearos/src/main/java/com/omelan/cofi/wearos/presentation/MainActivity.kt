package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.omelan.cofi.wearos.presentation.pages.RecipeDetails
import com.omelan.cofi.wearos.presentation.pages.RecipeList
import com.omelan.cofi.wearos.presentation.theme.CofiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChannelHandler.listenToChanges(this)

        setContent {
            val navController = rememberSwipeDismissableNavController()
            CofiTheme {
                Box {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "recipe_list",
                    ) {
                        composable("recipe_list") {
                            RecipeList(
                                goToDetails = {
                                    navController.navigate(route = "recipe_details/${it.id}")
                                },
                            )
                        }
                        composable(
                            "recipe_details/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType }),
                        ) {
                            val id = it.arguments?.getInt("id")
                                ?: throw Exception("Expected recipe id, got Null")
                            RecipeDetails(id)
                        }
                    }
                    TimeText()
                }
            }
        }
    }
}
