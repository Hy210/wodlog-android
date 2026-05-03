package com.wodlog.app.presentation.lifestyle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.wodlog.app.presentation.theme.WodlogTheme
import com.wodlog.app.util.ValidationError
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LifestyleScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun lifestyleScreen_displaysInputsAndSaveButton() {
        composeRule.setContent {
            WodlogTheme {
                LifestyleScreen(state = sampleState())
            }
        }

        composeRule.onNodeWithTag("screen-lifestyle").assertIsDisplayed()
        composeRule.onNodeWithTag("input-lifestyle-week-start").assertIsDisplayed()
        composeRule.onNodeWithTag("input-lifestyle-diet-summary").assertIsDisplayed()
        composeRule.onNodeWithTag("toggle-lifestyle-alcohol").assertIsDisplayed()
        composeRule.onNodeWithTag("toggle-lifestyle-smoking").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("input-lifestyle-sleep-hours").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("action-save-lifestyle").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun lifestyleScreen_whenAlcoholAndSmokingOff_disablesAmountInputs() {
        composeRule.setContent {
            WodlogTheme {
                LifestyleScreen(state = sampleState(hasAlcohol = false, hasSmoking = false))
            }
        }

        composeRule.onNodeWithTag("input-lifestyle-alcohol-amount").assertIsNotEnabled()
        composeRule.onNodeWithTag("input-lifestyle-smoking-amount").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun lifestyleScreen_togglesAndTextInputsCallCallbacks() {
        composeRule.setContent {
            WodlogTheme {
                TestableLifestyleScreen()
            }
        }

        composeRule.onNodeWithTag("toggle-lifestyle-alcohol").performClick()
        composeRule.onNodeWithTag("input-lifestyle-alcohol-amount").performTextInput("2")
        composeRule.onNodeWithTag("toggle-lifestyle-smoking").performScrollTo().performClick()
        composeRule.onNodeWithTag("input-lifestyle-smoking-amount").performScrollTo().performTextInput("0")
        composeRule.onNodeWithTag("input-lifestyle-diet-summary").performScrollTo().performTextInput("balanced")

        composeRule.onNodeWithTag("input-lifestyle-alcohol-amount").assertTextContains("2")
        composeRule.onNodeWithTag("input-lifestyle-smoking-amount").assertTextContains("0")
        composeRule.onNodeWithTag("input-lifestyle-diet-summary").assertTextContains("balanced")
    }

    @Test
    fun lifestyleScreen_displaysValidationError() {
        composeRule.setContent {
            WodlogTheme {
                LifestyleScreen(
                    state = sampleState(
                        validationErrors = listOf(ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY)
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-lifestyle-errors").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun lifestyleScreen_displaysSavedMessage() {
        composeRule.setContent {
            WodlogTheme {
                LifestyleScreen(
                    state = sampleState(
                        message = "Lifestyle log saved",
                        savedLifestyleLogId = 7L
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-lifestyle-message").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun saveButton_callsCallback() {
        var saveCount = 0

        composeRule.setContent {
            WodlogTheme {
                LifestyleScreen(
                    state = sampleState(),
                    onSaveClick = { saveCount += 1 }
                )
            }
        }

        composeRule.onNodeWithTag("action-save-lifestyle").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertEquals(1, saveCount)
        }
    }
}

@Composable
private fun TestableLifestyleScreen() {
    var state by remember { mutableStateOf(sampleState()) }

    LifestyleScreen(
        state = state,
        onDietSummaryChange = { state = state.copy(dietSummaryInput = it) },
        onHasAlcoholChange = { state = state.copy(hasAlcohol = it) },
        onAlcoholAmountChange = { state = state.copy(alcoholAmountPerWeekInput = it) },
        onHasSmokingChange = { state = state.copy(hasSmoking = it) },
        onSmokingAmountChange = { state = state.copy(smokingAmountPerWeekInput = it) }
    )
}

private fun sampleState(
    hasAlcohol: Boolean = true,
    hasSmoking: Boolean = true,
    validationErrors: List<ValidationError> = emptyList(),
    message: String? = null,
    savedLifestyleLogId: Long? = null
): LifestyleUiState = LifestyleUiState(
    weekStartDateInput = "2026-04-27",
    hasAlcohol = hasAlcohol,
    hasSmoking = hasSmoking,
    validationErrors = validationErrors,
    message = message,
    savedLifestyleLogId = savedLifestyleLogId
)
