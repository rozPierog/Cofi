package com.omelan.cofi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.parseColor
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.omelan.cofi.pages.RecipeEdit
import com.omelan.cofi.pages.details.RecipeDetails
import com.omelan.cofi.pages.list.RecipeList
import com.omelan.cofi.share.model.PrepopulateData
import com.omelan.cofi.share.utils.ScreenshotsHelpers
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@RunWith(JUnit4::class)
class ScreenshotCreator {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @SuppressLint("ComposableNaming")
    @Composable
    private fun setNavigationBarColor(darkMode: Boolean) {
        rememberSystemUiController().setNavigationBarColor(
            color = if (darkMode) Color(parseColor("#121212")) else Color.White,
            darkIcons = darkMode,
        )
    }

    private fun saveScreenshot(name: String) {
        val screenShot =
            composeTestRule.onAllNodes(isRoot(), false).onFirst().captureToImage().asAndroidBitmap()
        ScreenshotsHelpers.saveBitmap(
            context = context,
            bitmap = screenShot,
            format = Bitmap.CompressFormat.PNG,
            displayName = name,
            mimeType = "image/png",
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
                        goToSettings = { },
                    )
                }
            }
        }
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(45000)
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
                        goToSettings = { },
                    )
                }
            }
        }
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(45000)
        saveScreenshot("2_en-US")
    }

    @Test
    fun recipeDetailsScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(false)
            val data = PrepopulateData(context)
            val recipe = data.recipes.find { it.id == 1 } ?: throw Exception("No such recipe")
            val steps = data.steps.filter { it.recipeId == recipe.id }

            CofiTheme(isDarkMode = false) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(recipe, steps)
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
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            context = LocalContext.current
            setNavigationBarColor(true)
            val data = PrepopulateData(context)
            val recipe = data.recipes.find { it.id == 3 } ?: throw Exception("No such recipe")
            val steps = data.steps.filter { it.recipeId == recipe.id }
            CofiTheme(isDarkMode = true) {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        recipe = recipe,
                        steps = steps,
                    )
                }
            }
        }
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
            val data = PrepopulateData(context)
            val recipe = data.recipes[1]
            val steps = data.steps.filter { it.recipeId == recipe.id }
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
            val data = PrepopulateData(context)
            val recipe = data.recipes[1]
            val steps = data.steps.filter { it.recipeId == recipe.id }
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
