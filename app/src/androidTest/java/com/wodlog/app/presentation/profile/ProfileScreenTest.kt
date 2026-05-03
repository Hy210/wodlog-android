package com.wodlog.app.presentation.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.wodlog.app.presentation.theme.WodlogTheme
import com.wodlog.app.util.ValidationError
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profileScreen_displaysInputsAndSaveButton() {
        composeRule.setContent {
            WodlogTheme {
                ProfileScreen(
                    state = ProfileUiState(),
                    onHeightChange = {},
                    onWeightChange = {},
                    onStartDateChange = {},
                    onSaveClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("input-profile-height").assertIsDisplayed()
        composeRule.onNodeWithTag("input-profile-weight").assertIsDisplayed()
        composeRule.onNodeWithTag("input-profile-start-date").assertIsDisplayed()
        composeRule.onNodeWithTag("action-save-profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysValidationErrors() {
        composeRule.setContent {
            WodlogTheme {
                ProfileScreen(
                    state = ProfileUiState(
                        validationErrors = listOf(ValidationError.HEIGHT_OUT_OF_RANGE)
                    ),
                    onHeightChange = {},
                    onWeightChange = {},
                    onStartDateChange = {},
                    onSaveClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("text-validation-errors").assertIsDisplayed()
    }
}
