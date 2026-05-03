package com.wodlog.app.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.repository.WodlogRepository
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportEditViewModel(
    private val repository: WodlogRepository,
    private val nowProvider: () -> Instant = { Instant.now() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportEditUiState())
    val uiState: StateFlow<ReportEditUiState> = _uiState.asStateFlow()

    fun loadReports(wodId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    wodId = wodId,
                    isLoading = true,
                    validationErrors = emptyList(),
                    message = null,
                    errorMessage = null
                )
            }

            runCatching {
                repository.getAiReportsForWod(wodId)
            }.onSuccess { reports ->
                _uiState.update {
                    it.copy(
                        reports = reports,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load reports."
                    )
                }
            }
        }
    }

    fun onAnswerChange(value: String) {
        _uiState.update {
            it.copy(
                answerInput = value,
                validationErrors = emptyList(),
                message = null,
                errorMessage = null
            )
        }
    }

    fun selectReport(reportId: Long) {
        val report = _uiState.value.reports.firstOrNull { it.id == reportId } ?: return
        _uiState.update {
            it.copy(
                selectedReportId = report.id,
                answerInput = report.reportText,
                validationErrors = emptyList(),
                message = null,
                errorMessage = null
            )
        }
    }

    fun clearSelection() {
        _uiState.update {
            it.copy(
                selectedReportId = null,
                answerInput = "",
                validationErrors = emptyList(),
                message = null,
                errorMessage = null
            )
        }
    }

    fun saveReport() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    validationErrors = emptyList(),
                    message = null,
                    errorMessage = null
                )
            }

            val state = _uiState.value
            val errors = validate(state)
            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = errors
                    )
                }
                return@launch
            }

            val selectedReport = state.selectedReportId?.let { selectedId ->
                state.reports.firstOrNull { it.id == selectedId }
            }
            val now = nowProvider()
            val report = AiReport(
                id = selectedReport?.id ?: 0L,
                targetWodId = state.wodId,
                promptText = selectedReport?.promptText,
                reportText = state.answerInput.trim(),
                userMemo = selectedReport?.userMemo,
                createdAt = selectedReport?.createdAt ?: now,
                updatedAt = now
            )

            runCatching {
                val reportId = repository.saveAiReport(report)
                val refreshedReports = repository.getAiReportsForWod(state.wodId)
                reportId to refreshedReports
            }.onSuccess { (reportId, reports) ->
                _uiState.update {
                    it.copy(
                        reports = reports,
                        selectedReportId = reportId,
                        answerInput = reports.firstOrNull { report -> report.id == reportId }
                            ?.reportText
                            ?: state.answerInput.trim(),
                        isSaving = false,
                        validationErrors = emptyList(),
                        message = "Report saved",
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Failed to save report."
                    )
                }
            }
        }
    }

    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true,
                    validationErrors = emptyList(),
                    message = null,
                    errorMessage = null
                )
            }

            val wodId = _uiState.value.wodId
            runCatching {
                repository.deleteAiReport(reportId)
                repository.getAiReportsForWod(wodId)
            }.onSuccess { reports ->
                _uiState.update {
                    val deletedSelection = it.selectedReportId == reportId
                    it.copy(
                        reports = reports,
                        selectedReportId = if (deletedSelection) null else it.selectedReportId,
                        answerInput = if (deletedSelection) "" else it.answerInput,
                        isDeleting = false,
                        message = "Report deleted",
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = throwable.message ?: "Failed to delete report."
                    )
                }
            }
        }
    }

    private fun validate(state: ReportEditUiState): List<String> = buildList {
        if (state.wodId <= 0L) add(ERROR_INVALID_WOD_ID)
        if (state.answerInput.isBlank()) add(ERROR_BLANK_ANSWER)
    }

    companion object {
        const val ERROR_BLANK_ANSWER = "ANSWER_REQUIRED"
        const val ERROR_INVALID_WOD_ID = "WOD_ID_REQUIRED"
    }
}
