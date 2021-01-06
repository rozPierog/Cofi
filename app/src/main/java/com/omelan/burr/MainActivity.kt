package com.omelan.burr

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.omelan.burr.components.PiPAwareAppBar
import com.omelan.burr.model.AppDatabase
import com.omelan.burr.model.dummySteps
import com.omelan.burr.pages.AddNewRecipePage
import com.omelan.burr.pages.RecipeList
import com.omelan.burr.pages.RecipeTimerPage
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.utils.SystemUIHelpers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @ExperimentalLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        SystemUIHelpers.setSystemBarsColors(view = window.decorView, resources)
        setContent {
            MainNavigation()
        }
    }

    @ExperimentalLayout
    @Composable
    fun MainNavigation() {
        val navController = rememberNavController()
        val db = AppDatabase.getInstance(this)
        val isInPiP = mainActivityViewModel.pipState.observeAsState(false)
        // Transition animations for navController.navigate are not supported right now
        // monitor this issue https://issuetracker.google.com/issues/172112072
        // val builder: NavOptionsBuilder.() -> Unit = {
        //     anim {
        //         enter = android.R.anim.slide_out_right
        //         exit = android.R.anim.fade_out
        //         popEnter = android.R.anim.slide_out_right
        //         popExit = android.R.anim.fade_out
        //     }
        // }
        BurrTheme {
            Column {
                PiPAwareAppBar(isInPiP = isInPiP.value)
                NavHost(navController, startDestination = "list") {
                    composable("list") {
                        mainActivityViewModel.setCanGoToPiP(false)
                        RecipeList(
                            navigateToRecipe = { recipeId ->
                                navController.navigate(
                                    route = "recipe/${recipeId}",
                                )
                            },
                            addNewRecipe = {
                                navController.navigate(
                                    "add_recipe",
                                )
                            },
                        )
                    }
                    composable(
                        "recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getInt("recipeId")
                            ?: throw IllegalStateException("No Recipe ID")
                        mainActivityViewModel.setCanGoToPiP(true)
                        RecipeTimerPage(
                            recipeId = recipeId,
                            isInPiP = isInPiP.value,
                            onRecipeEnd = { recipe ->
                                lifecycleScope.launch {
                                    db.recipeDao()
                                        .updateRecipe(recipe.copy(lastFinished = Date().time))
                                }
                            }
                        )
                    }
                    composable("add_recipe") {
                        mainActivityViewModel.setCanGoToPiP(false)
                        AddNewRecipePage(steps = dummySteps, saveRecipe = { recipe, steps ->
                            lifecycleScope.launch {
                                val idOfRecipe = db.recipeDao().insertRecipe(recipe)
                                db.stepDao()
                                    .insertAll(steps.map { it.copy(recipeId = idOfRecipe.toInt()) })

                            }
                            navController.navigate(
                                "list",
                            )
                        })
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        SystemUIHelpers.setSystemBarsColors(view = window.decorView, resources)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        mainActivityViewModel.setIsInPiP(isInPictureInPictureMode)
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            mainActivityViewModel.canGoToPiP.value == true
        ) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(Rational(1, 1)).build()
            )
        }
    }
}
