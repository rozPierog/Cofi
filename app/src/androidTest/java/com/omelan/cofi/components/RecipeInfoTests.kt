package com.omelan.cofi.components

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.toMillis
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RecipeInfoTests {
    @get:Rule
    val composeTestRule = createComposeRule()
    // createComposeRule() if you don't need access to the activityTestRule

    @Test
    fun showRecipeInfo() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 30,
                            time = 5.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_box").assertIsDisplayed()
    }

    @Test
    fun checkAllParamVisible() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 30,
                            time = 5.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 60,
                            time = 5.toMillis(),
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNullParamVisibility() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = null,
                            time = null,
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = null,
                            time = null,
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
    }

    @Test
    fun checkNonParamVisibility() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 0,
                            time = 0.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 0,
                            time = 0.toMillis(),
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
    }

    @Test
    fun checkNullCoffee() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = null,
                            time = 5.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNoCoffee() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 0,
                            time = 300.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNullWater() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = null,
                            time = 5.toMillis(),
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNoWater() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 0,
                            time = 5.toMillis(),
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNullTimeWater() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 5,
                            time = null,
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
    }

    @Test
    fun checkNullTime() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 5,
                            time = 0,
                            type = StepType.WATER,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
    }

    @Test
    fun checkNullTimeCoffee() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5,
                            time = null,
                            type = StepType.ADD_COFFEE,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun checkNoTimeCoffee() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5,
                            time = 0.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun checkTimeWait() {
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5,
                            time = 0.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_wait),
                            time = 35.toMillis(),
                            type = StepType.WAIT,
                        ),
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }
}