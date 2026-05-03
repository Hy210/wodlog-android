package com.wodlog.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
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
        composeRule.onNodeWithTag("action-calendar-create-wod").performScrollTo().performClick()

        composeRule.onNodeWithTag("screen-wod-edit").assertIsDisplayed()
    }

    @Test
    fun calendarOpenWod_showsWodDetailScreen() {
        val title = "Calendar Detail WOD ${System.nanoTime()}"
        createWodAndOpenDetail(title)
        composeRule.onNodeWithTag("nav-calendar").performClick()
        composeRule.onNodeWithText(title).performScrollTo().performClick()

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        waitForWodDetailTitle()
        composeRule.onNodeWithTag("text-wod-detail-title").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun savingWodFromEdit_showsWodDetailScreen() {
        composeRule.onNodeWithTag("action-create-wod").performClick()
        composeRule.onNodeWithTag("input-wod-title").performTextInput("Detail Test WOD")
        composeRule.onNodeWithTag("input-wod-type-FOR_TIME").performScrollTo().performClick()
        composeRule.onNodeWithTag("action-save-wod").performScrollTo().performClick()

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        waitForWodDetailTitle()
        composeRule.onNodeWithTag("text-wod-detail-title").performScrollTo().assertIsDisplayed()
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
        createWodAndOpenDetail("Result Entry WOD ${System.nanoTime()}")
        composeRule.onNodeWithTag("action-edit-result").performScrollTo().performClick()

        composeRule.onNodeWithTag("screen-result-edit").assertIsDisplayed()
    }

    @Test
    fun savingResultFromWodDetail_returnsToDetailAndShowsResultSummary() {
        composeRule.onNodeWithTag("action-create-wod").performClick()
        composeRule.onNodeWithTag("input-wod-title").performTextInput("Result Flow WOD")
        composeRule.onNodeWithTag("input-wod-type-FOR_TIME").performScrollTo().performClick()
        composeRule.onNodeWithTag("action-save-wod").performScrollTo().performClick()
        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()

        composeRule.onNodeWithTag("action-edit-result").performClick()
        composeRule.onNodeWithTag("screen-result-edit").assertIsDisplayed()
        composeRule.onNodeWithTag("input-result-score-type-TIME").performClick()
        composeRule.onNodeWithTag("input-result-time").performTextInput("300")
        composeRule.onNodeWithTag("input-result-rx-status-RX").performScrollTo().performClick()
        composeRule.onNodeWithTag("input-result-condition-GOOD").performScrollTo().performClick()
        composeRule.onNodeWithTag("input-result-rpe").performScrollTo().performTextInput("8")
        composeRule.onNodeWithTag("action-save-result").performScrollTo().performClick()

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        waitForWodDetailResult()
        composeRule.onNodeWithTag("text-wod-detail-result-status").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun wodDetailPrompt_showsPromptScreen() {
        createWodAndOpenDetail("Prompt WOD ${System.nanoTime()}")
        composeRule.onNodeWithTag("action-open-prompt").performClick()

        composeRule.onNodeWithTag("screen-prompt").assertIsDisplayed()
    }

    @Test
    fun wodDetailReport_showsReportEditScreen() {
        createWodAndOpenDetail("Report WOD ${System.nanoTime()}")
        composeRule.onNodeWithTag("action-open-report").performClick()

        composeRule.onNodeWithTag("screen-report-edit").assertIsDisplayed()
    }

    private fun createWodAndOpenDetail(title: String) {
        composeRule.onNodeWithTag("action-create-wod").performClick()
        composeRule.onNodeWithTag("input-wod-title").performTextInput(title)
        composeRule.onNodeWithTag("input-wod-type-FOR_TIME").performScrollTo().performClick()
        composeRule.onNodeWithTag("action-save-wod").performScrollTo().performClick()
        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        waitForWodDetailTitle()
    }

    private fun waitForWodDetailTitle() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("text-wod-detail-title")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun waitForWodDetailResult() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("text-wod-detail-result-status")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
