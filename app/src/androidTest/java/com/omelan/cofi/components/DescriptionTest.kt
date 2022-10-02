package com.omelan.cofi.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
        val testDescription = "Test description"
        composeTestRule.setContent {
            CofiTheme {
                Description(descriptionText = testDescription)
            }
        }
        composeTestRule.onNodeWithText(testDescription).assertIsDisplayed()
    }

    @Test
    fun expandingDescription() {
        val testDescription =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
                "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat " +
                "cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum."
        composeTestRule.setContent {
            CofiTheme {
                Description(descriptionText = testDescription)
            }
        }
        val descriptionNode = composeTestRule.onNodeWithText(testDescription)
        descriptionNode.assertIsDisplayed().assertIsToggleable()
        descriptionNode.assertIsOff()
        descriptionNode.performClick().assertIsOn()
    }
}
