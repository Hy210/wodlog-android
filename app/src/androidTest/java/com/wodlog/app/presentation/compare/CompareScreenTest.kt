package com.wodlog.app.presentation.compare

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.wodlog.app.domain.analysis.AnalysisSummary
import com.wodlog.app.domain.analysis.CategoryShare
import com.wodlog.app.domain.analysis.ComparisonLabel
import com.wodlog.app.domain.analysis.WodComparisonItem
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CompareScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun compareScreen_displaysEmptyState() {
        composeRule.setContent {
            WodlogTheme {
                CompareScreen(
                    state = CompareUiState(isEmpty = true)
                )
            }
        }

        composeRule.onNodeWithTag("screen-compare").assertIsDisplayed()
        composeRule.onNodeWithTag("compare-empty").assertIsDisplayed()
    }

    @Test
    fun compareScreen_displaysSummaryItems() {
        composeRule.setContent {
            WodlogTheme {
                CompareScreen(state = summaryState())
            }
        }

        composeRule.onNodeWithTag("compare-summary-list").assertIsDisplayed()
        composeRule.onNodeWithTag("compare-item-older").assertIsDisplayed()
        composeRule.onNodeWithTag("compare-item-previous").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("compare-item-current").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun compareScreen_displaysOriginalDataMetricsCategoryBreakdownAndNeutralSummary() {
        composeRule.setContent {
            WodlogTheme {
                CompareScreen(state = summaryState())
            }
        }

        composeRule.onNodeWithText("Older WOD raw text").assertIsDisplayed()
        composeRule.onNodeWithText("1. Older section").assertIsDisplayed()
        composeRule.onNodeWithText("1. Older movement - Strength / 30 reps").assertIsDisplayed()
        composeRule.onNodeWithText("Score type: TIME").assertIsDisplayed()
        assertTrue(composeRule.onAllNodesWithText("Total reps").fetchSemanticsNodes().isNotEmpty())
        assertTrue(composeRule.onAllNodesWithText("Load volume").fetchSemanticsNodes().isNotEmpty())
        assertTrue(composeRule.onAllNodesWithText("Distance").fetchSemanticsNodes().isNotEmpty())
        assertTrue(composeRule.onAllNodesWithText("Calories").fetchSemanticsNodes().isNotEmpty())
        composeRule.onNodeWithTag("compare-category-breakdown").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("STRENGTH: 2개 67%").assertIsDisplayed()
        composeRule.onNodeWithTag("compare-neutral-summary").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("정량 지표만 표시합니다.").assertIsDisplayed()
    }

    @Test
    fun refreshButton_callsCallback() {
        var refreshCount = 0

        composeRule.setContent {
            WodlogTheme {
                CompareScreen(
                    state = CompareUiState(isEmpty = true),
                    onRefreshClick = { refreshCount += 1 }
                )
            }
        }

        composeRule.onNodeWithTag("action-refresh-compare").performClick()

        composeRule.runOnIdle {
            assertEquals(1, refreshCount)
        }
    }

    private fun summaryState(): CompareUiState = CompareUiState(
        summary = AnalysisSummary(
            items = listOf(
                comparisonItem(
                    label = ComparisonLabel.Older,
                    wodId = 1L,
                    date = LocalDate.of(2026, 5, 1),
                    title = "Older WOD",
                    reps = 30
                ),
                comparisonItem(
                    label = ComparisonLabel.Previous,
                    wodId = 2L,
                    date = LocalDate.of(2026, 5, 2),
                    title = "Previous WOD",
                    reps = 40
                ),
                comparisonItem(
                    label = ComparisonLabel.Current,
                    wodId = 3L,
                    date = LocalDate.of(2026, 5, 3),
                    title = "Current WOD",
                    reps = 50
                )
            ),
            categoryBreakdown = listOf(
                CategoryShare(
                    category = MovementCategory.STRENGTH,
                    count = 2,
                    ratio = 2.0 / 3.0
                ),
                CategoryShare(
                    category = MovementCategory.CARDIO,
                    count = 1,
                    ratio = 1.0 / 3.0
                )
            ),
            neutralSummary = listOf(
                "최근 3회 기록을 날짜순으로 요약했습니다.",
                "정량 지표만 표시합니다."
            ),
            hasEnoughDataForComparison = true
        ),
        wodCount = 3
    )

    private fun comparisonItem(
        label: ComparisonLabel,
        wodId: Long,
        date: LocalDate,
        title: String,
        reps: Int
    ): WodComparisonItem = WodComparisonItem(
        label = label,
        wodId = wodId,
        date = date,
        title = title,
        wodType = WodType.FOR_TIME,
        rawText = "$title raw text",
        notes = "$title notes",
        sections = listOf(
            WodSection(
                wodId = wodId,
                name = "${title.removeSuffix(" WOD")} section",
                orderIndex = 0
            )
        ),
        movements = listOf(
            Movement(
                wodId = wodId,
                name = "${title.removeSuffix(" WOD")} movement",
                category = MovementCategory.STRENGTH,
                reps = reps,
                orderIndex = 0
            )
        ),
        result = WodResult(
            wodId = wodId,
            scoreType = ScoreType.TIME,
            rxStatus = RxStatus.RX,
            rpe = 7,
            createdAt = java.time.Instant.parse("2026-05-04T00:00:00Z"),
            updatedAt = java.time.Instant.parse("2026-05-04T00:00:00Z")
        ),
        totalReps = reps,
        totalLoadVolume = reps * 20.0,
        totalDistance = 100.0,
        totalCalories = 12.0,
        rxStatus = RxStatus.RX,
        rpe = 7,
        movementCategoryCounts = mapOf(MovementCategory.STRENGTH to 1)
    )
}

