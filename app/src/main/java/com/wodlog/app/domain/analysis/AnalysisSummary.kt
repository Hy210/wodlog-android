package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodType
import java.time.LocalDate

data class WodAnalysisInput(
    val wod: Wod,
    val movements: List<Movement> = emptyList(),
    val result: WodResult? = null
)

data class AnalysisSummary(
    val items: List<WodComparisonItem>,
    val categoryBreakdown: List<CategoryShare>,
    val neutralSummary: List<String>,
    val hasEnoughDataForComparison: Boolean
)

data class WodComparisonItem(
    val label: ComparisonLabel,
    val wodId: Long,
    val date: LocalDate,
    val title: String,
    val wodType: WodType,
    val totalReps: Int,
    val totalLoadVolume: Double,
    val totalDistance: Double,
    val totalCalories: Double,
    val rxStatus: RxStatus?,
    val rpe: Int?,
    val movementCategoryCounts: Map<MovementCategory, Int>
)

data class CategoryShare(
    val category: MovementCategory,
    val count: Int,
    val ratio: Double
)

enum class ComparisonLabel {
    Older,
    Previous,
    Current
}
