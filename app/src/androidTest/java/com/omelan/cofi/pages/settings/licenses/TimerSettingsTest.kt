package com.omelan.cofi.pages.settings.licenses

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.pages.settings.TimerSettings
import com.omelan.cofi.ui.CofiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimerSettingsTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context

    private fun renderTimerSettings() {
        composeTestRule.setContent {
            context = LocalContext.current
            CofiTheme {
                CompositionLocalProvider(
                    LocalPiPState provides false,
                ) {
                    TimerSettings(goBack = { })
                }
            }
        }
    }

    @Test
    fun showTimerSettings() {
        composeTestRule.onNodeWithTag("settings_timer_list_item_pip").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_list_item_sound").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_list_item_vibration").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_list_item_weight").assertIsDisplayed()
    }

    @Test
    fun checkPipSettings() {
        renderTimerSettings()
        composeTestRule.onNodeWithTag("settings_timer_list_item_pip")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_switch_pip")
            .assertIsOn().performClick().assertIsOff()
    }

    @Test
    fun checkSoundSettings() {
        renderTimerSettings()
        composeTestRule.onNodeWithTag("settings_timer_list_item_sound")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_switch_sound")
            .assertIsOn().performClick().assertIsOff()
    }

    @Test
    fun checkVibrationSettings() {
        renderTimerSettings()
        composeTestRule.onNodeWithTag("settings_timer_list_item_vibration")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_timer_switch_vibration")
            .assertIsOn().performClick().assertIsOff()
    }

    @Test
    fun checkWeightSettings() {
        renderTimerSettings()
        composeTestRule.onNodeWithTag("settings_timer_list_item_weight")
            .assertIsDisplayed().performClick()
        composeTestRule.onAllNodesWithTag("settings_timer_list_item_weight_dialog")
            .assertAll(hasClickAction())
        // TODO:Test combine weight
    }
}
