package com.omelan.burr

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.omelan.burr.components.RecipeList
import com.omelan.burr.model.AppDatabase
import com.omelan.burr.model.dummySteps
import com.omelan.burr.pages.AddNewRecipePage
import com.omelan.burr.pages.RecipeTimerPage
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.utils.pixelsToDp
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

class MainActivityViewModel : ViewModel() {
    private val _pipState = MutableLiveData(false)
    val pipState: LiveData<Boolean> = _pipState

    private val _canGoToPiP = MutableLiveData(false)
    val canGoToPiP: LiveData<Boolean> = _canGoToPiP

    private val _statusBarHeight = MutableLiveData(0.dp)
    val statusBarHeight: LiveData<Dp> = _statusBarHeight

    private val _navBarHeight = MutableLiveData(0.dp)
    val navBarHeight: LiveData<Dp> = _navBarHeight

    fun setStatusBarHeight(newHeight: Dp) {
        _statusBarHeight.value = newHeight
    }

    fun setNavBarHeight(newHeight: Dp) {
        _navBarHeight.value = newHeight
    }

    fun setIsInPiP(newPiPState: Boolean) {
        _pipState.value = newPiPState
    }

    fun setCanGoToPiP(newCanGoToPiP: Boolean) {
        _canGoToPiP.value = newCanGoToPiP
    }
}

@ExperimentalTime
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @ExperimentalLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            mainActivityViewModel.setStatusBarHeight(
                insets.systemWindowInsetTop.pixelsToDp(
                    resources
                )
            )
            mainActivityViewModel.setNavBarHeight(
                insets.systemWindowInsetBottom.pixelsToDp(
                    resources
                )
            )
            insets.consumeSystemWindowInsets()
        }
        setStatusBarColor()
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
//                                    builder = {
//                                        anim {
//                                            enter = android.R.anim.slide_out_right
//                                            exit = android.R.anim.fade_out
//                                            popEnter = android.R.anim.slide_out_right
//                                            popExit = android.R.anim.fade_out
//                                        }
//                                    }
                                )
                            },
                            addNewRecipe = {
                                navController.navigate("add_recipe",
//                                    builder = {
//                                        anim {
//                                            enter = android.R.anim.slide_out_right
//                                            exit = android.R.anim.fade_out
//                                            popEnter = android.R.anim.slide_out_right
//                                            popExit = android.R.anim.fade_out
//                                        }
//                                    }
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
                            isInPiP = isInPiP.value
                        )
                    }
                    composable("add_recipe") {
                        mainActivityViewModel.setCanGoToPiP(false)
                        AddNewRecipePage(steps = dummySteps, saveRecipe = { recipe, steps ->
                            runBlocking {
                                val idOfRecipe = db.recipeDao().insertRecipe(recipe)
                                db.stepDao()
                                    .insertAll(steps.map { it.copy(recipeId = idOfRecipe.toInt()) })

                            }
                            navController.navigate("list",
//                                builder = {
//                                    anim {
//                                        enter = android.R.anim.slide_out_right
//                                        exit = android.R.anim.fade_out
//                                        popEnter = android.R.anim.slide_out_right
//                                        popExit = android.R.anim.fade_out
//                                    }
//                                }
                            )
                        })
                    }
                }
            }
        }
    }

    @Composable
    fun PiPAwareAppBar(isInPiP: Boolean) {
        if (!isInPiP) {
            val topPaddingInDp = mainActivityViewModel.statusBarHeight.observeAsState(0.dp)

            Column {
                Surface(
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().height(topPaddingInDp.value)
                        .background(colorResource(id = R.color.navigationBar)),
                ) {}
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    elevation = 0.dp,
                    backgroundColor = colorResource(id = R.color.navigationBar),
                    contentColor = colorResource(id = R.color.textPrimary),
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setLightModeSystemBars()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setDarkModeSystemBars()
            }
        }
    }

    private fun setLightModeSystemBars(view: View = window.decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            view.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                view.systemUiVisibility = flags
            }
        }
    }

    private fun setDarkModeSystemBars(view: View = window.decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            view.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                view.systemUiVisibility = flags
            }
        }
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
