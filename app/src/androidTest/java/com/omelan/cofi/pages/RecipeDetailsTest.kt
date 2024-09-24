package com.omelan.cofi.pages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.pages.details.RecipeDetails
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.RecipeIcon
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalAnimationApi
@RunWith(JUnit4::class)
class RecipeDetailsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testRecipe = Recipe(
        id = 0,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed egestas nisi " +
            "vel est malesuada, in tincidunt ligula lacinia. Maecenas sed sem non nisl " +
            "commodo ullamcorper. Donec euismod volutpat magna, nec dapibus augue rutrum et. " +
            "Cras ligula erat, tempus quis nibh vel, sagittis gravida leo. Mauris quis " +
            "leo erat. Aliquam tincidunt sagittis tempor. Sed id finibus urna. Praesent " +
            "at nulla aliquet, molestie magna at, mattis tortor. Aenean eleifend justo ipsum," +
            " sed convallis lectus viverra at. ",
        name = "Test Recipe",
        recipeIcon = RecipeIcon.AeroPress,
        lastFinished = 0L,
    )

    private val testStep = Step(
        id = 0,
        name = "Step without time",
        time = null,
        recipeId = 0,
        orderInRecipe = 0,
        type = StepType.OTHER,
    )

    @ExperimentalComposeUiApi
    @Test
    fun testEmptyDescription() {
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        isInPiP = false,
                        recipe = testRecipe.copy(description = ""),
                        steps = listOf(testStep),
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_description").assertDoesNotExist()
    }

    @ExperimentalComposeUiApi
    @Test
    fun testFilledDescription() {
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        isInPiP = false,
                        recipe = testRecipe,
                        steps = listOf(testStep),
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_description").assertExists()
    }

    @ExperimentalComposeUiApi
    @Test
    fun testStepsWithTime() {
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        isInPiP = false,
                        recipe = testRecipe,
                        steps = listOf(testStep.copy(name = "Step with time", time = 10000)),
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_start")
            .assertIsToggleable()
            .assertIsOff()
            .performClick()
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithTag("recipe_start")
            .assertIsToggleable()
            .assertIsOn()
    }

    @ExperimentalComposeUiApi
    @Test
    fun testStepsWithoutTime() {
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        isInPiP = false,
                        recipe = testRecipe,
                        steps = listOf(testStep),
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_start")
            .assertIsToggleable()
            .assertIsOff()
            .performClick()
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithTag("recipe_start")
            .assertIsToggleable()
            .assertIsOff()
    }

    @ExperimentalComposeUiApi
    @Test
    fun pipTest() {
        var isInPip by mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    RecipeDetails(
                        isInPiP = isInPip,
                        recipe = testRecipe,
                        steps = listOf(testStep),
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_timer").assertExists()
        composeTestRule.onNodeWithTag("recipe_description").assertExists()
        composeTestRule.onNodeWithTag("recipe_step").assertExists()
        composeTestRule.onNodeWithTag("recipe_start")
            .assertIsToggleable()
            .assertIsOff()
//            .performClick()
        isInPip = true
        composeTestRule.onNodeWithTag("recipe_timer").assertExists()
        composeTestRule.onNodeWithTag("recipe_description").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_step").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_start").assertDoesNotExist()
    }
}
