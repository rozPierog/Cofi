package com.omelan.cofi.components

import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.omelan.cofi.R
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StepAddCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    // createComposeRule() if you don't need access to the activityTestRule

    @ExperimentalLayout
    @Test
    fun testRender() {
        // Start the app
        composeTestRule.setContent {
            CofiTheme {
                StepAddCard(save = {}, stepToEdit = null)
            }
        }
        composeTestRule.onNodeWithTag("step_type_button_${StepType.WAIT.name.toLowerCase()}")
            .assertExists()
    }

    @ExperimentalLayout
    @Test
    fun testFillName() {
        composeTestRule.setContent {
            CofiTheme {
                StepAddCard(save = {}, stepToEdit = null)
            }
        }
        composeTestRule.onNodeWithTag("step_name").assertDoesNotExist()
        val expectedLabel = InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.getString(R.string.step_add_name)
        StepType.values().forEach { stepType ->
            composeTestRule.onNodeWithTag(
                "step_type_button_${stepType.name.toLowerCase()}"
            ).assertExists().performClick()
            val nameNode = composeTestRule.onNodeWithTag("step_name")

            nameNode.assertExists()
            val expectedName = InstrumentationRegistry.getInstrumentation()
                .targetContext.resources.getString(stepType.stringRes)

            nameNode.assertTextEquals("$expectedName, $expectedLabel")
//            when (stepType) {
//                StepType.WAIT ->
//                    composeTestRule.onNodeWithTag("step_value").assertDoesNotExist()
//                StepType.ADD_COFFEE, StepType.OTHER, StepType.WATER ->
//                    composeTestRule.onNodeWithTag("step_value").assertExists()
//
//            }
        }
    }
}