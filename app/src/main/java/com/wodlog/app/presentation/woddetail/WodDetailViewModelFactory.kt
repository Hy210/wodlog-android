package com.wodlog.app.presentation.woddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodlog.app.domain.repository.WodlogRepository

class WodDetailViewModelFactory(
    private val repository: WodlogRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WodDetailViewModel::class.java)) {
            return WodDetailViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
