package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection

data class PromptInput(
    val profile: UserProfile? = null,
    val currentWod: Wod,
    val sections: List<WodSection> = emptyList(),
    val movements: List<Movement> = emptyList(),
    val result: WodResult? = null,
    val recentSummary: AnalysisSummary? = null,
    val lifestyleLog: LifestyleLog? = null,
    val additionalUserMemo: String? = null
)
