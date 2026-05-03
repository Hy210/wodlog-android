package com.wodlog.app.presentation.woddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.repository.WodlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WodDetailViewModel(
    private val repository: WodlogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WodDetailUiState())
    val uiState: StateFlow<WodDetailUiState> = _uiState.asStateFlow()

    fun loadWod(wodId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val wod = repository.getWodById(wodId)
            if (wod == null) {
                _uiState.value = WodDetailUiState(
                    errorMessage = "WOD not found"
                )
                return@launch
            }

            _uiState.value = WodDetailUiState(
                wod = wod,
                sections = repository.getSectionsForWod(wodId),
                movements = repository.getMovementsForWod(wodId),
                result = repository.getResultForWod(wodId),
                aiReports = repository.getAiReportsForWod(wodId)
            )
        }
    }
}
