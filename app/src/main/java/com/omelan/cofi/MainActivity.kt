package com.omelan.cofi

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.omelan.cofi.model.AppDatabase
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.RecipeViewModel
import com.omelan.cofi.model.StepsViewModel
import com.omelan.cofi.pages.RecipeDetails
import com.omelan.cofi.pages.RecipeEdit
import com.omelan.cofi.pages.RecipeList
import com.omelan.cofi.pages.settings.AppSettings
import com.omelan.cofi.pages.settings.AppSettingsAbout
import com.omelan.cofi.pages.settings.Licenses
import com.omelan.cofi.ui.CofiTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

val LocalPiPState = staticCompositionLocalOf<Boolean> {
    error("AmbientPiPState value not available.")
}

const val appDeepLinkUrl = "https://rozpierog.github.io"

@ExperimentalAnimatedInsets
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
class MainActivity : MonetCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Cofi)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            lifecycleScope.launchWhenCreated {
                monet.awaitMonetReady()
            }
        }
        this.setContent(null) {
            MainNavigation()
        }
    }

    private fun onTimerRunning(isRunning: Boolean) {
        mainActivityViewModel.setCanGoToPiP(isRunning)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setPictureInPictureParams(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(1, 1))
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            setAutoEnterEnabled(isRunning)
                            setSeamlessResizeEnabled(true)
                        }
                    }.build()
            )
        }
        if (isRunning) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @Composable
    fun MainList(navController: NavController) {
        RecipeList(
            navigateToRecipe = { recipeId ->
                navController.navigate(
                    route = "recipe/$recipeId",
                )
            },
            addNewRecipe = {
                navController.navigate(
                    route = "add_recipe",
                )
            },
            goToSettings = {
                navController.navigate(
                    route = "settings"
                )
            }
        )
    }

    @Composable
    fun MainRecipeDetails(
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        goBack: () -> Unit,
        db: AppDatabase
    ) {
        val recipeId = backStackEntry.arguments?.getInt("recipeId")
            ?: throw IllegalStateException("No Recipe ID")
        RecipeDetails(
            recipeId = recipeId,
            onRecipeEnd = { recipe ->
                lifecycleScope.launch {
                    db.recipeDao().updateRecipe(recipe.copy(lastFinished = Date().time))
                }
            },
            goBack = {
                onTimerRunning(false)
                goBack()
            },
            goToEdit = {
                onTimerRunning(false)
                navController.navigate(
                    route = "edit/$recipeId",
                )
            },
            onTimerRunning = { onTimerRunning(it) },
        )
    }

    @Composable
    fun MainEditRecipe(
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        goBack: () -> Unit,
        db: AppDatabase
    ) {
        val recipeId = backStackEntry.arguments?.getInt("recipeId")
            ?: throw IllegalStateException("No Recipe ID")
        val recipeViewModel: RecipeViewModel = viewModel()
        val stepsViewModel: StepsViewModel = viewModel()
        val recipe = recipeViewModel.getRecipe(recipeId)
            .observeAsState(Recipe(name = "", description = ""))
        val steps = stepsViewModel.getAllStepsForRecipe(recipeId)
            .observeAsState(listOf())
        RecipeEdit(
            goBack = goBack,
            isEditing = true,
            recipeToEdit = recipe.value,
            stepsToEdit = steps.value,
            saveRecipe = { _recipe, _steps ->
                lifecycleScope.launch {
                    db.recipeDao().updateRecipe(_recipe)
                    db.stepDao().deleteAllStepsForRecipe(_recipe.id)
                    db.stepDao().insertAll(_steps)
                }
                goBack()
            },
            deleteRecipe = {
                lifecycleScope.launch {
                    db.recipeDao().deleteById(recipeId = recipeId)
                    db.stepDao().deleteAllStepsForRecipe(recipeId = recipeId)
                }
                navController.navigate("list") {
                    this.popUpTo("list") {
                        inclusive = true
                    }
                }
            }
        )
    }

    @Composable
    fun MainAddRecipe(goBack: () -> Unit, db: AppDatabase) {
        var isRecipeSave: Boolean = false
        RecipeEdit(
            saveRecipe = { recipe, steps ->
                lifecycleScope.launch {
          
           if(!recipe.name.isNullOrBlank() and !recipe.description.isNullOrBlank()) {
                    val idOfRecipe =
                        db.recipeDao().insertRecipe(recipe)
                    db.stepDao()
                        .insertAll(
                            steps.map {
                                it.copy(recipeId = idOfRecipe.toInt())
                            }
                        )
                                    isRecipeSave=true
                    }else{Toast.makeText(applicationContext,"Name and description can't be empty.",Toast.LENGTH_SHORT).show()  }

                    if(isRecipeSave.equals(true)  ){goBack()}

                }

            },
            
            
            
            goBack = goBack,
        )
    }

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
        val goBack: () -> Unit = {
            navController.popBackStack()
        }
        val systemUiController = rememberSystemUiController()

        CofiTheme(monet) {
            val darkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = darkIcons
            )
            systemUiController.setNavigationBarColor(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
                darkIcons = darkIcons
            )

            CompositionLocalProvider(
                LocalPiPState provides isInPiP.value,
            ) {
                NavHost(
                    navController,
                    startDestination = "list",
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
//                    composable("list_color") {
//                        ColorPicker(goToList = {
//                            navController.navigate(
//                                route = "list",
//                            )
//                        }, monet)
//                    }
                    composable("list") {
                        MainList(navController = navController)
                    }
                    composable(
                        "recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "$appDeepLinkUrl/recipe/{recipeId}"
                            }
                        ),
                    ) { backStackEntry ->
                        MainRecipeDetails(navController, backStackEntry, goBack, db)
                    }
                    composable(
                        "edit/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
                    ) { backStackEntry ->
                        MainEditRecipe(navController, backStackEntry, goBack, db)
                    }
                    composable("add_recipe") {
                        MainAddRecipe(goBack, db)
                    }
                    navigation(startDestination = "settings_list", route = "settings") {
                        composable("settings_list") {
                            AppSettings(
                                goBack = goBack,
                                goToAbout = {
                                    navController.navigate("about")
                                },
                            )
                        }
                        composable("about") {
                            AppSettingsAbout(
                                goBack = goBack,
                                openLicenses = {
                                    navController.navigate("licenses")
                                }
                            )
                        }
                        composable("licenses") {
                            Licenses(goBack = goBack)
                        }
                    }
                }
            }
        }
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        SystemUIHelpers.setStatusBarIconsTheme(activity = this, darkIcons = false)
//    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        mainActivityViewModel.setIsInPiP(isInPictureInPictureMode)
    }

    override fun onUserLeaveHint() {

        val isPiPEnabledFlow: Flow<Boolean> = DataStore(this).getPiPSetting()
        var isPiPEnabled: Boolean
        runBlocking {
            isPiPEnabled = isPiPEnabledFlow.first()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            mainActivityViewModel.canGoToPiP.value == true &&
            isPiPEnabled
        ) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        }
    }
}
