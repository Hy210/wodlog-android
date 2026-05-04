package com.wodlog.app.presentation.lifestyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogDateField
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField
import com.wodlog.app.util.ValidationError

@Composable
fun LifestyleRoute(
    viewModel: LifestyleViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadCurrentWeek()
    }

    LifestyleScreen(
        state = state,
        onWeekStartDateChange = viewModel::onWeekStartDateChange,
        onDietSummaryChange = viewModel::onDietSummaryChange,
        onHasAlcoholChange = viewModel::onHasAlcoholChange,
        onAlcoholAmountChange = viewModel::onAlcoholAmountChange,
        onHasSmokingChange = viewModel::onHasSmokingChange,
        onSmokingAmountChange = viewModel::onSmokingAmountChange,
        onAverageSleepHoursChange = viewModel::onAverageSleepHoursChange,
        onMemoChange = viewModel::onMemoChange,
        onSaveClick = viewModel::saveLifestyleLog
    )
}

@Composable
fun LifestyleScreen(
    state: LifestyleUiState,
    onWeekStartDateChange: (String) -> Unit = {},
    onDietSummaryChange: (String) -> Unit = {},
    onHasAlcoholChange: (Boolean) -> Unit = {},
    onAlcoholAmountChange: (String) -> Unit = {},
    onHasSmokingChange: (Boolean) -> Unit = {},
    onSmokingAmountChange: (String) -> Unit = {},
    onAverageSleepHoursChange: (String) -> Unit = {},
    onMemoChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-lifestyle")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "생활습관",
            style = MaterialTheme.typography.headlineMedium
        )

        WodLogStatusChip(
            text = if (state.hasExistingLog) "저장된 주간 기록" else "주간 기록 없음",
            tone = if (state.hasExistingLog) WodLogStatusChipTone.Success else WodLogStatusChipTone.Neutral,
            modifier = Modifier.testTag("text-lifestyle-status")
        )

        if (state.isLoading) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.testTag("progress-lifestyle-loading"))
                Text(
                    text = "생활습관 기록을 불러오는 중입니다",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        WodLogCard(
            title = "주간 기준",
            subtitle = "월요일 날짜를 기준으로 한 주를 기록합니다.",
            outlined = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogDateField(
                value = state.weekStartDateInput,
                onValueChange = onWeekStartDateChange,
                label = "주 시작일",
                placeholder = "yyyy-MM-dd",
                supportingText = "월요일 날짜를 직접 입력하거나 달력에서 선택하세요.",
                modifier = Modifier.testTag("input-lifestyle-week-start")
            )
        }

        WodLogSectionHeader(
            title = "식단과 회복",
            description = "식단, 수면, 컨디션 흐름을 질문지에 반영합니다."
        )
        WodLogCard(
            title = "식단 메모",
            subtitle = "일주일 식단을 짧게 요약합니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.dietSummaryInput,
                onValueChange = onDietSummaryChange,
                label = "식단 메모",
                placeholder = "예: 단백질은 충분, 주말 외식 2회",
                singleLine = false,
                minLines = 4,
                modifier = Modifier.testTag("input-lifestyle-diet-summary")
            )
            WodLogTextField(
                value = state.averageSleepHoursInput,
                onValueChange = onAverageSleepHoursChange,
                label = "평균 수면 시간",
                placeholder = "시간",
                supportingText = "0부터 24 사이",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.testTag("input-lifestyle-sleep-hours")
            )
        }

        WodLogCard(
            title = "음주와 흡연",
            subtitle = "없다면 꺼둔 상태로 저장하면 됩니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            ToggleRow(
                label = "음주",
                checked = state.hasAlcohol,
                onCheckedChange = onHasAlcoholChange,
                tag = "toggle-lifestyle-alcohol"
            )
            WodLogTextField(
                value = state.alcoholAmountPerWeekInput,
                onValueChange = onAlcoholAmountChange,
                label = "주간 음주량",
                placeholder = "횟수 또는 잔 수",
                enabled = state.hasAlcohol,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.testTag("input-lifestyle-alcohol-amount")
            )

            ToggleRow(
                label = "흡연",
                checked = state.hasSmoking,
                onCheckedChange = onHasSmokingChange,
                tag = "toggle-lifestyle-smoking"
            )
            WodLogTextField(
                value = state.smokingAmountPerWeekInput,
                onValueChange = onSmokingAmountChange,
                label = "주간 흡연량",
                placeholder = "개비 수",
                enabled = state.hasSmoking,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.testTag("input-lifestyle-smoking-amount")
            )
        }

        WodLogCard(
            title = "메모",
            subtitle = "스트레스, 활동량, 특이사항을 남겨두세요.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.memoInput,
                onValueChange = onMemoChange,
                label = "생활 메모",
                placeholder = "예: 업무 스트레스 높음, 걷기 많음",
                singleLine = false,
                minLines = 3,
                modifier = Modifier.testTag("input-lifestyle-memo")
            )
        }

        if (state.validationErrors.isNotEmpty()) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-lifestyle-errors")
                )
            }
        }

        state.message?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                WodLogStatusChip(
                    text = state.savedLifestyleLogId?.let { "$message (#$it)" } ?: message,
                    tone = WodLogStatusChipTone.Success,
                    modifier = Modifier.testTag("text-lifestyle-message")
                )
            }
        }

        WodLogPrimaryButton(
            text = if (state.isSaving) "저장 중..." else "생활습관 저장",
            onClick = onSaveClick,
            enabled = !state.isSaving && !state.isLoading,
            loading = state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-save-lifestyle")
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.WOD_DATE_INVALID -> "주 시작일은 yyyy-MM-dd 형식으로 입력하세요."
        ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY -> "주 시작일은 월요일이어야 합니다."
        ValidationError.LIFESTYLE_SLEEP_HOURS_INVALID -> "수면 시간은 숫자로 입력하세요."
        ValidationError.LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE -> "수면 시간은 0부터 24 사이로 입력하세요."
        ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_INVALID -> "음주량은 숫자로 입력하세요."
        ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_NEGATIVE -> "음주량은 0 이상이어야 합니다."
        ValidationError.LIFESTYLE_SMOKING_AMOUNT_INVALID -> "흡연량은 숫자로 입력하세요."
        ValidationError.LIFESTYLE_SMOKING_AMOUNT_NEGATIVE -> "흡연량은 0 이상이어야 합니다."
        else -> name
    }
}
