package com.omelan.cofi

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.share.model.PrepopulateData
import com.omelan.cofi.share.utils.ScreenshotsHelpers
import com.omelan.cofi.wearos.presentation.LocalAmbientModeProvider
import com.omelan.cofi.wearos.presentation.pages.RecipeList
import com.omelan.cofi.wearos.presentation.pages.details.RecipeDetails
import com.omelan.cofi.wearos.presentation.theme.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@RunWith(JUnit4::class)
class ScreenshotCreator {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    private fun saveScreenshot(name: String) {
        val screenShot = composeTestRule.onNode(isRoot()).captureToImage()
        ScreenshotsHelpers.saveBitmap(
            context = context,
            bitmap = screenShot.asAndroidBitmap(),
            format = Bitmap.CompressFormat.PNG,
            displayName = name,
            mimeType = "image/png",
        )
    }

    @Test
    fun recipeListScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            val prepopulateData = PrepopulateData(context)
            CofiTheme {
                RecipeList(
                    recipes = prepopulateData.recipes.toList(),
                )
            }
        }
        saveScreenshot("1_en-US")
        saveScreenshot("4_en-US")
    }

    @Test
    fun recipeDetailsScreenshot() {
        composeTestRule.setContent {
            context = LocalContext.current
            context = LocalContext.current
            val prepopulateData = PrepopulateData(context)
            CofiTheme {
                CompositionLocalProvider(
                    LocalAmbientModeProvider provides null,
                ) {
                    RecipeDetails(
                        recipe = prepopulateData.recipes.first(),
                        steps = prepopulateData.steps,
                        canSwipeToClose = { _ -> },
                        onTimerRunning = { _ -> },
                    )
                }
            }
        }
        saveScreenshot("2_en-US")
        saveScreenshot("5_en-US")
        composeTestRule.onNodeWithTag("start_button").performClick()
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(2000)
        saveScreenshot("3_en-US")
        saveScreenshot("6_en-US")
    }

}
