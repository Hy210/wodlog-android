package com.wodlog.app.presentation.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogConfirmDialog
import com.wodlog.app.presentation.components.WodLogDangerButton
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField

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
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-report-edit")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "GPT 답변 저장",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "ChatGPT에서 복사한 답변을 붙여넣고 이 WOD에 저장합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "AI 요청은 실행하지 않습니다. 이 화면은 직접 붙여넣은 텍스트만 저장합니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-report-local-only-note")
        )

        state.errorMessage?.let { message ->
            MessageCard(
                message = message,
                tag = "text-report-error",
                error = true
            )
        }

        if (state.validationErrors.isNotEmpty()) {
            MessageCard(
                message = state.validationErrors.joinToString(separator = "\n"),
                tag = "text-report-errors",
                error = true
            )
        }

        state.message?.let { message ->
            MessageCard(
                message = message,
                tag = "text-report-message",
                error = false
            )
        }

        ReportList(
            reports = state.reports,
            onSelectReport = onSelectReport
        )

        WodLogSecondaryButton(
            text = "새 답변",
            onClick = onNewReportClick,
            modifier = Modifier.testTag("action-new-report")
        )

        WodLogCard(
            title = "답변 본문",
            subtitle = "긴 답변도 읽기 쉽도록 여백을 두고 저장합니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.answerInput,
                onValueChange = onAnswerChange,
                label = "GPT 답변",
                placeholder = "ChatGPT 답변을 붙여넣으세요",
                singleLine = false,
                minLines = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .testTag("input-report-answer")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WodLogPrimaryButton(
                text = if (state.isSaving) "저장 중..." else "GPT 답변 저장",
                onClick = onSaveClick,
                enabled = !state.isSaving && !state.isDeleting,
                loading = state.isSaving,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-save-report")
            )
            WodLogDangerButton(
                text = if (state.isDeleting) "삭제 중..." else "삭제",
                onClick = { showDeleteDialog = true },
                enabled = state.selectedReportId != null && !state.isDeleting && !state.isSaving,
                loading = state.isDeleting,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-delete-report")
            )
        }
    }

    if (showDeleteDialog) {
        WodLogConfirmDialog(
            title = "삭제하시겠습니까?",
            message = "저장된 GPT 답변이 삭제됩니다. 이 작업은 되돌릴 수 없습니다.",
            confirmText = "삭제",
            dismissText = "취소",
            dangerous = true,
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun MessageCard(
    message: String,
    tag: String,
    error: Boolean
) {
    WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
        WodLogStatusChip(
            text = message,
            tone = if (error) WodLogStatusChipTone.Error else WodLogStatusChipTone.Success,
            modifier = Modifier.testTag(tag)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
            text = "저장된 답변",
            style = MaterialTheme.typography.titleMedium
        )
        if (reports.isEmpty()) {
            WodLogCard(modifier = Modifier.fillMaxWidth()) {
                WodLogEmptyState(
                    title = "저장된 답변이 없습니다",
                    description = "답변을 붙여넣고 저장하세요."
                )
            }
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WodLogStatusChip(
                    text = "${reports.size}개 저장됨",
                    tone = WodLogStatusChipTone.Success
                )
            }
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
    WodLogCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report-item-${report.id}")
            .clickable(onClick = onClick),
        title = "답변 #${report.id}",
        subtitle = report.updatedAt.toString()
    ) {
        Text(
            text = report.reportText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
