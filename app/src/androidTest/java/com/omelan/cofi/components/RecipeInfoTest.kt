package com.omelan.cofi.components

import androidx.compose.runtime.mutableStateOf
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

    @Test
    fun showRecipeInfo() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_box").assertIsDisplayed()
    }

    @Test
    fun checkAllParamVisible() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
            composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun checkNullParamVisibility() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun checkNonParamVisibility() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun checkNullCoffee() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNoCoffee() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNullWater() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNoWater() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
    }

    @Test
    fun checkNullTimeWater() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
    }

    @Test
    fun checkNoTimeWater() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
    }

    @Test
    fun checkNullTimeCoffee() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun checkNoTimeCoffee() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun checkTimeWait() {
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        compactStyle.value = true
        composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
    }

    @Test
    fun sumValueCoffeeAndTime() {
        val value1 = 5
        val value2 = 12
        val time1 = 6
        val time2 = 13
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed().onChildren()
                .filterToOne(
                    hasTestTag("recipe_info_text"),
                ).assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed().onChildren()
                .filterToOne(
                    hasTestTag("recipe_info_text"),
                ).assertTextEquals("${value1 + value2}g")
        }

        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun sumValueWaterAndTime() {
        val value1 = 5
        val value2 = 6
        val time1 = 7
        val time2 = 8
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed().onChildren()
                .filterToOne(
                    hasTestTag("recipe_info_text"),
                ).assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
            composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed().onChildren()
                .filterToOne(
                    hasTestTag("recipe_info_text"),
                ).assertTextEquals("${value1 + value2}g")
        }

        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }
}
