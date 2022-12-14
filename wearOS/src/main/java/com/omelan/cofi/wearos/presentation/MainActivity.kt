package com.omelan.cofi.wearos.presentation

import RecipeDetails
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.ambient.AmbientModeSupport.AmbientCallback
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.omelan.cofi.wearos.presentation.components.KeyEventHandler
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
            val edgeSwipeToDismissBoxState = rememberSwipeToDismissBoxState()
            val swipeDismissableNavHostState =
                rememberSwipeDismissableNavHostState(edgeSwipeToDismissBoxState)
            val navController = rememberSwipeDismissableNavController()
            CompositionLocalProvider(
                LocalKeyEventHandlers provides keyEventHandlers,
                LocalAmbientModeProvider provides ambientController,
            ) {
                CofiTheme {
                    SwipeDismissableNavHost(
                        navController = navController,
                        state = swipeDismissableNavHostState,
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
                                modifier = Modifier.edgeSwipeToDismiss(edgeSwipeToDismissBoxState, 10.dp),
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
