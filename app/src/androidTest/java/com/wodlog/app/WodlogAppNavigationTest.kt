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

    @Test
    fun homeCreateWod_showsWodEditScreen() {
        composeRule.onNodeWithTag("action-create-wod").performClick()

        composeRule.onNodeWithTag("screen-wod-edit").assertIsDisplayed()
        composeRule.onNodeWithTag("input-wod-title").assertIsDisplayed()
    }

    @Test
    fun calendarCreateWod_showsWodEditScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithTag("action-calendar-create-wod").performClick()

        composeRule.onNodeWithTag("screen-wod-edit").assertIsDisplayed()
    }

    @Test
    fun calendarOpenWod_showsWodDetailScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithTag("action-calendar-open-wod").performClick()

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
    }

    @Test
    fun settingsProfile_showsProfileScreen() {
        composeRule.onNodeWithTag("nav-settings").performClick()
        composeRule.onNodeWithTag("action-open-profile").performClick()

        composeRule.onNodeWithTag("screen-profile").assertIsDisplayed()
        composeRule.onNodeWithTag("input-profile-height").assertIsDisplayed()
    }

    @Test
    fun settingsLifestyle_showsLifestyleScreen() {
        composeRule.onNodeWithTag("nav-settings").performClick()
        composeRule.onNodeWithTag("action-open-lifestyle").performClick()

        composeRule.onNodeWithTag("screen-lifestyle").assertIsDisplayed()
    }

    @Test
    fun wodDetailEditResult_showsResultEditScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithTag("action-calendar-open-wod").performClick()
        composeRule.onNodeWithTag("action-edit-result").performClick()

        composeRule.onNodeWithTag("screen-result-edit").assertIsDisplayed()
    }

    @Test
    fun wodDetailPrompt_showsPromptScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithTag("action-calendar-open-wod").performClick()
        composeRule.onNodeWithTag("action-open-prompt").performClick()

        composeRule.onNodeWithTag("screen-prompt").assertIsDisplayed()
    }

    @Test
    fun wodDetailReport_showsReportEditScreen() {
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithTag("action-calendar-open-wod").performClick()
        composeRule.onNodeWithTag("action-open-report").performClick()

        composeRule.onNodeWithTag("screen-report-edit").assertIsDisplayed()
    }
}
