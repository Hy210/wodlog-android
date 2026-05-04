package com.wodlog.app.presentation.resultedit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField
import com.wodlog.app.util.ValidationError

@Composable
fun ResultEditRoute(
    viewModel: ResultEditViewModel,
    wodId: Long,
    onSaved: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(wodId) {
        viewModel.loadResult(wodId)
    }
    LaunchedEffect(state.savedResultId) {
        state.savedResultId?.let {
            onSaved()
            viewModel.onSavedNavigationHandled()
        }
    }

    ResultEditScreen(
        state = state,
        onScoreTypeChange = viewModel::onScoreTypeChange,
        onTimeSecondsChange = viewModel::onTimeSecondsChange,
        onRoundsChange = viewModel::onRoundsChange,
        onRepsChange = viewModel::onRepsChange,
        onTotalRepsChange = viewModel::onTotalRepsChange,
        onLoadChange = viewModel::onLoadChange,
        onDistanceChange = viewModel::onDistanceChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onRxStatusChange = viewModel::onRxStatusChange,
        onRpeChange = viewModel::onRpeChange,
        onConditionChange = viewModel::onConditionChange,
        onMemoChange = viewModel::onMemoChange,
        onSaveClick = viewModel::saveResult
    )
}

@Composable
fun ResultEditScreen(
    state: ResultEditUiState,
    onScoreTypeChange: (ScoreType) -> Unit,
    onTimeSecondsChange: (String) -> Unit,
    onRoundsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onTotalRepsChange: (String) -> Unit,
    onLoadChange: (String) -> Unit,
    onDistanceChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onRxStatusChange: (RxStatus) -> Unit,
    onRpeChange: (String) -> Unit,
    onConditionChange: (Condition) -> Unit,
    onMemoChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-result-edit")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "결과 입력",
            style = MaterialTheme.typography.headlineMedium
        )
        WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WodLogMetricChip(label = "WOD", value = "#${state.wodId}")
                WodLogStatusChip(
                    text = if (state.hasExistingResult) "수정 중" else "새 결과",
                    tone = if (state.hasExistingResult) WodLogStatusChipTone.Warning else WodLogStatusChipTone.Primary
                )
            }
            Text(
                text = "결과 유형을 고르고 필요한 수치를 빠르게 입력하세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WodLogCard(
            title = "결과 유형",
            subtitle = "Time, Rounds + Reps, Load처럼 기록 기준을 먼저 선택합니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            EnumSelector(
                label = "Score Type",
                values = ScoreType.entries,
                selectedValue = state.scoreType,
                onSelect = onScoreTypeChange,
                tagPrefix = "input-result-score-type",
                displayText = { it.displayName() }
            )
        }

        WodLogSectionHeader(
            title = "기록 수치",
            description = state.scoreType?.supportingText() ?: "결과 유형을 선택한 뒤 필요한 값만 채우면 됩니다."
        )
        WodLogCard(modifier = Modifier.fillMaxWidth()) {
            MetricRow {
                MetricInput("시간", "초", state.timeSecondsInput, "input-result-time", onTimeSecondsChange)
                MetricInput("라운드", null, state.roundsInput, "input-result-rounds", onRoundsChange)
            }
            MetricRow {
                MetricInput("Reps", null, state.repsInput, "input-result-reps", onRepsChange)
                MetricInput("총 reps", null, state.totalRepsInput, "input-result-total-reps", onTotalRepsChange)
            }
            MetricRow {
                MetricInput("무게", "kg", state.loadInput, "input-result-load", onLoadChange)
                MetricInput("거리", "m", state.distanceInput, "input-result-distance", onDistanceChange)
            }
            MetricRow {
                MetricInput("Calories", "kcal", state.caloriesInput, "input-result-calories", onCaloriesChange)
                MetricInput("RPE", "1-10", state.rpeInput, "input-result-rpe", onRpeChange)
            }
        }

        WodLogCard(
            title = "상태",
            subtitle = "Rx/Scaled와 컨디션을 함께 남깁니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            EnumSelector(
                label = "Rx 상태",
                values = RxStatus.entries,
                selectedValue = state.rxStatus,
                onSelect = onRxStatusChange,
                tagPrefix = "input-result-rx-status",
                displayText = { it.displayName() }
            )
            EnumSelector(
                label = "컨디션",
                values = Condition.entries,
                selectedValue = state.condition,
                onSelect = onConditionChange,
                tagPrefix = "input-result-condition",
                displayText = { it.displayName() }
            )
        }

        WodLogCard(
            title = "기록 메모",
            subtitle = "전략, 페이스, 실패 지점 등을 남겨두세요.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.memoInput,
                onValueChange = onMemoChange,
                label = "메모",
                placeholder = "예: 마지막 라운드에서 grip이 풀림",
                singleLine = false,
                minLines = 3,
                modifier = Modifier.testTag("input-result-memo")
            )
        }

        if (state.validationErrors.isNotEmpty()) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-result-validation-errors")
                )
            }
        }

        state.message?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                WodLogStatusChip(
                    text = message,
                    tone = WodLogStatusChipTone.Success,
                    modifier = Modifier.testTag("text-result-message")
                )
            }
        }

        WodLogPrimaryButton(
            text = if (state.isSaving) "저장 중..." else "결과 저장",
            onClick = onSaveClick,
            enabled = !state.isSaving,
            loading = state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-save-result")
        )
    }
}

