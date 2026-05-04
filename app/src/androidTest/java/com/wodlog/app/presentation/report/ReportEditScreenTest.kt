package com.wodlog.app.presentation.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.Instant
import org.junit.Rule
import org.junit.Test

class ReportEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun reportEditScreen_displaysInputAndSaveButton() {
        composeRule.setContent {
            WodlogTheme {
                TestableReportEditScreen()
            }
        }

        composeRule.onNodeWithTag("screen-report-edit").assertIsDisplayed()
        composeRule.onNodeWithTag("input-report-answer").assertIsDisplayed()
        composeRule.onNodeWithTag("action-save-report").assertIsDisplayed()
    }

    @Test
    fun blankAnswerValidationError_isDisplayed() {
        composeRule.setContent {
            WodlogTheme {
                TestableReportEditScreen()
            }
        }

        composeRule.onNodeWithTag("action-save-report").performClick()

        composeRule.onNodeWithTag("text-report-errors").assertIsDisplayed()
    }

    @Test
    fun existingReportList_isDisplayedAndItemClickFillsAnswerInput() {
        composeRule.setContent {
            WodlogTheme {
                TestableReportEditScreen(
                    initialState = ReportEditUiState(
                        wodId = 7L,
                        reports = listOf(report(id = 3L, text = "Saved GPT answer"))
                    )
                )
            }
        }

        composeRule.onNodeWithTag("report-list").assertIsDisplayed()
        composeRule.onNodeWithTag("report-item-3").assertIsDisplayed()
        composeRule.onNodeWithTag("report-item-3").performClick()

        composeRule.onNodeWithTag("input-report-answer").assertTextContains("Saved GPT answer")
    }

    @Test
    fun savingNewReport_displaysMessage() {
        composeRule.setContent {
            WodlogTheme {
                TestableReportEditScreen()
            }
        }

        composeRule.onNodeWithTag("input-report-answer").performTextInput("New pasted answer")
        composeRule.onNodeWithTag("action-save-report").performClick()

        composeRule.onNodeWithTag("text-report-message").assertIsDisplayed()
        composeRule.onNodeWithTag("report-item-1").assertIsDisplayed()
    }

    @Test
    fun selectedReport_enablesDeleteButton() {
        composeRule.setContent {
            WodlogTheme {
                TestableReportEditScreen(
                    initialState = ReportEditUiState(
                        wodId = 7L,
                        reports = listOf(report(id = 3L, text = "Saved GPT answer"))
                    )
                )
            }
        }

        composeRule.onNodeWithTag("report-item-3").performClick()

        composeRule.onNodeWithTag("action-delete-report").assertIsEnabled()
    }

    @Composable
    private fun TestableReportEditScreen(
        initialState: ReportEditUiState = ReportEditUiState(wodId = 7L)
    ) {
        var state by remember { mutableStateOf(initialState) }

        ReportEditScreen(
            state = state,
            onAnswerChange = {
                state = state.copy(
                    answerInput = it,
                    validationErrors = emptyList(),
                    message = null
                )
            },
            onSelectReport = { reportId ->
                val selectedReport = state.reports.first { it.id == reportId }
                state = state.copy(
                    selectedReportId = selectedReport.id,
                    answerInput = selectedReport.reportText
                )
            },
            onNewReportClick = {
                state = state.copy(
                    selectedReportId = null,
                    answerInput = "",
                    message = null,
                    validationErrors = emptyList()
                )
            },
            onSaveClick = {
                state = if (state.answerInput.isBlank()) {
                    state.copy(validationErrors = listOf(ReportEditViewModel.ERROR_BLANK_ANSWER))
                } else {
                    val reportId = state.selectedReportId ?: 1L
                    val savedReport = report(
                        id = reportId,
                        text = state.answerInput
                    )
                    state.copy(
                        reports = state.reports.filterNot { it.id == reportId } + savedReport,
                        selectedReportId = reportId,
                        answerInput = savedReport.reportText,
                        validationErrors = emptyList(),
                        message = "Report saved"
                    )
                }
            },
            onDeleteClick = {
                val selectedId = state.selectedReportId
                state = state.copy(
                    reports = state.reports.filterNot { it.id == selectedId },
                    selectedReportId = null,
                    answerInput = "",
                    message = "Report deleted"
                )
            }
        )
    }

    private fun report(
        id: Long,
        text: String
    ): AiReport = AiReport(
        id = id,
        targetWodId = 7L,
        reportText = text,
        createdAt = Instant.parse("2026-05-04T00:00:00Z"),
        updatedAt = Instant.parse("2026-05-04T00:00:00Z")
    )
}
