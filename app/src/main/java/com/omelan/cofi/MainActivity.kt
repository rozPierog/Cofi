package com.omelan.cofi

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.omelan.cofi.pages.RecipeDetails
import com.omelan.cofi.model.AppDatabase
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.RecipeViewModel
import com.omelan.cofi.model.StepsViewModel
import com.omelan.cofi.pages.RecipeEdit
import com.omelan.cofi.pages.RecipeList
import com.omelan.cofi.pages.details.RecipeDetails
import com.omelan.cofi.pages.settings.AppSettings
import com.omelan.cofi.pages.settings.AppSettingsAbout
import com.omelan.cofi.pages.settings.BackupRestoreSettings
import com.omelan.cofi.pages.settings.TimerSettings
import com.omelan.cofi.pages.settings.licenses.LicensesList
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.share.StepsViewModel
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.InstantUtils
import com.omelan.cofi.utils.WearUtils
import com.omelan.cofi.utils.checkPiPPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

val LocalPiPState = staticCompositionLocalOf<Boolean> {
    error("AmbientPiPState value not available.")
}

const val appDeepLinkUrl = "https://rozpierog.github.io"

const val tweenDuration = 200

@ExperimentalMaterial3WindowSizeClassApi
@OptIn(ExperimentalAnimationApi::class)
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

    private val onTimerRunning: (Boolean) -> Unit = { isRunning ->
        mainActivityViewModel.setCanGoToPiP(isRunning)
        if (isRunning) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            blockPip()
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun blockPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setPictureInPictureParams(
                PictureInPictureParams.Builder().setAutoEnterEnabled(false).build(),
            )
        }
    }

    @Composable
    fun MainList(navController: NavController) {
        RecipeList(
            navigateToRecipe = { recipeId -> navController.navigate(route = "recipe/$recipeId") },
            addNewRecipe = { navController.navigate(route = "add_recipe") },
            goToSettings = { navController.navigate(route = "settings") },
        )
    }

    @Composable
    fun MainRecipeDetails(
        navController: NavController,
        recipeId: Int,
        goBack: () -> Unit,
        windowSizeClass: WindowSizeClass,
        db: AppDatabase,
    ) {
        val pipState = LocalPiPState.current
        RecipeDetails(
            recipeId = recipeId,
            onRecipeEnd = { recipe ->
                lifecycleScope.launch {
                    db.recipeDao().updateRecipe(recipe.copy(lastFinished = Date().time))
                }
                if (InstantUtils.isInstantApp(this@MainActivity) && !pipState) {
                    InstantUtils.showInstallPrompt(this@MainActivity)
                } else {
                    lifecycleScope.launch {
                        val deepLinkIntent = Intent(
                            Intent.ACTION_VIEW,
                            "$appDeepLinkUrl/recipe/$recipeId".toUri(),
                            this@MainActivity,
                            MainActivity::class.java,
                        )
                        val shortcut =
                            ShortcutInfoCompat.Builder(this@MainActivity, recipeId.toString())
                                .setShortLabel(recipe.name)
                                .setLongLabel(recipe.name)
                                .setIcon(
                                    IconCompat.createWithResource(
                                        this@MainActivity,
                                        recipe.recipeIcon.icon,
                                    ),
                                )
                                .setIntent(deepLinkIntent)
                                .build()
                        ShortcutManagerCompat.pushDynamicShortcut(this@MainActivity, shortcut)
                    }
                }
            },
            goBack = goBack,
            goToEdit = { navController.navigate(route = "edit/$recipeId") },
            onTimerRunning = onTimerRunning,
            windowSizeClass = windowSizeClass,
        )
    }

    @Composable
    fun MainEditRecipe(
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        goBack: () -> Unit,
        db: AppDatabase,
    ) {
        val recipeId = backStackEntry.arguments?.getInt("recipeId")
            ?: throw IllegalStateException("No Recipe ID")
        val recipeViewModel: RecipeViewModel = viewModel()
        val stepsViewModel: StepsViewModel = viewModel()
        val recipe by recipeViewModel.getRecipe(recipeId)
            .observeAsState(Recipe(name = "", description = ""))
        val steps by stepsViewModel.getAllStepsForRecipe(recipeId)
            .observeAsState(listOf())
        RecipeEdit(
            goBack = goBack,
            isEditing = true,
            recipeToEdit = recipe,
            stepsToEdit = steps,
            saveRecipe = { newRecipe, newSteps ->
                lifecycleScope.launch {
                    db.recipeDao().updateRecipe(newRecipe)
                    db.stepDao().deleteAllStepsForRecipe(newRecipe.id)
                    db.stepDao().insertAll(newSteps)
                }
                goBack()
            },
            cloneRecipe = { newRecipe, newSteps ->
                lifecycleScope.launch {
                    val idOfRecipe = db.recipeDao().insertRecipe(
                        newRecipe.copy(
                            id = 0,
                            name = applicationContext.resources.getString(
                                R.string.recipe_clone_suffix,
                                recipe.name,
                            ),
                        ),
                    )
                    db.stepDao().insertAll(
                        newSteps.map {
                            it.copy(recipeId = idOfRecipe.toInt(), id = 0)
                        },
                    )
                }
                navController.navigate("list") {
                    this.popUpTo("list") {
                        inclusive = true
                    }
                }
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
            },
        )
    }

    @Composable
    fun MainAddRecipe(goBack: () -> Unit, db: AppDatabase) {
        RecipeEdit(
            saveRecipe = { recipe, steps ->
                lifecycleScope.launch {
                    val idOfRecipe = db.recipeDao().insertRecipe(recipe)
                    db.stepDao().insertAll(steps.map { it.copy(recipeId = idOfRecipe.toInt()) })
                }
                goBack()
            },
            goBack = goBack,
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    fun MainNavigation() {
        val navController = rememberAnimatedNavController()
        val db = AppDatabase.getInstance(this)
        val isInPiP by mainActivityViewModel.pipState.observeAsState(false)
        val goBack: () -> Unit = {
            navController.popBackStack()
        }
        val systemUiController = rememberSystemUiController()
        val windowSizeClass = calculateWindowSizeClass(this)
        val intent by mainActivityViewModel.intent.observeAsState()
        LaunchedEffect(intent) {
            navController.handleDeepLink(intent)
        }
        CofiTheme(monet) {
            val darkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = darkIcons,
            )
            systemUiController.setNavigationBarColor(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
                darkIcons = darkIcons,
            )
            CompositionLocalProvider(
                LocalPiPState provides isInPiP,
            ) {
                AnimatedNavHost(
                    navController,
                    startDestination = "list",
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    enterTransition = {
                        fadeIn(tween(tweenDuration)) +
                                slideIntoContainer(
                                    AnimatedContentScope.SlideDirection.End,
                                    animationSpec = tween(tweenDuration),
                                    initialOffset = { fullWidth -> -fullWidth / 5 },
                                )
                    },
                    exitTransition = {
                        fadeOut(tween(tweenDuration)) +
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Start,
                                    animationSpec = tween(tweenDuration),
                                    targetOffset = { fullWidth -> fullWidth / 5 },
                                )
                    },
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
                            },
                        ),
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getInt("recipeId")
                            ?: throw IllegalStateException("No Recipe ID")
                        MainRecipeDetails(navController, recipeId, goBack, windowSizeClass, db)
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
                                goToBackupRestore = {
                                    navController.navigate("backup")
                                },
                                goToTimerSettings = {
                                    navController.navigate("timer")
                                },
                            )
                        }
                        composable("timer") {
                            TimerSettings(goBack = goBack)
                        }
                        composable("backup") {
                            BackupRestoreSettings(
                                goBack = goBack,
                                goToRoot = {
                                    navController.navigate("list") {
                                        popUpTo("list") {
                                            inclusive = true
                                        }
                                    }
                                },
                            )
                        }
                        composable("about") {
                            AppSettingsAbout(
                                goBack = goBack,
                                openLicenses = {
                                    navController.navigate("licenses")
                                },
                            )
                        }
                        composable("about") {
                            AppSettingsAbout(
                                goBack = goBack,
                                openLicenses = {
                                    navController.navigate("licenses")
                                },
                            )
                        }
                        composable("licenses") {
                            LicensesList(goBack = goBack)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(intent)
        if (newIntent != null) {
            mainActivityViewModel.setIntent(newIntent)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        mainActivityViewModel.setIsInPiP(isInPictureInPictureMode)
    }

    override fun onUserLeaveHint() {
        val isPiPEnabledFlow: Flow<Boolean> = DataStore(this).getPiPSetting()
        var isPiPEnabled: Boolean
        runBlocking {
            isPiPEnabled = isPiPEnabledFlow.first()
        }
        if (mainActivityViewModel.canGoToPiP.value == true && isPiPEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            }
        }
    }

    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        onResumedCompat()
        super.onTopResumedActivityChanged(isTopResumedActivity)
    }

    override fun onResume() {
        onResumedCompat()
        super.onResume()
    }

    private fun onResumedCompat() {
        val currentPiPStatus = mainActivityViewModel.canGoToPiP.value ?: false
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            currentPiPStatus &&
            !checkPiPPermission(this)
        ) {
            setPictureInPictureParams(
                PictureInPictureParams.Builder().setAutoEnterEnabled(false).build(),
            )
        }
        WearUtils.observeChangesAndSendToWear(this)
    }

    override fun onPause() {
        super.onPause()
        WearUtils.removeObservers(this)
    }
}

