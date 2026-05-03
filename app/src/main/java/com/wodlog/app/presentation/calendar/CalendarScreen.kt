package com.wodlog.app.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.Wod
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel,
    onCreateWod: () -> Unit = {},
    onOpenWod: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    CalendarScreen(
        state = state,
        onPreviousMonthClick = viewModel::goToPreviousMonth,
        onNextMonthClick = viewModel::goToNextMonth,
        onDateClick = viewModel::selectDate,
        onCreateWodClick = onCreateWod,
        onOpenWodClick = onOpenWod
    )
}

@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onDateClick: (LocalDate) -> Unit = {},
    onCreateWodClick: () -> Unit = {},
    onOpenWodClick: (Long) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-calendar")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarHeader(
            state = state,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )
        WeekdayHeader()
        CalendarGrid(
            state = state,
            onDateClick = onDateClick
        )
        Text(
            text = "Selected: ${state.selectedDate}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("calendar-selected-date")
        )

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        SelectedDateWods(
            wods = state.selectedDateWods,
            onCreateWodClick = onCreateWodClick,
            onOpenWodClick = onOpenWodClick
        )
    }
}

@Composable
private fun CalendarHeader(
    state: CalendarUiState,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onPreviousMonthClick,
            modifier = Modifier.testTag("action-calendar-previous-month")
        ) {
            Text("Prev")
        }
        Text(
            text = "%04d-%02d".format(state.visibleYear, state.visibleMonth),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.testTag("calendar-current-month")
        )
        OutlinedButton(
            onClick = onNextMonthClick,
            modifier = Modifier.testTag("action-calendar-next-month")
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .testTag("calendar-weekday-$day")
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    state: CalendarUiState,
    onDateClick: (LocalDate) -> Unit
) {
    val weeks = calendarWeeks(state.visibleYear, state.visibleMonth)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { date ->
                    if (date == null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        CalendarDayCell(
                            date = date,
                            isSelected = date == state.selectedDate,
                            isRecorded = state.recordedDates.contains(date),
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    isRecorded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val background = if (isSelected) colorScheme.primaryContainer else Color.Transparent
    val borderColor = if (isRecorded) colorScheme.primary else colorScheme.outlineVariant

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .border(1.dp, borderColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .testTag("calendar-day-$date"),
        contentAlignment = Alignment.Center
    ) {
        if (isRecorded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("calendar-day-recorded-$date")
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isRecorded) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary)
            )
        }
    }
}

@Composable
private fun SelectedDateWods(
    wods: List<Wod>,
    onCreateWodClick: () -> Unit,
    onOpenWodClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.testTag("calendar-wod-list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (wods.isEmpty()) {
            Text(
                text = "No WOD recorded for this date.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            wods.forEach { wod ->
                OutlinedButton(
                    onClick = { onOpenWodClick(wod.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calendar-wod-item-${wod.id}")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = wod.title,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.clickable { onOpenWodClick(wod.id) }
                        )
                        Text(
                            text = wod.type.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        Button(
            onClick = onCreateWodClick,
            modifier = Modifier.testTag("action-calendar-create-wod")
        ) {
            Text("Create WOD")
        }
    }
}

private fun calendarWeeks(year: Int, month: Int): List<List<LocalDate?>> {
    val yearMonth = YearMonth.of(year, month)
    val firstDay = yearMonth.atDay(1)
    val leadingBlanks = firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value
    val cells = MutableList<LocalDate?>(leadingBlanks) { null }

    for (day in 1..yearMonth.lengthOfMonth()) {
        cells += yearMonth.atDay(day)
    }
    while (cells.size % 7 != 0) {
        cells += null
    }

    return cells.chunked(7)
}
