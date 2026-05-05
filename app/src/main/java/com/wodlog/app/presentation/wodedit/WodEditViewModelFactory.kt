package com.wodlog.app.presentation.wodedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.domain.repository.WodlogRepository

class WodEditViewModelFactory(
    private val repository: WodlogRepository,
    private val importedWodText: ImportedWodText? = null,
    private val showImportedPrefillMissingMessage: Boolean = false
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WodEditViewModel::class.java)) {
            return WodEditViewModel(
                repository = repository,
                importedWodText = importedWodText,
                showImportedPrefillMissingMessage = showImportedPrefillMissingMessage
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
