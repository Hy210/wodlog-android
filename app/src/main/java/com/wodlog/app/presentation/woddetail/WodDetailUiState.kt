package com.wodlog.app.presentation.woddetail

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection

data class WodDetailUiState(
    val isLoading: Boolean = false,
    val wod: Wod? = null,
    val sections: List<WodSection> = emptyList(),
    val movements: List<Movement> = emptyList(),
    val result: WodResult? = null,
    val aiReports: List<AiReport> = emptyList(),
    val errorMessage: String? = null
)
