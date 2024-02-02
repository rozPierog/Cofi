package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.navigation
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.omelan.cofi.share.pages.Destinations
import com.omelan.cofi.wearos.presentation.components.KeyEventHandler
import com.omelan.cofi.wearos.presentation.pages.details.recipeDetails
import com.omelan.cofi.wearos.presentation.pages.recipeList
import com.omelan.cofi.wearos.presentation.pages.settings.LicensesList
import com.omelan.cofi.wearos.presentation.pages.settings.Settings
import com.omelan.cofi.wearos.presentation.theme.CofiTheme

class MainActivity : FragmentActivity() {
    private val keyEventHandlers = mutableListOf<KeyEventHandler>()
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyEventHandlers.reversed().any { it(keyCode, event) } ||
                super.onKeyDown(keyCode, event)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val edgeSwipeToDismissBoxState = rememberSwipeToDismissBoxState()
            val swipeDismissableNavHostState =
                rememberSwipeDismissableNavHostState(edgeSwipeToDismissBoxState)
            val navController = rememberSwipeDismissableNavController()
            CompositionLocalProvider(LocalKeyEventHandlers provides keyEventHandlers) {
                CofiTheme {
                    SwipeDismissableNavHost(
                        navController = navController,
                        state = swipeDismissableNavHostState,
                        startDestination = Destinations.RECIPE_LIST,
                    ) {
                        recipeList(navController)
                        recipeDetails(edgeSwipeToDismissBoxState, window)
                        navigation(Destinations.SETTINGS_LIST, route = Destinations.SETTINGS) {
                            composable(Destinations.SETTINGS_LIST) {
                                Settings(
                                    navigateToLicenses = {
                                        navController.navigate(Destinations.SETTINGS_LICENSES)
                                    },
                                )
                            }
                            composable(Destinations.SETTINGS_LICENSES) {
                                LicensesList()
                            }
                        }
                    }
                }
            }
        }
    }
}

val LocalKeyEventHandlers = compositionLocalOf<MutableList<KeyEventHandler>?> {
    null
}
