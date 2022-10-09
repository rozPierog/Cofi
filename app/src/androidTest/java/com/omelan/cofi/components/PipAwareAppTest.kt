package com.omelan.cofi.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalMaterial3WindowSizeClassApi
@OptIn(ExperimentalMaterial3Api::class)
@RunWith(JUnit4::class)
class PipAwareAppTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showAppBar() {
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) { PiPAwareAppBar() }
            }
        }
        composeTestRule.onNodeWithTag("pip_app_bar").assertIsDisplayed()
    }

    @Test
    fun checkPipState() {
        val pipState = mutableStateOf(false)
        composeTestRule.setContent {
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides pipState.value,
                ) { PiPAwareAppBar() }
            }
        }
        composeTestRule.onNodeWithTag("pip_app_bar").assertIsDisplayed()
        pipState.value = true
        composeTestRule.onNodeWithTag("pip_app_bar").assertDoesNotExist()
    }
}
