package com.wodlog.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
        composeRule.onNodeWithText("캘린더").performClick()

        composeRule.onNodeWithTag("screen-calendar").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_showsCompareScreen() {
        composeRule.onNodeWithText("비교").performClick()

        composeRule.onNodeWithTag("screen-compare").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_showsSettingsScreen() {
        composeRule.onNodeWithText("설정").performClick()

        composeRule.onNodeWithTag("screen-settings").assertIsDisplayed()
    }
}
