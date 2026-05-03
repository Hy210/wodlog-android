package com.wodlog.app.presentation.resultedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.presentation.theme.WodlogTheme
import com.wodlog.app.util.ValidationError
import org.junit.Rule
import org.junit.Test

class ResultEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun resultEditScreen_displaysCoreInputsAndSaveButton() {
        composeRule.setContent {
            WodlogTheme {
                TestableResultEditScreen()
            }
        }

        composeRule.onNodeWithTag("screen-result-edit").assertIsDisplayed()
        composeRule.onNodeWithTag("input-result-score-type").assertIsDisplayed()
        composeRule.onNodeWithTag("input-result-rpe").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("action-save-result").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun validationErrorState_isDisplayed() {
        composeRule.setContent {
            WodlogTheme {
                TestableResultEditScreen(
                    initialState = ResultEditUiState(
                        wodId = 1L,
                        validationErrors = listOf(ValidationError.RESULT_RPE_OUT_OF_RANGE)
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-result-validation-errors").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun scoreAndRpeInput_acceptsChanges() {
        composeRule.setContent {
            WodlogTheme {
                TestableResultEditScreen()
            }
        }

        composeRule.onNodeWithTag("input-result-score-type-TIME").performClick()
        composeRule.onNodeWithTag("input-result-rpe").performScrollTo().performTextInput("8")

        composeRule.onNodeWithTag("input-result-score-type-TIME").assertIsDisplayed()
        composeRule.onNodeWithTag("input-result-rpe").assertIsDisplayed()
    }
}

@Composable
private fun TestableResultEditScreen(
    initialState: ResultEditUiState = ResultEditUiState(wodId = 1L)
) {
    var state by remember { mutableStateOf(initialState) }

    ResultEditScreen(
        state = state,
        onScoreTypeChange = { state = state.copy(scoreType = it, validationErrors = emptyList()) },
        onTimeSecondsChange = { state = state.copy(timeSecondsInput = it, validationErrors = emptyList()) },
        onRoundsChange = { state = state.copy(roundsInput = it, validationErrors = emptyList()) },
        onRepsChange = { state = state.copy(repsInput = it, validationErrors = emptyList()) },
        onTotalRepsChange = { state = state.copy(totalRepsInput = it, validationErrors = emptyList()) },
        onLoadChange = { state = state.copy(loadInput = it, validationErrors = emptyList()) },
        onDistanceChange = { state = state.copy(distanceInput = it, validationErrors = emptyList()) },
        onCaloriesChange = { state = state.copy(caloriesInput = it, validationErrors = emptyList()) },
        onRxStatusChange = { state = state.copy(rxStatus = it, validationErrors = emptyList()) },
        onRpeChange = { state = state.copy(rpeInput = it, validationErrors = emptyList()) },
        onConditionChange = { state = state.copy(condition = it, validationErrors = emptyList()) },
        onMemoChange = { state = state.copy(memoInput = it, validationErrors = emptyList()) },
        onSaveClick = {
            state = if (state.scoreType == null) {
                state.copy(validationErrors = listOf(ValidationError.SCORE_TYPE_REQUIRED))
            } else {
                state.copy(
                    validationErrors = emptyList(),
                    message = "Result saved",
                    savedResultId = 1L,
                    rxStatus = RxStatus.RX,
                    condition = Condition.GOOD
                )
            }
        }
    )
}
