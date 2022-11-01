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

//    private fun checkListItem(listItemTag: String, switchTag: String) {
//        val listItem = composeTestRule.onNodeWithTag(listItemTag)
//        val switch = composeTestRule.onNodeWithTag(switchTag)
//
//        listItem.assertIsDisplayed()
//        val currentSwitchState =
//            switch.fetchSemanticsNode().config[SemanticsProperties.ToggleableState]
//        val isListItemDisabled =
//            listItem.fetchSemanticsNode().config.contains(SemanticsProperties.Disabled)
//        if (isListItemDisabled) {
//            switch.assertIsOff()
//            listItem.assertIsNotEnabled()
//            return
//        }
//        if (currentSwitchState == ToggleableState.Off) {
//            switch.performClick()
//        }
//        switch.assertIsOn()
//        listItem.performClick()
//        listItem.printToLog("LISTITEM")
//        switch.assertIsOff().performClick().assertIsOn()
//    }
//
//    @Test
//    fun showTimerSettings() {
//        renderTimerSettings()
//        composeTestRule.onNodeWithTag("settings_timer_list_item_pip").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("settings_timer_list_item_sound").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("settings_timer_list_item_vibration").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("settings_timer_list_item_weight").assertIsDisplayed()
//    }
//
//    @Test
//    fun checkPipSettings() {
//        renderTimerSettings()
//        checkListItem("settings_timer_list_item_pip", "settings_timer_switch_pip")
//    }
//
//    @Test
//    fun checkSoundSettings() {
//        renderTimerSettings()
//        checkListItem("settings_timer_list_item_sound", "settings_timer_switch_sound")
//    }
//
//    @Test
//    fun checkVibrationSettings() {
//        renderTimerSettings()
//        checkListItem("settings_timer_list_item_vibration", "settings_timer_switch_vibration")
//    }

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
