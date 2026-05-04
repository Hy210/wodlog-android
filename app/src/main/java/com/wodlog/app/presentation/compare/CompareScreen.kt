package com.wodlog.app.presentation.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.analysis.CategoryShare
import com.wodlog.app.domain.analysis.ComparisonLabel
import com.wodlog.app.domain.analysis.WodComparisonItem
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import java.util.Locale

@Composable
fun CompareRoute(viewModel: CompareViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadComparison()
    }

    CompareScreen(
        state = state,
        onRefreshClick = viewModel::refresh
    )
}

@Composable
fun CompareScreen(
    state: CompareUiState,
    onRefreshClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-compare")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 3회 비교",
                style = MaterialTheme.typography.headlineMedium
            )
            WodLogSecondaryButton(
                text = "새로고침",
                onClick = onRefreshClick,
                modifier = Modifier
                    .testTag("action-refresh-compare")
                    .semantics { contentDescription = "비교 데이터 새로고침" }
            )
        }

        CompareHeader()

        when {
            state.isLoading -> CompareLoading()
            state.errorMessage != null -> CompareError(state.errorMessage)
            state.isEmpty || state.summary == null -> CompareEmpty()
            else -> {
                WodLogSectionHeader(
                    title = "최근 3회 비교",
                    description = "비교 대상 ${state.wodCount}개"
                )
                CompareSummaryList(items = state.summary.items)
                CategoryBreakdown(shares = state.summary.categoryBreakdown)
                NeutralSummary(lines = state.summary.neutralSummary)
            }
        }
    }
}

@Composable
private fun CompareHeader() {
    WodLogCard(
        title = "기록 흐름",
        subtitle = "서로 다른 WOD를 순위로 판단하지 않고 정량 지표만 요약합니다.",
        outlined = false
    ) {
        Text(
            text = "reps, load, distance, calories를 나누어 봅니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompareLoading() {
    WodLogCard(
        modifier = Modifier.testTag("compare-loading")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Text(text = "비교 데이터를 불러오는 중입니다")
        }
    }
}

@Composable
private fun CompareEmpty() {
    WodLogCard(
        modifier = Modifier.testTag("compare-empty")
    ) {
        WodLogEmptyState(
            title = "비교할 기록이 아직 부족합니다",
            description = "WOD 기록이 쌓이면 최근 3회를 지표별로 비교할 수 있습니다."
        )
    }
}

@Composable
private fun CompareError(message: String) {
    WodLogCard(
        modifier = Modifier.testTag("compare-error")
    ) {
        Text(
            text = "비교 데이터를 불러오지 못했습니다. $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CompareSummaryList(items: List<WodComparisonItem>) {
    Column(
        modifier = Modifier.testTag("compare-summary-list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            WodComparisonCard(item = item)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WodComparisonCard(item: WodComparisonItem) {
    WodLogCard(
        title = item.label.displayName(),
        subtitle = "${item.date} · ${item.title}",
        actions = {
            WodLogStatusChip(
                text = item.rxStatus.displayName(),
                tone = item.rxStatus.statusTone()
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(item.label.testTag())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WodLogStatusChip(
                text = item.wodType.displayName(),
                tone = WodLogStatusChipTone.Primary
            )
            item.rpe?.let {
                WodLogStatusChip(text = "RPE $it")
            }
        }
        HorizontalDivider()
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WodLogMetricChip(label = "Total reps", value = item.totalReps.toString(), unit = "reps")
            WodLogMetricChip(label = "Load volume", value = formatDouble(item.totalLoadVolume), unit = "kg")
            WodLogMetricChip(label = "Distance", value = formatDouble(item.totalDistance), unit = "m")
            WodLogMetricChip(label = "Calories", value = formatDouble(item.totalCalories), unit = "kcal")
        }
    }
}

@Composable
private fun CategoryBreakdown(shares: List<CategoryShare>) {
    WodLogCard(
        title = "카테고리별 비중",
        subtitle = "최근 기록에 포함된 movement 분포",
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compare-category-breakdown")
    ) {
        if (shares.isEmpty()) {
            Text(
                text = "기록된 movement 카테고리가 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            shares.forEach { share ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${share.category.name}: ${share.count}개 ${formatPercent(share.ratio)}")
                    WodLogStatusChip(
                        text = share.category.displayName(),
                        tone = WodLogStatusChipTone.Neutral
                    )
                }
            }
        }
    }
}

@Composable
private fun NeutralSummary(lines: List<String>) {
    WodLogCard(
        title = "요약",
        subtitle = "자동 판단 없이 확인 가능한 정보만 표시합니다.",
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compare-neutral-summary")
    ) {
        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

private fun ComparisonLabel.displayName(): String = when (this) {
    ComparisonLabel.Older -> "이전 WOD"
    ComparisonLabel.Previous -> "직전 WOD"
    ComparisonLabel.Current -> "현재 WOD"
}

private fun ComparisonLabel.testTag(): String = when (this) {
    ComparisonLabel.Older -> "compare-item-older"
    ComparisonLabel.Previous -> "compare-item-previous"
    ComparisonLabel.Current -> "compare-item-current"
}

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

private fun RxStatus?.displayName(): String = when (this) {
    RxStatus.RX -> "Rx"
    RxStatus.SCALED -> "Scaled"
    RxStatus.CUSTOM -> "Custom"
    RxStatus.UNKNOWN, null -> "Unknown"
}

private fun RxStatus?.statusTone(): WodLogStatusChipTone = when (this) {
    RxStatus.RX -> WodLogStatusChipTone.Success
    RxStatus.SCALED, RxStatus.CUSTOM -> WodLogStatusChipTone.Warning
    RxStatus.UNKNOWN, null -> WodLogStatusChipTone.Neutral
}

private fun MovementCategory.displayName(): String = when (this) {
    MovementCategory.STRENGTH -> "Strength"
    MovementCategory.CARDIO -> "Cardio"
    MovementCategory.GYMNASTICS -> "Gymnastics"
    MovementCategory.WEIGHTLIFTING -> "Weightlifting"
    MovementCategory.BODYWEIGHT -> "Bodyweight"
    MovementCategory.OTHER -> "Other"
}

private fun formatDouble(value: Double): String =
    String.format(Locale.US, "%.1f", value)

private fun formatPercent(value: Double): String =
    String.format(Locale.US, "%.0f%%", value * 100.0)
