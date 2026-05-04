package com.wodlog.app.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.wodlog.app.domain.backup.BackupImportError
import com.wodlog.app.domain.backup.BackupImportErrorType
import com.wodlog.app.domain.backup.BackupImportPreview
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
    fun settingsScreen_displaysExportImportAndKeepsResetDisabled() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithTag("action-export-json")
            .performScrollTo()
            .assertIsEnabled()
        composeRule.onNodeWithTag("action-import-json")
            .performScrollTo()
            .assertIsEnabled()
        composeRule.onNodeWithTag("action-reset-data-placeholder")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun importButton_callsCallback() {
        var clickCount = 0
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(onImportJsonClick = { clickCount += 1 })
            }
        }

        composeRule.onNodeWithTag("action-import-json").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertEquals(1, clickCount)
        }
    }

    @Test
    fun settingsScreen_displaysValidImportPreview() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(
                    importState = SettingsImportState(
                        message = "가져오기 파일을 확인했습니다.",
                        preview = BackupImportPreview(
                            backup = null,
                            isValid = true,
                            errors = emptyList(),
                            wodCount = 2,
                            movementCount = 4,
                            resultCount = 1,
                            lifestyleLogCount = 1,
                            aiReportCount = 3
                        )
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-settings-import-message")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("settings-import-validity")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("settings-import-wod-count")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("settings-import-report-count")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysInvalidImportPreviewErrors() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(
                    importState = SettingsImportState(
                        message = "가져올 수 없는 백업 파일입니다.",
                        preview = BackupImportPreview(
                            backup = null,
                            isValid = false,
                            errors = listOf(
                                BackupImportError(
                                    type = BackupImportErrorType.INVALID_JSON,
                                    message = "Invalid JSON."
                                )
                            ),
                            wodCount = 0,
                            movementCount = 0,
                            resultCount = 0,
                            lifestyleLogCount = 0,
                            aiReportCount = 0
                        )
                    )
                )
            }
        }

        composeRule.onNodeWithTag("settings-import-validity")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("settings-import-error-0")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysImportCancelAndFailureMessages() {
        composeRule.setContent {
            WodlogTheme {
                SettingsScreen(
                    importState = SettingsImportState(
                        message = "JSON 가져오기를 취소했습니다.",
                        errorMessage = "JSON 파일을 읽거나 검증하지 못했습니다."
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-settings-import-message")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("text-settings-import-error")
            .performScrollTo()
            .assertIsDisplayed()
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
