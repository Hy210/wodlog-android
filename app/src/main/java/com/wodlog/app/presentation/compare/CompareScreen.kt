package com.wodlog.app.presentation.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.analysis.CategoryShare
import com.wodlog.app.domain.analysis.ComparisonLabel
import com.wodlog.app.domain.analysis.WodComparisonItem
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "최근 3회 비교",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "정량 지표 중심 요약",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = onRefreshClick,
                modifier = Modifier.testTag("action-refresh-compare")
            ) {
                Text(text = "새로고침")
            }
        }

        when {
            state.isLoading -> CompareLoading()
            state.errorMessage != null -> CompareError(state.errorMessage)
            state.isEmpty || state.summary == null -> CompareEmpty()
            else -> {
                Text(
                    text = "비교 대상 ${state.wodCount}개",
                    style = MaterialTheme.typography.titleMedium
                )
                CompareSummaryList(items = state.summary.items)
                CategoryBreakdown(shares = state.summary.categoryBreakdown)
                NeutralSummary(lines = state.summary.neutralSummary)
            }
        }
    }
}

@Composable
private fun CompareLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compare-loading")
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
        Text(text = "비교 데이터를 불러오는 중")
    }
}

@Composable
private fun CompareEmpty() {
    Text(
        text = "비교할 WOD 기록이 없습니다.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag("compare-empty")
    )
}

@Composable
private fun CompareError(message: String) {
    Text(
        text = "비교 데이터를 불러오지 못했습니다: $message",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag("compare-error")
    )
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

@Composable
private fun WodComparisonCard(item: WodComparisonItem) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(item.label.testTag())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.label.displayName(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "${item.date} · ${item.title}")
            Text(text = "WOD type: ${item.wodType.name}")
            HorizontalDivider()
            MetricRow(label = "Total reps", value = item.totalReps.toString())
            MetricRow(label = "Load volume", value = formatDouble(item.totalLoadVolume))
            MetricRow(label = "Distance", value = formatDouble(item.totalDistance))
            MetricRow(label = "Calories", value = formatDouble(item.totalCalories))
            MetricRow(label = "Rx status", value = item.rxStatus?.name ?: "-")
            MetricRow(label = "RPE", value = item.rpe?.toString() ?: "-")
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}

@Composable
private fun CategoryBreakdown(shares: List<CategoryShare>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compare-category-breakdown"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "카테고리별 비중",
            style = MaterialTheme.typography.titleMedium
        )
        if (shares.isEmpty()) {
            Text(text = "기록된 movement 카테고리가 없습니다.")
        } else {
            shares.forEach { share ->
                Text(
                    text = "${share.category.name}: ${share.count}개, ${formatPercent(share.ratio)}"
                )
            }
        }
    }
}

@Composable
private fun NeutralSummary(lines: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compare-neutral-summary"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "요약",
            style = MaterialTheme.typography.titleMedium
        )
        lines.forEach { line ->
            Text(text = line)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun ComparisonLabel.displayName(): String = when (this) {
    ComparisonLabel.Older -> "전전 WOD"
    ComparisonLabel.Previous -> "전 WOD"
    ComparisonLabel.Current -> "현재 WOD"
}

private fun ComparisonLabel.testTag(): String = when (this) {
    ComparisonLabel.Older -> "compare-item-older"
    ComparisonLabel.Previous -> "compare-item-previous"
    ComparisonLabel.Current -> "compare-item-current"
}

private fun formatDouble(value: Double): String =
    String.format(Locale.US, "%.1f", value)

private fun formatPercent(value: Double): String =
    String.format(Locale.US, "%.0f%%", value * 100.0)
