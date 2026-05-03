package com.wodlog.app.presentation.woddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.WodResult

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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD Detail",
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            state.isLoading -> {
                Text(
                    text = "Loading WOD...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-wod-detail-loading")
                )
            }

            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-wod-detail-error")
                )
            }

            state.wod != null -> {
                val wod = state.wod
                Text(
                    text = wod.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("text-wod-detail-title")
                )
                Text(
                    text = wod.date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-wod-detail-date")
                )
                Text(
                    text = wod.type.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-wod-detail-type")
                )
                wod.rawText?.let { rawText ->
                    DetailBlock(
                        title = "Raw text",
                        body = rawText,
                        tag = "text-wod-detail-raw-text"
                    )
                }
                wod.notes?.let { memo ->
                    DetailBlock(
                        title = "Memo",
                        body = memo,
                        tag = "text-wod-detail-memo"
                    )
                }

                DetailBlock(
                    title = "Sections",
                    body = if (state.sections.isEmpty()) {
                        "No sections recorded."
                    } else {
                        state.sections
                            .sortedBy { it.orderIndex }
                            .joinToString(separator = "\n") { "${it.orderIndex + 1}. ${it.name}" }
                    },
                    tag = "text-wod-detail-sections"
                )

                DetailBlock(
                    title = "Movements",
                    body = if (state.movements.isEmpty()) {
                        "No movements recorded."
                    } else {
                        state.movements
                            .sortedBy { it.orderIndex }
                            .joinToString(separator = "\n") { it.toDisplayText() }
                    },
                    tag = "text-wod-detail-movements"
                )

                DetailBlock(
                    title = "Result",
                    body = state.result?.toDisplayText() ?: "No result recorded yet.",
                    tag = "text-wod-detail-result-status"
                )

                DetailBlock(
                    title = "GPT reports",
                    body = if (state.aiReports.isEmpty()) {
                        "No saved GPT reports yet."
                    } else {
                        "${state.aiReports.size} saved report(s)."
                    },
                    tag = "text-wod-detail-report-status"
                )
            }
        }

        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onEditResultClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-edit-result")
            ) {
                Text("Result")
            }
            OutlinedButton(
                onClick = onPromptClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-open-prompt")
            ) {
                Text("Prompt")
            }
        }
        Button(
            onClick = onReportClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-open-report")
        ) {
            Text("GPT report")
        }
    }
}

@Composable
private fun DetailBlock(
    title: String,
    body: String,
    tag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag(tag)
        )
    }
}

private fun Movement.toDisplayText(): String {
    val metrics = listOfNotNull(
        weightKg?.let { "${it}kg" },
        reps?.let { "${it} reps" },
        sets?.let { "${it} sets" },
        rounds?.let { "${it} rounds" },
        distanceMeters?.let { "${it}m" },
        calories?.let { "${it} cal" },
        durationSeconds?.let { "${it}s" }
    ).joinToString(separator = ", ")
    val categoryText = category?.name?.let { " [$it]" }.orEmpty()
    val metricsText = metrics.takeIf { it.isNotBlank() }?.let { " - $it" }.orEmpty()

    return "${orderIndex + 1}. $name$categoryText$metricsText"
}

private fun WodResult.toDisplayText(): String {
    val values = listOfNotNull(
        "score=${scoreType.name}",
        timeSeconds?.let { "time=${it}s" },
        rounds?.let { "rounds=$it" },
        extraReps?.let { "extra reps=$it" },
        totalReps?.let { "total reps=$it" },
        loadKg?.let { "load=${it}kg" },
        distanceMeters?.let { "distance=${it}m" },
        calories?.let { "calories=$it" },
        "rx=${rxStatus.name}",
        rpe?.let { "rpe=$it" },
        condition?.let { "condition=${it.name}" },
        memo?.let { "memo=$it" }
    )

    return values.joinToString(separator = ", ")
}
