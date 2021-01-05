package com.omelan.burr

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
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
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

class MainActivityViewModel : ViewModel() {
    private val _pipState = MutableLiveData(false)
    val pipState: LiveData<Boolean> = _pipState

    private val _canGoToPiP = MutableLiveData(false)
    val canGoToPiP: LiveData<Boolean> = _canGoToPiP

    private val _statusBarHeight = MutableLiveData(0)
    val statusBarHeight: LiveData<Int> = _statusBarHeight

    fun setStatusBarHeight(newHeight: Int) {
        _statusBarHeight.value = newHeight
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
            mainActivityViewModel.setStatusBarHeight(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
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
        Column {

            PiPAwareAppBar(isInPiP = isInPiP.value)

            NavHost(navController, startDestination = "list") {
                composable("list") {
                    mainActivityViewModel.setCanGoToPiP(false)
                    RecipeList(
                        navigateToRecipe = { recipeId ->
                            navController.navigate(
                                route = "recipe/${recipeId}",
//                                    builder = NavOptionsBuilder().anim {
//                                        AnimBuilder().apply {
//                                            enter = R.anim.slide_in
//                                            exit = R.anim.fade_out
//                                            popEnter = R.anim.slide_in
//                                            popExit = R.anim.fade_out
//                                        }
//                                    }
                            )
                        },
                        addNewRecipe = {
                            navController.navigate("add_recipe")
                        },
                    )
                }
                composable(
                    "recipe/{recipeId}",
                    arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
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
                        navController.navigate("list")
                    })
                }
            }
        }
    }

    @Composable
    fun PiPAwareAppBar(isInPiP: Boolean) {
        if (!isInPiP) {
            var topPaddingInDp: Dp by remember { mutableStateOf(0.dp) }

            mainActivityViewModel.statusBarHeight.observe(this) {
                topPaddingInDp = (it / (
                        resources?.displayMetrics?.density
                            ?: 1f
                        )).dp
            }
            Column {
                Surface(elevation = 4.dp) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(topPaddingInDp)
                            .background(colorResource(id = R.color.navigationBar)),
                    )
                }
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    elevation = 4.dp,
                    backgroundColor = colorResource(id = R.color.navigationBar),
                    contentColor = colorResource(id = R.color.textPrimary),
                )
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
