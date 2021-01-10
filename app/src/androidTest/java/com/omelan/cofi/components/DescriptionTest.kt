package com.omelan.cofi.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class DescriptionTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    // createComposeRule() if you don't need access to the activityTestRule

    @Test
    fun shortDescriptionTest() {
        // Start the app
        composeTestRule.setContent {
            CofiTheme {
                Description(descriptionText = "Test description")
            }
        }
        composeTestRule.onNodeWithText("Test description").assertIsDisplayed()
    }
}
