package com.wodlog.app.presentation.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.analysis.AnalysisSummary
import com.wodlog.app.domain.analysis.AnalysisSummaryGenerator
import com.wodlog.app.domain.analysis.WodAnalysisInput
import com.wodlog.app.domain.repository.WodlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompareViewModel(
    private val repository: WodlogRepository,
    private val summaryGenerator: (List<WodAnalysisInput>) -> AnalysisSummary = AnalysisSummaryGenerator::generate
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    fun loadComparison() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                val recentWods = repository.getRecentWods(limit = RECENT_WOD_LIMIT)
                if (recentWods.isEmpty()) {
                    CompareUiState(isEmpty = true)
                } else {
                    val inputs = recentWods.map { wod ->
                        WodAnalysisInput(
                            wod = wod,
                            movements = repository.getMovementsForWod(wod.id),
                            result = repository.getResultForWod(wod.id)
                        )
                    }
                    CompareUiState(
                        summary = summaryGenerator(inputs),
                        wodCount = recentWods.size,
                        isEmpty = false
                    )
                }
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load comparison",
                        isEmpty = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadComparison()
    }

    private companion object {
        const val RECENT_WOD_LIMIT = 3
    }
}
