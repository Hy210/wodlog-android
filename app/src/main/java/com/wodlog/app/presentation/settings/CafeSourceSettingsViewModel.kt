package com.wodlog.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.repository.WodlogRepository
import java.net.URI
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CafeSourceSettingsViewModel(
    private val repository: WodlogRepository,
    private val nowProvider: () -> Instant = { Instant.now() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(CafeSourceSettingsUiState())
    val uiState: StateFlow<CafeSourceSettingsUiState> = _uiState.asStateFlow()

    init {
        observeCafeSources()
    }

    fun onBoxNameChange(value: String) {
        _uiState.update {
            it.copy(
                boxName = value,
                boxNameError = null,
                message = null,
                errorMessage = null
            )
        }
    }

    fun onBoardUrlChange(value: String) {
        _uiState.update {
            it.copy(
                boardUrl = value,
                boardUrlError = null,
                message = null,
                errorMessage = null
            )
        }
    }

    fun onTitleKeywordsTextChange(value: String) {
        _uiState.update {
            it.copy(
                titleKeywordsText = value,
                message = null,
                errorMessage = null
            )
        }
    }

    fun onPreferMobileUrlChange(value: Boolean) {
        _uiState.update {
            it.copy(
                preferMobileUrl = value,
                message = null,
                errorMessage = null
            )
        }
    }

    fun startEdit(cafeSource: CafeSource) {
        _uiState.update {
            it.copy(
                boxName = cafeSource.boxName,
                boardUrl = cafeSource.boardUrl,
                titleKeywordsText = cafeSource.titleKeywords.ifEmpty { DefaultCafeSourceKeywords }.joinToString(", "),
                preferMobileUrl = cafeSource.preferMobileUrl,
                editingCafeSourceId = cafeSource.id,
                boxNameError = null,
                boardUrlError = null,
                message = null,
                errorMessage = null
            )
        }
    }

    fun cancelEdit() {
        _uiState.update { it.resetForm() }
    }

    fun saveCafeSource() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val boxName = currentState.boxName.trim()
            val boardUrl = currentState.boardUrl.trim()
            val boxNameError = if (boxName.isEmpty()) "Box 이름을 입력해 주세요." else null
            val boardUrlError = when {
                boardUrl.isEmpty() -> "게시판 URL을 입력해 주세요."
                !boardUrl.isValidHttpUrl() -> "올바른 URL을 입력해 주세요."
                else -> null
            }

            if (boxNameError != null || boardUrlError != null) {
                _uiState.update {
                    it.copy(
                        boxNameError = boxNameError,
                        boardUrlError = boardUrlError,
                        message = null,
                        errorMessage = null
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isSaving = true,
                    boxNameError = null,
                    boardUrlError = null,
                    message = null,
                    errorMessage = null
                )
            }

            runCatching {
                val now = nowProvider()
                val existing = currentState.editingCafeSourceId?.let { id ->
                    currentState.cafeSources.firstOrNull { it.id == id } ?: repository.getCafeSource(id)
                }
                repository.saveCafeSource(
                    CafeSource(
                        id = existing?.id ?: 0L,
                        boxName = boxName,
                        boardUrl = boardUrl,
                        titleKeywords = currentState.titleKeywordsText.toKeywordList(),
                        preferMobileUrl = currentState.preferMobileUrl,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.resetForm().copy(
                        isSaving = false,
                        message = "카페 소스를 저장했습니다."
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "카페 소스를 저장하지 못했습니다. 다시 시도해 주세요."
                    )
                }
            }
        }
    }

    fun requestDelete(cafeSource: CafeSource) {
        _uiState.update {
            it.copy(
                deleteTarget = cafeSource,
                message = null,
                errorMessage = null
            )
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun confirmDelete() {
        val target = _uiState.value.deleteTarget ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    message = null,
                    errorMessage = null
                )
            }

            runCatching {
                repository.deleteCafeSource(target.id)
            }.onSuccess {
                _uiState.update {
                    val clearedState = if (it.editingCafeSourceId == target.id) it.resetForm() else it
                    clearedState.copy(
                        deleteTarget = null,
                        isSaving = false,
                        message = "카페 소스를 삭제했습니다."
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        deleteTarget = null,
                        isSaving = false,
                        errorMessage = "카페 소스를 삭제하지 못했습니다. 다시 시도해 주세요."
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, errorMessage = null) }
    }

    private fun observeCafeSources() {
        viewModelScope.launch {
            repository.observeCafeSources()
                .catch {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "카페 소스를 불러오지 못했습니다."
                        )
                    }
                }
                .collect { cafeSources ->
                    _uiState.update {
                        it.copy(
                            cafeSources = cafeSources,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun CafeSourceSettingsUiState.resetForm(): CafeSourceSettingsUiState {
        return copy(
            boxName = "",
            boardUrl = "",
            titleKeywordsText = DefaultCafeSourceKeywords.joinToString(", "),
            preferMobileUrl = true,
            editingCafeSourceId = null,
            boxNameError = null,
            boardUrlError = null,
            message = null,
            errorMessage = null
        )
    }

    private fun String.toKeywordList(): List<String> {
        return split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { DefaultCafeSourceKeywords }
    }

    private fun String.isValidHttpUrl(): Boolean {
        val uri = runCatching { URI(this) }.getOrNull() ?: return false
        return uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
    }
}
