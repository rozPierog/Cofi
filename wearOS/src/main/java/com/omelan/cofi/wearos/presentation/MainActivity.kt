package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.omelan.cofi.wearos.presentation.components.KeyEventHandler
import com.omelan.cofi.wearos.presentation.pages.RecipeDetails
import com.omelan.cofi.wearos.presentation.pages.RecipeList
import com.omelan.cofi.wearos.presentation.theme.CofiTheme

class MainActivity : ComponentActivity() {
    private val keyEventHandlers = mutableListOf<KeyEventHandler>()
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyEventHandlers.reversed().any { it(keyCode, event) } || super.onKeyDown(
            keyCode,
            event,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberSwipeDismissableNavController()
            CompositionLocalProvider(LocalKeyEventHandlers provides keyEventHandlers) {
                CofiTheme {
                    Box {
                        SwipeDismissableNavHost(
                            navController = navController,
                            startDestination = "recipe_list",
                        ) {
                            composable("recipe_list") {
                                RecipeList(
                                    goToDetails = { recipe ->
                                        navController.navigate(route = "recipe_details/${recipe.id}")
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
}

val LocalKeyEventHandlers = compositionLocalOf<MutableList<KeyEventHandler>> {
    error("LocalKeyEventHandlers is not provided")
}