@Composable
private fun MetricRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun RowScope.MetricInput(
    label: String,
    unit: String?,
    value: String,
    tag: String,
    onValueChange: (String) -> Unit
) {
    WodLogTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = unit ?: "숫자",
        supportingText = unit,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .weight(1f)
            .testTag(tag)
    )
}

@Composable
private fun <T : Enum<T>> EnumSelector(
    label: String,
    values: List<T>,
    selectedValue: T?,
    onSelect: (T) -> Unit,
    tagPrefix: String,
    displayText: (T) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .testTag(tagPrefix),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                WodLogStatusChip(
                    text = displayText(value),
                    selected = value == selectedValue,
                    tone = if (value == selectedValue) {
                        WodLogStatusChipTone.Primary
                    } else {
                        WodLogStatusChipTone.Neutral
                    },
                    onClick = { onSelect(value) },
                    modifier = Modifier.testTag("$tagPrefix-${value.name}")
                )
            }
        }
    }
}

private fun ScoreType.displayName(): String = when (this) {
    ScoreType.TIME -> "Time"
    ScoreType.ROUNDS_REPS -> "Rounds + Reps"
    ScoreType.REPS -> "Reps"
    ScoreType.LOAD -> "Load"
    ScoreType.DISTANCE -> "Distance"
    ScoreType.CALORIES -> "Calories"
    ScoreType.OTHER -> "Other"
}

private fun ScoreType.supportingText(): String = when (this) {
    ScoreType.TIME -> "시간을 초 단위로 입력하세요."
    ScoreType.ROUNDS_REPS -> "라운드와 추가 reps를 함께 입력하세요."
    ScoreType.REPS -> "총 reps를 중심으로 입력하세요."
    ScoreType.LOAD -> "무게를 kg 단위로 입력하세요."
    ScoreType.DISTANCE -> "거리를 m 단위로 입력하세요."
    ScoreType.CALORIES -> "Calories 값을 입력하세요."
    ScoreType.OTHER -> "필요한 수치와 메모를 자유롭게 남기세요."
}

private fun RxStatus.displayName(): String = when (this) {
    RxStatus.RX -> "Rx"
    RxStatus.SCALED -> "Scaled"
    RxStatus.CUSTOM -> "Custom"
    RxStatus.UNKNOWN -> "Unknown"
}

private fun Condition.displayName(): String = when (this) {
    Condition.GREAT -> "아주 좋음"
    Condition.GOOD -> "좋음"
    Condition.NORMAL -> "보통"
    Condition.TIRED -> "피곤함"
    Condition.PAIN -> "통증"
    Condition.UNKNOWN -> "미확인"
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.SCORE_TYPE_REQUIRED -> "결과 유형을 선택하세요."
        ValidationError.RESULT_TIME_INVALID -> "시간은 숫자로 입력하세요."
        ValidationError.RESULT_ROUNDS_INVALID -> "라운드는 숫자로 입력하세요."
        ValidationError.RESULT_REPS_INVALID -> "Reps는 숫자로 입력하세요."
        ValidationError.RESULT_TOTAL_REPS_INVALID -> "총 reps는 숫자로 입력하세요."
        ValidationError.RESULT_LOAD_INVALID -> "무게는 숫자로 입력하세요."
        ValidationError.RESULT_DISTANCE_INVALID -> "거리는 숫자로 입력하세요."
        ValidationError.RESULT_CALORIES_INVALID -> "Calories는 숫자로 입력하세요."
        ValidationError.RESULT_RPE_INVALID -> "RPE는 숫자로 입력하세요."
        ValidationError.RESULT_TIME_NEGATIVE -> "시간은 0 이상이어야 합니다."
        ValidationError.RESULT_ROUNDS_NEGATIVE -> "라운드는 0 이상이어야 합니다."
        ValidationError.RESULT_REPS_NEGATIVE -> "Reps는 0 이상이어야 합니다."
        ValidationError.RESULT_TOTAL_REPS_NEGATIVE -> "총 reps는 0 이상이어야 합니다."
        ValidationError.RESULT_LOAD_NEGATIVE -> "무게는 0 이상이어야 합니다."
        ValidationError.RESULT_DISTANCE_NEGATIVE -> "거리는 0 이상이어야 합니다."
        ValidationError.RESULT_CALORIES_NEGATIVE -> "Calories는 0 이상이어야 합니다."
        ValidationError.RESULT_RPE_OUT_OF_RANGE -> "RPE는 1부터 10 사이로 입력하세요."
        else -> name
    }
}
