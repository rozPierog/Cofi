package com.omelan.cofi.components

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.green600
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalAnimationApi
@RunWith(JUnit4::class)
class TimerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @SuppressLint("UnrememberedAnimatable")
    @Test
    fun testOnWait() {
        val step = Step(name = "Test", time = 5000, type = StepType.WAIT, orderInRecipe = 0)
        composeTestRule.setContent {
            CofiTheme {
                Timer(
                    isInPiP = false,
                    currentStep = step,
                    animatedProgressValue = Animatable(0.5f),
                    animatedProgressColor = Animatable(green600),
                )
            }
        }
        composeTestRule.onNodeWithTag("timer_name").assertExists()
        composeTestRule.onNodeWithTag("timer_value").assertDoesNotExist()
        composeTestRule.onNodeWithTag("timer_duration").assertExists()
    }

    @SuppressLint("UnrememberedAnimatable")
    @Test
    fun testOnCoffee() {
        val step = Step(
            name = "Test",
            time = 5000,
            type = StepType.ADD_COFFEE,
            value = 500f,
            orderInRecipe = 0,
        )
        composeTestRule.setContent {
            CofiTheme {
                Timer(
                    isInPiP = false,
                    currentStep = step,
                    animatedProgressValue = Animatable(0.5f),
                    animatedProgressColor = Animatable(green600),
                )
            }
        }
        composeTestRule.onNodeWithTag("timer_name").assertExists()
        composeTestRule.onNodeWithTag("timer_value").assertExists()
        composeTestRule.onNodeWithTag("timer_duration").assertExists()
    }

    @SuppressLint("UnrememberedAnimatable")
    @Test
    fun testOnNull() {
        composeTestRule.setContent {
            CofiTheme {
                Timer(
                    isInPiP = false,
                    currentStep = null,
                    animatedProgressValue = Animatable(0.5f),
                    animatedProgressColor = Animatable(green600),
                )
            }
        }
        composeTestRule.onNodeWithTag("timer_name").assertDoesNotExist()
        composeTestRule.onNodeWithTag("timer_value").assertDoesNotExist()
        composeTestRule.onNodeWithTag("timer_duration").assertDoesNotExist()
    }
}
