package com.omelan.cofi.pages

import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Providers
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.AmbientPiPState
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RecipeEditTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalLayout
    @Test
    fun testAddingNameAndDescription() {
        val expectedRecipe =
            Recipe(id = 0, name = "Name of test recipe", description = "Test test test")
        val expectedStep =
            Step(
                name = "Step 1",
                time = 5000,
                type = StepType.WAIT,
                orderInRecipe = 0,
                recipeId = 0
            )
        composeTestRule.setContent {
            CofiTheme {
                Providers(
                    AmbientPiPState provides false,
                ) {
                    RecipeEdit(
                        saveRecipe = { recipe: Recipe, steps: List<Step> ->
                            assert(
                                expectedRecipe == recipe
                            ) {
                                "Expected: $expectedRecipe \ngot: $recipe"
                            }
                            assert(
                                steps.first() == expectedStep
                            ) {
                                "expected: $expectedStep \ngot: ${steps.first()}"
                            }
                        }
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_edit_name")
            .performTextInput(expectedRecipe.name)
        composeTestRule.onNodeWithTag("recipe_edit_description")
            .performTextInput(expectedRecipe.description)

        composeTestRule.onNodeWithTag("step_type_button_${expectedStep.type.name.toLowerCase()}")
            .performClick()
        val stepNameNode = composeTestRule.onNodeWithTag("step_name")
        stepNameNode.assertExists().performTextClearance()
        stepNameNode.performTextInput(expectedStep.name)

        val stepValueNode = composeTestRule.onNodeWithTag("step_time")
        stepValueNode.assertExists()
        stepValueNode.performTextInput((expectedStep.time / 1000).toString())
        composeTestRule.onNodeWithTag("step_save").performClick()

        composeTestRule.onNodeWithTag("recipe_edit_save").performClick()
    }

    @ExperimentalLayout
    @Test
    fun testEditRecipe() {
        val expectedRecipe = Recipe(id = 3, name = "Name of recipe", description = "Test test test")
        val startingRecipe =
            Recipe(id = 3, name = "of Name test recipe", description = "no no no no")
        val expectedStep =
            Step(
                name = "Step 1",
                time = 5000,
                type = StepType.WAIT,
                orderInRecipe = 0,
                recipeId = 3
            )
        val startingStep =
            Step(
                name = "Step 0",
                time = 2000,
                type = StepType.ADD_COFFEE,
                orderInRecipe = 0,
                recipeId = 3
            )
        composeTestRule.setContent {
            CofiTheme {
                Providers(
                    AmbientPiPState provides false,
                ) {
                    RecipeEdit(
                        saveRecipe = { recipe: Recipe, steps: List<Step> ->
                            assert(
                                expectedRecipe == recipe
                            ) {
                                "Expected recipe do not equal saved recipe"
                            }
                            assert(
                                steps.first() == expectedStep
                            ) {
                                "expected: $expectedStep \ngot: ${steps.first()}"
                            }
                        },
                        isEditing = true,
                        stepsToEdit = listOf(startingStep),
                        recipeToEdit = startingRecipe
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("recipe_edit_name")
            .performTextReplacement(expectedRecipe.name)
        composeTestRule.onNodeWithTag("recipe_edit_description")
            .performTextReplacement(expectedRecipe.description)
        composeTestRule.onNodeWithText(startingStep.name, useUnmergedTree = true).performClick()

        composeTestRule.onNodeWithTag("step_type_button_${expectedStep.type.name.toLowerCase()}")
            .performClick()
        val stepNameNode = composeTestRule.onNodeWithTag("step_name")
        stepNameNode.assertExists().performTextClearance()
        stepNameNode.performTextInput(expectedStep.name)

        val stepValueNode = composeTestRule.onNodeWithTag("step_time")
        stepValueNode.assertExists()
        stepValueNode.performTextReplacement((expectedStep.time / 1000).toString())
        composeTestRule.onNodeWithTag("step_save").performClick()

        composeTestRule.onNodeWithTag("recipe_edit_save").performClick()
    }
}