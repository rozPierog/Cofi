package com.omelan.cofi.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.R
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.toMillis
import com.omelan.cofi.share.utils.toStringDuration
import com.omelan.cofi.share.utils.toStringShort
import com.omelan.cofi.ui.CofiTheme
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
                            value = 30f,
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
                            value = 30f,
                            time = 5.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 60f,
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
                            value = 0f,
                            time = 0.toMillis(),
                            type = StepType.ADD_COFFEE,
                        ),
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 0f,
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
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
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
                            value = null,
                            time = 5.toMillis(),
                            type = StepType.ADD_COFFEE,
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
    fun checkNoCoffee() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertDoesNotExist()
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
                            value = 0f,
                            time = 300.toMillis(),
                            type = StepType.ADD_COFFEE,
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
    fun checkNullWater() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        }
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun checkNoWater() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_water").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 0f,
                            time = 5.toMillis(),
                            type = StepType.WATER,
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
    fun checkNullTimeWater() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 5f,
                            time = null,
                            type = StepType.WATER,
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
    fun checkNoTimeWater() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_water").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_water),
                            value = 5f,
                            time = 0,
                            type = StepType.WATER,
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
    fun checkNullTimeCoffee() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5f,
                            time = null,
                            type = StepType.ADD_COFFEE,
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
    fun checkNoTimeCoffee() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertDoesNotExist()
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5f,
                            time = 0.toMillis(),
                            type = StepType.ADD_COFFEE,
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
    fun checkTimeWait() {
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time").assertIsDisplayed()
            composeTestRule.onNodeWithTag("recipe_info_coffee").assertIsDisplayed()
        }
        val compactStyle = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                RecipeInfo(
                    compactStyle = compactStyle.value,
                    steps = listOf(
                        Step(
                            name = stringResource(R.string.prepopulate_step_coffee),
                            value = 5f,
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
        checkAsserts()
        compactStyle.value = true
        checkAsserts()
    }

    @Test
    fun sumValueCoffeeAndTime() {
        val value1 = 5f
        val value2 = 12f
        val time1 = 6
        val time2 = 13
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time")
                .assertIsDisplayed()
                .onChildren()
                .filterToOne(hasTestTag("recipe_info_text"))
                .assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
            composeTestRule.onNodeWithTag("recipe_info_coffee")
                .assertIsDisplayed()
                .onChildren()
                .filterToOne(hasTestTag("recipe_info_text"))
                .assertTextEquals("${(value1 + value2).toStringShort()}g")
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
        val value1 = 5f
        val value2 = 6f
        val time1 = 7
        val time2 = 8
        fun checkAsserts() {
            composeTestRule.onNodeWithTag("recipe_info_time")
                .assertIsDisplayed()
                .onChildren()
                .filterToOne(hasTestTag("recipe_info_text"))
                .assertTextEquals((time1 + time2 + time2).toMillis().toStringDuration())
            composeTestRule.onNodeWithTag("recipe_info_water")
                .assertIsDisplayed()
                .onChildren()
                .filterToOne(hasTestTag("recipe_info_text"))
                .assertTextEquals("${(value1 + value2).toStringShort()}g")
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
