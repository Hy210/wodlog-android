package com.wodlog.app.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.wodlog.app.presentation.theme.WodlogTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsScreen_displaysRequiredSections() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithTag("screen-settings").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-section-profile").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-section-app-info").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-section-data").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings-section-license").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("settings-section-danger").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysProfileLifestyleAndAppInfo() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(appVersion = "0.1.0-test")
            }
        }

        composeRule.onNodeWithTag("action-open-profile").assertIsDisplayed()
        composeRule.onNodeWithTag("action-open-lifestyle").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-app-version").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-github-placeholder").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysExportAndKeepsImportResetDisabled() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithTag("action-export-json")
            .performScrollTo()
            .assertIsEnabled()
        composeRule.onNodeWithTag("action-import-json-placeholder")
            .performScrollTo()
            .assertIsNotEnabled()
        composeRule.onNodeWithTag("action-reset-data-placeholder")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun exportButton_callsCallback() {
        var clickCount = 0
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(onExportJsonClick = { clickCount += 1 })
            }
        }

        composeRule.onNodeWithTag("action-export-json").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun settingsScreen_displaysExportMessages() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(
                    exportState = SettingsExportState(
                        message = "JSON 내보내기를 완료했습니다.",
                        errorMessage = "JSON 내보내기에 실패했습니다."
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-settings-export-message")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("text-settings-export-error")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun licenseButton_callsCallback() {
        var clickCount = 0
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(onLicenseClick = { clickCount += 1 })
            }
        }

        composeRule.onNodeWithTag("action-open-license").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun licenseScreen_displaysMitLicense() {
        composeRule.setContent {
            WodlogTheme {
                LicenseScreen()
            }
        }

        composeRule.onNodeWithTag("screen-license").assertIsDisplayed()
    }
}
