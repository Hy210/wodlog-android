package com.wodlog.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodlog.app.domain.repository.WodlogRepository

class CafeSourceSettingsViewModelFactory(
    private val repository: WodlogRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CafeSourceSettingsViewModel::class.java)) {
            return CafeSourceSettingsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
