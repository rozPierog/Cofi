@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.omelan.cofi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.parseColor
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.RecipeViewModel
import com.omelan.cofi.model.StepsViewModel
import com.omelan.cofi.pages.RecipeDetails
import com.omelan.cofi.pages.RecipeEdit
import com.omelan.cofi.pages.RecipeList
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.ScreenshotsHelpers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@RunWith(JUnit4::class)
class ScreenshotCreator {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var context: Context

    @SuppressLint("ComposableNaming")
    @Composable
    private fun setNavigationBarColor(darkMode: Boolean) {
        rememberSystemUiController().setNavigationBarColor(
            color = if (darkMode) Color(parseColor("#121212")) else Color.White,
            darkIcons = darkMode
        )
    }

    private fun saveScreenshot(name: String) {
        val screenShot = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
        ScreenshotsHelpers.saveBitmap(
            context = context,
            bitmap = screenShot,
            format = Bitmap.CompressFormat.PNG,
            displayName = name,
            mimeType = "image/png"
        )
    }

    @Test
    fun recipeListScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            CofiTheme(isDarkMode = false) {
                setNavigationBarColor(false)
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeList(
                        navigateToRecipe = {},
                        addNewRecipe = { },
                        goToSettings = { }
                    )
                }
            }
        }
        saveScreenshot("1_en-US")
    }

    @Test
    fun recipeListScreenshotDark() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(true)
            CofiTheme(isDarkMode = true) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeList(
                        navigateToRecipe = {},
                        addNewRecipe = { },
                        goToSettings = { }
                    )
                }
            }
        }
        saveScreenshot("2_en-US")
    }

    @Test
    fun recipeDetailsScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(false)
            val stepsViewModel: StepsViewModel = viewModel()
            val recipeViewModel: RecipeViewModel = viewModel()

            CofiTheme(isDarkMode = false) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        recipeId = 1,
                        recipeViewModel = recipeViewModel,
                        stepsViewModel = stepsViewModel
                    )
                }
            }
        }
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithTag("recipe_start").performClick()
        composeTestRule.mainClock.advanceTimeBy(4500)
        composeTestRule.mainClock.advanceTimeBy(5000)
        saveScreenshot("3_en-US")
    }

    @Test
    fun recipeDetailsScreenshotDark() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(true)
            val stepsViewModel: StepsViewModel = viewModel()
            val recipeViewModel: RecipeViewModel = viewModel()

            CofiTheme(isDarkMode = true) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        recipeId = 3,
                        recipeViewModel = recipeViewModel,
                        stepsViewModel = stepsViewModel,
                    )
                }
            }
        }
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithTag("recipe_start").performClick()
        composeTestRule.mainClock.advanceTimeBy(8000)
        composeTestRule.mainClock.advanceTimeBy(10000)
        saveScreenshot("4_en-US")
    }

    @Test
    fun recipeEditScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(false)
            val recipeViewModel: RecipeViewModel = viewModel()
            val stepsViewModel: StepsViewModel = viewModel()
            val recipe by recipeViewModel.getRecipe(1)
                .observeAsState(Recipe(name = "", description = ""))
            val steps by stepsViewModel.getAllStepsForRecipe(1)
                .observeAsState(listOf())
            CofiTheme(isDarkMode = false) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeEdit(
                        saveRecipe = { _, _ -> },
                        recipeToEdit = recipe,
                        stepsToEdit = steps,
                    )
                }
            }
        }
        saveScreenshot("5_en-US")
    }

    @Test
    fun recipeEditScreenshotDark() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(true)
            val recipeViewModel: RecipeViewModel = viewModel()
            val stepsViewModel: StepsViewModel = viewModel()
            val recipe by recipeViewModel.getRecipe(1)
                .observeAsState(Recipe(name = "", description = ""))
            val steps by stepsViewModel.getAllStepsForRecipe(1)
                .observeAsState(listOf())
            CofiTheme(isDarkMode = true) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeEdit(
                        saveRecipe = { _, _ -> },
                        recipeToEdit = recipe,
                        stepsToEdit = steps,
                    )
                }
            }
        }
        saveScreenshot("6_en-US")
    }
}