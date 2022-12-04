package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.ambient.AmbientModeSupport.AmbientCallback
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.omelan.cofi.wearos.presentation.components.KeyEventHandler
import com.omelan.cofi.wearos.presentation.pages.RecipeDetails
import com.omelan.cofi.wearos.presentation.pages.RecipeList
import com.omelan.cofi.wearos.presentation.theme.CofiTheme

class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val keyEventHandlers = mutableListOf<KeyEventHandler>()
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyEventHandlers.reversed().any { it(keyCode, event) } || super.onKeyDown(
            keyCode,
            event,
        )
    }

    private lateinit var ambientController: AmbientModeSupport.AmbientController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ambientController = AmbientModeSupport.attach(this)
        ambientController.setAmbientOffloadEnabled(false)
        setContent {
            val navController = rememberSwipeDismissableNavController()
            CompositionLocalProvider(
                LocalKeyEventHandlers provides keyEventHandlers,
                LocalAmbientModeProvider provides ambientController,
            ) {
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
                                RecipeDetails(
                                    id,
                                    onTimerRunning = { isTimerRunning ->
                                        val flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                        if (isTimerRunning) {
                                            window.addFlags(flag)
                                        } else {
                                            window.clearFlags(flag)
                                        }
                                    },
                                )
                            }
                        }
                        TimeText()
                    }
                }
            }
        }
    }

    override fun getAmbientCallback() = object : AmbientCallback() {}
}

val LocalKeyEventHandlers = compositionLocalOf<MutableList<KeyEventHandler>> {
    error("LocalKeyEventHandlers is not provided")
}

val LocalAmbientModeProvider = compositionLocalOf<AmbientModeSupport.AmbientController> {
    error("AmbientModeProvider is not provided")
}
