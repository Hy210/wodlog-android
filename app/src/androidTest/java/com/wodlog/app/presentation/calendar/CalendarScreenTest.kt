package com.wodlog.app.presentation.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CalendarScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun calendarScreen_displaysMonthWeekdaysAndMonthButtons() {
        composeRule.setContent {
            WodlogTheme {
                TestableCalendarScreen()
            }
        }

        composeRule.onNodeWithTag("screen-calendar").assertIsDisplayed()
        composeRule.onNodeWithTag("calendar-current-month").assertIsDisplayed()
        composeRule.onNodeWithTag("calendar-weekday-Mon").assertIsDisplayed()
        composeRule.onNodeWithTag("calendar-weekday-Sun").assertIsDisplayed()
        composeRule.onNodeWithTag("action-calendar-previous-month").assertIsDisplayed()
        composeRule.onNodeWithTag("action-calendar-next-month").assertIsDisplayed()
    }

    @Test
    fun calendarScreen_marksRecordedDateAndShowsWodItem() {
        composeRule.setContent {
            WodlogTheme {
                TestableCalendarScreen()
            }
        }

        assertTrue(
            composeRule.onAllNodesWithTag(
                testTag = "calendar-day-recorded-2026-05-03",
                useUnmergedTree = true
            )
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
        composeRule.onNodeWithTag("calendar-wod-item-1").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun selectingDate_updatesSelectedDateAndShowsCreateButtonForEmptyDate() {
        composeRule.setContent {
            WodlogTheme {
                TestableCalendarScreen()
            }
        }

        composeRule.onNodeWithTag("calendar-day-2026-05-04").performClick()

        composeRule.onNodeWithTag("calendar-selected-date").assertIsDisplayed()
        composeRule.onNodeWithTag("action-calendar-create-wod").assertIsDisplayed()
    }

    @Test
    fun clickingWodItem_callsOpenWodCallback() {
        var openedWodId: Long? = null

        composeRule.setContent {
            WodlogTheme {
                CalendarScreen(
                    state = sampleCalendarState(),
                    onOpenWodClick = { openedWodId = it }
                )
            }
        }

        composeRule.onNodeWithTag("calendar-wod-item-1").performScrollTo().performClick()

        composeRule.runOnIdle {
            assertEquals(1L, openedWodId)
        }
    }
}

@Composable
private fun TestableCalendarScreen() {
    val wod = Wod(
        id = 1L,
        date = LocalDate.of(2026, 5, 3),
        title = "Fran",
        type = WodType.FOR_TIME,
        createdAt = Instant.parse("2026-05-03T00:00:00Z"),
        updatedAt = Instant.parse("2026-05-03T00:00:00Z")
    )
    var state by remember {
        mutableStateOf(sampleCalendarState(wod))
    }

    CalendarScreen(
        state = state,
        onDateClick = { date ->
            state = state.copy(
                selectedDate = date,
                selectedDateWods = state.monthWods.filter { it.date == date }
            )
        }
    )
}

private fun sampleCalendarState(
    wod: Wod = Wod(
        id = 1L,
        date = LocalDate.of(2026, 5, 3),
        title = "Fran",
        type = WodType.FOR_TIME,
        createdAt = Instant.parse("2026-05-03T00:00:00Z"),
        updatedAt = Instant.parse("2026-05-03T00:00:00Z")
    )
): CalendarUiState = CalendarUiState(
    visibleYear = 2026,
    visibleMonth = 5,
    selectedDate = LocalDate.of(2026, 5, 3),
    monthWods = listOf(wod),
    selectedDateWods = listOf(wod),
    recordedDates = setOf(LocalDate.of(2026, 5, 3))
)
