package com.omelan.cofi.components

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.R
import com.omelan.cofi.model.Step
import com.omelan.cofi.model.StepType
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.toMillis
import com.omelan.cofi.utils.toStringDuration
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RecipeInfoTest {
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
                    ),
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
                        Step(
                            name = stringResource(R.string.prepopulate_step_swirl),
                            time = 5.toMillis(),
                            type = StepType.OTHER,
                        ),
                    ),
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
                        Step(
                            name = stringResource(R.string.prepopulate_step_swirl),
                            time = null,
                            type = StepType.OTHER,
                        ),
                    ),
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
                        Step(
                            name = stringResource(R.string.prepopulate_step_swirl),
                            time = 0.toMillis(),
                            type = StepType.OTHER,
                        ),
                    ),
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
                    ),
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
                    ),
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
                    ),
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
                    ),
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
                    ),
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
    }

    @Test
    fun checkNoTimeWater() {
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
                    ),
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
                    ),
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
                    ),
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
                    ),
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun sumValueCoffeeAndTime() {
        val value1 = 5
        val value2 = 12
        val time1 = 6
        val time2 = 13
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = value1,
                            time = time1.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = value2,
                            time = time2.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_swirl),
                            time = time2.toMillis(),
                            type = StepType.OTHER,
                        ),
                    ),
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed().onChildren()
            .filterToOne(
                hasTestTag("recipe_info_text"),
            ).assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed().onChildren()
            .filterToOne(
                hasTestTag("recipe_info_text"),
            ).assertTextEquals("${value1 + value2}g")
    }

    @Test
    fun sumValueWaterAndTime() {
        val value1 = 5
        val value2 = 6
        val time1 = 7
        val time2 = 8
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = value1,
                            time = time1.toMillis(),
                            type = StepType.WATER,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = value2,
                            time = time2.toMillis(),
                            type = StepType.WATER,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_swirl),
                            time = time2.toMillis(),
                            type = StepType.OTHER,
                        ),
                    ),
                )
            }
        }
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed().onChildren()
            .filterToOne(
                hasTestTag("recipe_info_text"),
            ).assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed().onChildren()
            .filterToOne(
                hasTestTag("recipe_info_text"),
            ).assertTextEquals("${value1 + value2}g")
    }
}
