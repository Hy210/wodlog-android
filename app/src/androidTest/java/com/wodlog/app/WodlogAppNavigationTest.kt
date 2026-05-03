package com.wodlog.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class WodlogAppNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStartsOnHomeScreen() {
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_showsCalendarScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()

        composeRule.onNodeWithTag("screen-calendar").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_showsCompareScreen() {
        composeRule.onNodeWithTag("nav-compare").performClick()

        composeRule.onNodeWithTag("screen-compare").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_showsSettingsScreen() {
        composeRule.onNodeWithTag("nav-settings").performClick()

        composeRule.onNodeWithTag("screen-settings").assertIsDisplayed()
    }
}
