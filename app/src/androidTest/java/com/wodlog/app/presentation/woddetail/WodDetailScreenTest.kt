package com.wodlog.app.presentation.woddetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.Instant
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

class WodDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val now = Instant.parse("2026-05-03T00:00:00Z")

    @Test
    fun wodDetailScreen_displaysWodAndMovementDetails() {
        composeRule.setContent {
            WodlogTheme {
                WodDetailScreen(
                    state = WodDetailUiState(
                        wod = Wod(
                            id = 1L,
                            date = LocalDate.of(2026, 5, 3),
                            title = "Fran",
                            type = WodType.FOR_TIME,
                            rawText = "21-15-9",
                            notes = "benchmark",
                            createdAt = now,
                            updatedAt = now
                        ),
                        sections = listOf(
                            WodSection(id = 10L, wodId = 1L, name = "Metcon", orderIndex = 0)
                        ),
                        movements = listOf(
                            Movement(
                                id = 20L,
                                wodId = 1L,
                                sectionId = 10L,
                                name = "Thruster",
                                category = MovementCategory.WEIGHTLIFTING,
                                weightKg = 43.0,
                                reps = 21,
                                orderIndex = 0
                            )
                        )
                    )
                )
            }
        }

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-title").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-date").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-movements").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-result-status").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-report-status").assertIsDisplayed()
    }

    @Test
    fun wodDetailScreen_displaysErrorState() {
        composeRule.setContent {
            WodlogTheme {
                WodDetailScreen(
                    state = WodDetailUiState(errorMessage = "WOD not found")
                )
            }
        }

        composeRule.onNodeWithTag("screen-wod-detail").assertIsDisplayed()
        composeRule.onNodeWithTag("text-wod-detail-error").assertIsDisplayed()
    }
}
