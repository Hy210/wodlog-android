package com.wodlog.app.presentation.resultedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodlog.app.domain.repository.WodlogRepository

class ResultEditViewModelFactory(
    private val repository: WodlogRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultEditViewModel::class.java)) {
            return ResultEditViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
