package com.wodlog.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.repository.WodlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WodlogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeCafeSources()
    }

    fun onImportClick(onOpenCafeImport: (Long) -> Unit) {
        val cafeSources = _uiState.value.cafeSources
        when (cafeSources.size) {
            0 -> Unit
            1 -> onOpenCafeImport(cafeSources.first().id)
            else -> _uiState.update { it.copy(isCafeSourcePickerVisible = true) }
        }
    }

    fun onCafeSourceSelected(cafeSource: CafeSource, onOpenCafeImport: (Long) -> Unit) {
        _uiState.update { it.copy(isCafeSourcePickerVisible = false) }
        onOpenCafeImport(cafeSource.id)
    }

    fun dismissCafeSourcePicker() {
        _uiState.update { it.copy(isCafeSourcePickerVisible = false) }
    }

    private fun observeCafeSources() {
        viewModelScope.launch {
            repository.observeCafeSources()
                .catch {
                    _uiState.update {
                        it.copy(
                            isLoadingCafeSources = false,
                            errorMessage = "카페 소스 정보를 불러오지 못했습니다."
                        )
                    }
                }
                .collect { cafeSources ->
                    _uiState.update {
                        it.copy(
                            cafeSources = cafeSources,
                            isLoadingCafeSources = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
