package com.omelan.cofi

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.omelan.cofi.utils.ScreenshotsHelpers
import com.omelan.cofi.wearos.presentation.pages.RecipeList
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
        val screenShot = composeTestRule.onRoot().captureToImage()
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
            CofiTheme {
                RecipeList(
                   goToDetails = {},
                )
            }
        }
        saveScreenshot("1_en-US")
    }

}
