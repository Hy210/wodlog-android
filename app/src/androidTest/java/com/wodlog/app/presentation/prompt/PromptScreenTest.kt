package com.wodlog.app.presentation.prompt

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wodlog.app.presentation.theme.WodlogTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PromptScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun promptScreen_displaysPromptTextAndCopyButton() {
        composeRule.setContent {
            WodlogTheme {
                PromptScreen(
                    state = PromptUiState(
                        promptText = "Prompt body for testing",
                        wodTitle = "Test WOD"
                    ),
                    onCopyClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("screen-prompt").assertIsDisplayed()
        composeRule.onNodeWithTag("text-prompt-wod-title").assertIsDisplayed()
        composeRule.onNodeWithTag("text-prompt-content").assertIsDisplayed()
        composeRule.onNodeWithTag("action-copy-prompt").assertIsDisplayed()
        composeRule.onNodeWithText("Prompt body for testing").assertIsDisplayed()
    }

    @Test
    fun copyButton_isDisabledWhenPromptIsBlank() {
        composeRule.setContent {
            WodlogTheme {
                PromptScreen(
                    state = PromptUiState(promptText = ""),
                    onCopyClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("action-copy-prompt").assertIsNotEnabled()
    }

    @Test
    fun copyButton_callsCallback() {
        var copyCount = 0
        composeRule.setContent {
            WodlogTheme {
                PromptScreen(
                    state = PromptUiState(promptText = "Copy me"),
                    onCopyClick = { copyCount += 1 }
                )
            }
        }

        composeRule.onNodeWithTag("action-copy-prompt").performClick()

        composeRule.runOnIdle {
            assertEquals(1, copyCount)
        }
    }

    @Test
    fun copyMessage_isDisplayed() {
        composeRule.setContent {
            WodlogTheme {
                PromptScreen(
                    state = PromptUiState(
                        promptText = "Copy me",
                        copyMessage = "Prompt copied"
                    ),
                    onCopyClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("text-prompt-copy-message").assertIsDisplayed()
        composeRule.onNodeWithText("Prompt copied").assertIsDisplayed()
    }
}
