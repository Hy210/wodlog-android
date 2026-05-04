package com.wodlog.app.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        CalendarHeader(
            state = state,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )

        WodLogCard {
            WeekdayHeader()
            CalendarGrid(
                state = state,
                onDateClick = onDateClick
            )
        }

        WodLogSectionHeader(
            title = "선택한 날짜의 기록",
            description = formatSelectedDate(state.selectedDate),
            action = {
                WodLogStatusChip(
                    text = if (state.selectedDateWods.isEmpty()) "기록 없음" else "${state.selectedDateWods.size}개",
                    tone = if (state.selectedDateWods.isEmpty()) {
                        WodLogStatusChipTone.Neutral
                    } else {
                        WodLogStatusChipTone.Success
                    }
                )
            }
        )
        Text(
            text = "선택한 날짜: ${state.selectedDate}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("calendar-selected-date")
        )

        state.errorMessage?.let { message ->
            WodLogCard {
                Text(
                    text = "캘린더 기록을 불러오지 못했습니다. $message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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
    WodLogCard(
        title = "월간 기록",
        subtitle = "기록이 있는 날짜를 바로 확인하세요",
        outlined = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WodLogSecondaryButton(
                text = "이전",
                onClick = onPreviousMonthClick,
                modifier = Modifier.testTag("action-calendar-previous-month")
            )
            Text(
                text = "%04d.%02d".format(state.visibleYear, state.visibleMonth),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("calendar-current-month")
            )
            WodLogSecondaryButton(
                text = "다음",
                onClick = onNextMonthClick,
                modifier = Modifier.testTag("action-calendar-next-month")
            )
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(
            "Mon" to "월",
            "Tue" to "화",
            "Wed" to "수",
            "Thu" to "목",
            "Fri" to "금",
            "Sat" to "토",
            "Sun" to "일"
        ).forEach { (tag, label) ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .testTag("calendar-weekday-$tag")
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
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
    val background = when {
        isSelected -> colorScheme.primaryContainer
        isRecorded -> colorScheme.surfaceContainerHigh
        else -> Color.Transparent
    }
    val borderColor = when {
        isSelected -> colorScheme.primary
        isRecorded -> colorScheme.secondary
        else -> colorScheme.outlineVariant
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
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
            fontWeight = if (isSelected || isRecorded) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
        )
        if (isRecorded) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) colorScheme.primary else colorScheme.secondary)
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (wods.isEmpty()) {
            WodLogCard {
                WodLogEmptyState(
                    title = "이 날짜에는 기록이 없습니다",
                    description = "WOD를 추가해 날짜별 기록을 남겨보세요.",
                    action = {
                        WodLogPrimaryButton(
                            text = "WOD 추가",
                            onClick = onCreateWodClick,
                            modifier = Modifier.testTag("action-calendar-create-wod")
                        )
                    }
                )
            }
        } else {
            wods.forEach { wod ->
                WodLogCard(
                    title = wod.title,
                    subtitle = "${wod.date} · ${wod.type.displayName()}",
                    actions = {
                        WodLogStatusChip(
                            text = wod.type.displayName(),
                            tone = WodLogStatusChipTone.Primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenWodClick(wod.id) }
                        .testTag("calendar-wod-item-${wod.id}")
                ) {
                    Text(
                        text = wod.rawText?.takeIf { it.isNotBlank() } ?: "상세 보기로 기록 내용을 확인하세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            WodLogPrimaryButton(
                text = "WOD 추가",
                onClick = onCreateWodClick,
                modifier = Modifier.testTag("action-calendar-create-wod")
            )
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

private fun formatSelectedDate(date: LocalDate): String =
    "%04d년 %02d월 %02d일".format(date.year, date.monthValue, date.dayOfMonth)

private fun WodType.displayName(): String = when (this) {
    WodType.FOR_TIME -> "For Time"
    WodType.AMRAP -> "AMRAP"
    WodType.EMOM -> "EMOM"
    WodType.RFT -> "RFT"
    WodType.STRENGTH -> "Strength"
    WodType.SKILL -> "Skill"
    WodType.INTERVAL -> "Interval"
    WodType.OTHER -> "Other"
}
