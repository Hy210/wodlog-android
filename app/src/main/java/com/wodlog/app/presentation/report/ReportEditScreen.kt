package com.wodlog.app.presentation.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.AiReport

@Composable
fun ReportEditRoute(
    viewModel: ReportEditViewModel,
    wodId: Long
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(wodId) {
        viewModel.loadReports(wodId)
    }

    ReportEditScreen(
        state = state,
        onAnswerChange = viewModel::onAnswerChange,
        onSelectReport = viewModel::selectReport,
        onNewReportClick = viewModel::clearSelection,
        onSaveClick = viewModel::saveReport,
        onDeleteClick = {
            state.selectedReportId?.let(viewModel::deleteReport)
        }
    )
}

@Composable
fun ReportEditScreen(
    state: ReportEditUiState,
    onAnswerChange: (String) -> Unit,
    onSelectReport: (Long) -> Unit,
    onNewReportClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-report-edit")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "GPT report",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Paste the answer you copied from ChatGPT and save it to this WOD.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "No AI request is made here. This screen only saves text you paste manually.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("text-report-local-only-note")
        )

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-report-error")
            )
        }

        if (state.validationErrors.isNotEmpty()) {
            Text(
                text = state.validationErrors.joinToString(separator = "\n"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-report-errors")
            )
        }

        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-report-message")
            )
        }

        ReportList(
            reports = state.reports,
            onSelectReport = onSelectReport
        )

        OutlinedButton(
            onClick = onNewReportClick,
            modifier = Modifier.testTag("action-new-report")
        ) {
            Text("New report")
        }

        OutlinedTextField(
            value = state.answerInput,
            onValueChange = onAnswerChange,
            label = { Text("Pasted GPT answer") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .testTag("input-report-answer"),
            minLines = 8
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving && !state.isDeleting,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-save-report")
            ) {
                Text(if (state.isSaving) "Saving..." else "Save")
            }
            OutlinedButton(
                onClick = onDeleteClick,
                enabled = state.selectedReportId != null && !state.isDeleting && !state.isSaving,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-delete-report")
            ) {
                Text(if (state.isDeleting) "Deleting..." else "Delete")
            }
        }
    }
}

@Composable
private fun ReportList(
    reports: List<AiReport>,
    onSelectReport: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report-list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Saved reports",
            style = MaterialTheme.typography.titleMedium
        )
        if (reports.isEmpty()) {
            Text(
                text = "No saved reports yet.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            reports.forEach { report ->
                ReportItem(
                    report = report,
                    onClick = { onSelectReport(report.id) }
                )
            }
        }
    }
}

@Composable
private fun ReportItem(
    report: AiReport,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report-item-${report.id}")
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Report #${report.id} · ${report.updatedAt}",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = report.reportText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider()
    }
}
