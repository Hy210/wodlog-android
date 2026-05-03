package com.wodlog.app.presentation.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.analysis.AnalysisSummary
import com.wodlog.app.domain.analysis.AnalysisSummaryGenerator
import com.wodlog.app.domain.analysis.PromptGenerator
import com.wodlog.app.domain.analysis.PromptInput
import com.wodlog.app.domain.analysis.WodAnalysisInput
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.WodlogDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptViewModel(
    private val repository: WodlogRepository,
    private val promptGenerator: (PromptInput) -> String = PromptGenerator::generate,
    private val summaryGenerator: (List<WodAnalysisInput>) -> AnalysisSummary = AnalysisSummaryGenerator::generate
) : ViewModel() {
    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    fun loadPrompt(wodId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    copyMessage = null
                )
            }

            runCatching {
                val wod = repository.getWodById(wodId)
                    ?: return@runCatching PromptUiState(errorMessage = "WOD not found.")
                val movements = repository.getMovementsForWod(wod.id)
                val result = repository.getResultForWod(wod.id)
                val profile = repository.getUserProfile()
                val lifestyleLog = repository.getLifestyleLogByWeekStart(
                    WodlogDateUtils.weekStart(wod.date)
                )
                val recentSummary = buildRecentSummary()
                val promptText = promptGenerator(
                    PromptInput(
                        profile = profile,
                        currentWod = wod,
                        movements = movements,
                        result = result,
                        recentSummary = recentSummary,
                        lifestyleLog = lifestyleLog
                    )
                )

                PromptUiState(
                    promptText = promptText,
                    wodTitle = wod.title
                )
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to generate prompt."
                    )
                }
            }
        }
    }

    fun onCopied() {
        _uiState.update {
            it.copy(copyMessage = "Prompt copied")
        }
    }

    fun clearCopyMessage() {
        _uiState.update {
            it.copy(copyMessage = null)
        }
    }

    private suspend fun buildRecentSummary(): AnalysisSummary? {
        val recentWods = repository.getRecentWods(limit = RECENT_WOD_LIMIT)
        if (recentWods.isEmpty()) return null

        val inputs = recentWods.map { wod ->
            WodAnalysisInput(
                wod = wod,
                movements = repository.getMovementsForWod(wod.id),
                result = repository.getResultForWod(wod.id)
            )
        }

        return summaryGenerator(inputs)
    }

    private companion object {
        const val RECENT_WOD_LIMIT = 3
    }
}
