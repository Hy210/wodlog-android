package com.wodlog.app.presentation.woddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone

@Composable
fun WodDetailRoute(
    viewModel: WodDetailViewModel,
    wodId: Long,
    onEditResultClick: () -> Unit = {},
    onPromptClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(wodId) {
        viewModel.loadWod(wodId)
    }

    WodDetailScreen(
        state = state,
        onEditResultClick = onEditResultClick,
        onPromptClick = onPromptClick,
        onReportClick = onReportClick
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WodDetailScreen(
    state: WodDetailUiState,
    onEditResultClick: () -> Unit = {},
    onPromptClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-wod-detail")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD 상세",
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            state.isLoading -> {
                WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator()
                        Text(
                            text = "WOD를 불러오는 중입니다",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("text-wod-detail-loading")
                        )
                    }
                }
            }

            state.errorMessage != null -> {
                WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("text-wod-detail-error")
                    )
                }
            }

            state.wod != null -> {
                val wod = state.wod

                WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = wod.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.testTag("text-wod-detail-title")
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WodLogStatusChip(
                            text = wod.date.toString(),
                            tone = WodLogStatusChipTone.Neutral,
                            modifier = Modifier.testTag("text-wod-detail-date")
                        )
                        WodLogStatusChip(
                            text = wod.type.displayName(),
                            tone = WodLogStatusChipTone.Primary,
                            modifier = Modifier.testTag("text-wod-detail-type")
                        )
                        state.result?.rxStatus?.let { rxStatus ->
                            WodLogStatusChip(
                                text = rxStatus.displayName(),
                                tone = if (rxStatus == RxStatus.RX) {
                                    WodLogStatusChipTone.Success
                                } else {
                                    WodLogStatusChipTone.Warning
                                }
                            )
                        }
                    }
                }

                WodLogSectionHeader(
                    title = "기록 정보",
                    description = "WOD 구성과 메모를 확인합니다."
                )

                wod.rawText?.let { rawText ->
                    DetailCard(
                        title = "원문",
                        body = rawText,
                        tag = "text-wod-detail-raw-text"
                    )
                }
                wod.notes?.let { memo ->
                    DetailCard(
                        title = "메모",
                        body = memo,
                        tag = "text-wod-detail-memo"
                    )
                }

                DetailCard(
                    title = "섹션",
                    body = if (state.sections.isEmpty()) {
                        "등록된 섹션이 없습니다."
                    } else {
                        state.sections
                            .sortedBy { it.orderIndex }
                            .joinToString(separator = "\n") { "${it.orderIndex + 1}. ${it.name}" }
                    },
                    tag = "text-wod-detail-sections"
                )

                DetailCard(
                    title = "동작",
                    body = if (state.movements.isEmpty()) {
                        "등록된 동작이 없습니다."
                    } else {
                        state.movements
                            .sortedBy { it.orderIndex }
                            .joinToString(separator = "\n") { it.toDisplayText() }
                    },
                    tag = "text-wod-detail-movements"
                )

                WodLogSectionHeader(
                    title = "결과와 분석",
                    description = "결과 수치와 저장된 GPT 답변을 봅니다."
                )

                WodLogCard(title = "결과", modifier = Modifier.fillMaxWidth()) {
                    state.result?.let { result ->
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WodLogMetricChip(label = "종류", value = result.scoreType.displayName())
                            result.timeSeconds?.let { WodLogMetricChip(label = "시간", value = "${it}s") }
                            result.rounds?.let { WodLogMetricChip(label = "라운드", value = it.toString()) }
                            result.totalReps?.let { WodLogMetricChip(label = "총 reps", value = it.toString()) }
                            result.loadKg?.let { WodLogMetricChip(label = "무게", value = it.toString(), unit = "kg") }
                        }
                    }
                    Text(
                        text = state.result?.toDisplayText() ?: "결과가 아직 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("text-wod-detail-result-status")
                    )
                }

                DetailCard(
                    title = "GPT 답변",
                    body = if (state.aiReports.isEmpty()) {
                        "저장된 GPT 답변이 없습니다."
                    } else {
                        "저장된 답변 ${state.aiReports.size}개"
                    },
                    tag = "text-wod-detail-report-status"
                )
            }
        }

        WodLogSectionHeader(title = "액션")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WodLogSecondaryButton(
                text = "결과 입력",
                onClick = onEditResultClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-edit-result")
            )
            WodLogSecondaryButton(
                text = "질문지 만들기",
                onClick = onPromptClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-open-prompt")
            )
        }
        WodLogPrimaryButton(
            text = "GPT 답변 보기",
            onClick = onReportClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-open-report")
        )
    }
}

@Composable
private fun DetailCard(
    title: String,
    body: String,
    tag: String
) {
    WodLogCard(
        title = title,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag(tag)
        )
    }
}

private fun Movement.toDisplayText(): String {
    val metrics = listOfNotNull(
        weightKg?.let { "${it}kg" },
        reps?.let { "$it reps" },
        sets?.let { "$it sets" },
        rounds?.let { "$it rounds" },
        distanceMeters?.let { "${it}m" },
        calories?.let { "$it cal" },
        durationSeconds?.let { "${it}s" }
    ).joinToString(separator = ", ")
    val categoryText = category?.name?.let { " [$it]" }.orEmpty()
    val metricsText = metrics.takeIf { it.isNotBlank() }?.let { " - $it" }.orEmpty()

    return "${orderIndex + 1}. $name$categoryText$metricsText"
}

private fun WodResult.toDisplayText(): String {
    val values = listOfNotNull(
        "결과 유형: ${scoreType.displayName()}",
        timeSeconds?.let { "시간: ${it}s" },
        rounds?.let { "라운드: $it" },
        extraReps?.let { "추가 reps: $it" },
        totalReps?.let { "총 reps: $it" },
        loadKg?.let { "무게: ${it}kg" },
        distanceMeters?.let { "거리: ${it}m" },
        calories?.let { "칼로리: $it" },
        "스케일: ${rxStatus.displayName()}",
        rpe?.let { "RPE: $it" },
        condition?.let { "컨디션: ${it.displayName()}" },
        memo?.let { "메모: $it" }
    )

    return values.joinToString(separator = "\n")
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

private fun ScoreType.displayName(): String = when (this) {
    ScoreType.TIME -> "Time"
    ScoreType.ROUNDS_REPS -> "Rounds + Reps"
    ScoreType.REPS -> "Reps"
    ScoreType.LOAD -> "Load"
    ScoreType.DISTANCE -> "Distance"
    ScoreType.CALORIES -> "Calories"
    ScoreType.OTHER -> "Other"
}

private fun RxStatus.displayName(): String = when (this) {
    RxStatus.RX -> "Rx"
    RxStatus.SCALED -> "Scaled"
    RxStatus.CUSTOM -> "Custom"
    RxStatus.UNKNOWN -> "미확인"
}

private fun Condition.displayName(): String = when (this) {
    Condition.GREAT -> "아주 좋음"
    Condition.GOOD -> "좋음"
    Condition.NORMAL -> "보통"
    Condition.TIRED -> "피곤함"
    Condition.PAIN -> "통증"
    Condition.UNKNOWN -> "미확인"
}
