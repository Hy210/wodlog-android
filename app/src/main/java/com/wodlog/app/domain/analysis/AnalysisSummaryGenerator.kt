package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.WodResult

object AnalysisSummaryGenerator {
    fun generate(inputs: List<WodAnalysisInput>): AnalysisSummary {
        val selectedInputs = inputs
            .sortedWith(compareBy<WodAnalysisInput> { it.wod.date }.thenBy { it.wod.id })
            .takeLast(MAX_ITEMS)

        val items = selectedInputs.mapIndexed { index, input ->
            input.toComparisonItem(labelFor(index, selectedInputs.size))
        }

        return AnalysisSummary(
            items = items,
            categoryBreakdown = categoryBreakdown(selectedInputs.flatMap { it.movements }),
            neutralSummary = neutralSummary(selectedInputs.size),
            hasEnoughDataForComparison = selectedInputs.size >= MIN_COMPARISON_ITEMS
        )
    }

    private fun WodAnalysisInput.toComparisonItem(label: ComparisonLabel): WodComparisonItem {
        val movementDistance = movements.sumOf { it.distanceMeters ?: 0.0 }
        val movementCalories = movements.sumOf { it.calories ?: 0.0 }

        return WodComparisonItem(
            label = label,
            wodId = wod.id,
            date = wod.date,
            title = wod.title,
            wodType = wod.type,
            totalReps = movements.sumOf { it.totalReps() },
            totalLoadVolume = movements.sumOf { it.loadVolume() },
            totalDistance = movementDistance.takeIf { it > 0.0 } ?: result?.distanceMeters ?: 0.0,
            totalCalories = movementCalories.takeIf { it > 0.0 } ?: result?.calories ?: 0.0,
            rxStatus = result?.rxStatus,
            rpe = result?.rpe,
            movementCategoryCounts = movements.categoryCounts()
        )
    }

    private fun Movement.totalReps(): Int {
        val repCount = reps ?: return 0
        return repCount * (sets ?: 1) * (rounds ?: 1)
    }

    private fun Movement.loadVolume(): Double {
        val load = weightKg ?: return 0.0
        val repCount = reps ?: return 0.0
        return load * repCount * (sets ?: 1) * (rounds ?: 1)
    }

    private fun List<Movement>.categoryCounts(): Map<MovementCategory, Int> {
        return mapNotNull { it.category }
            .groupingBy { it }
            .eachCount()
            .toSortedMap(compareBy { it.name })
    }

    private fun categoryBreakdown(movements: List<Movement>): List<CategoryShare> {
        val counts = movements.categoryCounts()
        val total = counts.values.sum()
        if (total == 0) return emptyList()

        return counts.map { (category, count) ->
            CategoryShare(
                category = category,
                count = count,
                ratio = count.toDouble() / total
            )
        }
    }

    private fun labelFor(index: Int, size: Int): ComparisonLabel {
        return when (size) {
            1 -> ComparisonLabel.Current
            2 -> if (index == 0) ComparisonLabel.Previous else ComparisonLabel.Current
            else -> when (index) {
                0 -> ComparisonLabel.Older
                1 -> ComparisonLabel.Previous
                else -> ComparisonLabel.Current
            }
        }
    }

    private fun neutralSummary(itemCount: Int): List<String> {
        return buildList {
            add("최근 ${itemCount}회 기록을 날짜순으로 요약했습니다.")
            add("총 반복수와 볼륨은 입력된 movement 값을 기준으로 계산했습니다.")
            add("서로 다른 WOD는 직접적인 우열 비교 없이 정량 지표만 표시합니다.")
            if (itemCount < MIN_COMPARISON_ITEMS) {
                add("기록이 3개 미만이면 가능한 기록만 표시합니다.")
            }
        }
    }

    private const val MAX_ITEMS = 3
    private const val MIN_COMPARISON_ITEMS = 3
}
