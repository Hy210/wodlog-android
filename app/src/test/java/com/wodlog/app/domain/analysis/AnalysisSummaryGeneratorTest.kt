package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodType
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisSummaryGeneratorTest {
    @Test
    fun generate_usesLatestThreeAndOrdersOldestToCurrent() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(1, "First", LocalDate.of(2026, 5, 1)),
                input(4, "Fourth", LocalDate.of(2026, 5, 4)),
                input(2, "Second", LocalDate.of(2026, 5, 2)),
                input(3, "Third", LocalDate.of(2026, 5, 3))
            )
        )

        assertEquals(listOf(2L, 3L, 4L), summary.items.map { it.wodId })
        assertEquals(
            listOf(ComparisonLabel.Older, ComparisonLabel.Previous, ComparisonLabel.Current),
            summary.items.map { it.label }
        )
        assertTrue(summary.hasEnoughDataForComparison)
    }

    @Test
    fun generate_calculatesRepsAndLoadVolumeFromMovementMetrics() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    movements = listOf(
                        movement(reps = 10, sets = 3, rounds = 2, weightKg = 40.0),
                        movement(reps = 5, sets = null, rounds = null, weightKg = 20.0)
                    )
                )
            )
        )

        val item = summary.items.single()
        assertEquals(65, item.totalReps)
        assertEquals(2500.0, item.totalLoadVolume, 0.0001)
    }

    @Test
    fun generate_treatsNullRepsAsZeroAndNullSetsRoundsAsOne() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    movements = listOf(
                        movement(reps = null, sets = 5, rounds = 5, weightKg = 100.0),
                        movement(reps = 7, sets = null, rounds = null, weightKg = 10.0)
                    )
                )
            )
        )

        val item = summary.items.single()
        assertEquals(7, item.totalReps)
        assertEquals(70.0, item.totalLoadVolume, 0.0001)
    }

    @Test
    fun generate_usesMovementDistanceAndCaloriesBeforeResultFallback() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    movements = listOf(
                        movement(distanceMeters = 100.0, calories = 20.0),
                        movement(distanceMeters = 200.0, calories = 30.0)
                    ),
                    result = result(distanceMeters = 1000.0, calories = 300.0)
                )
            )
        )

        val item = summary.items.single()
        assertEquals(300.0, item.totalDistance, 0.0001)
        assertEquals(50.0, item.totalCalories, 0.0001)
    }

    @Test
    fun generate_fallsBackToResultDistanceAndCaloriesWhenMovementTotalsAreZero() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    movements = listOf(movement(distanceMeters = null, calories = null)),
                    result = result(distanceMeters = 750.0, calories = 42.0)
                )
            )
        )

        val item = summary.items.single()
        assertEquals(750.0, item.totalDistance, 0.0001)
        assertEquals(42.0, item.totalCalories, 0.0001)
    }

    @Test
    fun generate_calculatesCategoryShareByMovementCount() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    movements = listOf(
                        movement(category = MovementCategory.STRENGTH),
                        movement(category = MovementCategory.STRENGTH),
                        movement(category = MovementCategory.CARDIO),
                        movement(category = null)
                    )
                )
            )
        )

        val strength = summary.categoryBreakdown.first { it.category == MovementCategory.STRENGTH }
        val cardio = summary.categoryBreakdown.first { it.category == MovementCategory.CARDIO }
        assertEquals(2, strength.count)
        assertEquals(2.0 / 3.0, strength.ratio, 0.0001)
        assertEquals(1, cardio.count)
        assertEquals(1.0 / 3.0, cardio.ratio, 0.0001)
    }

    @Test
    fun generate_keepsResultValuesWhenPresentAndNullWhenMissing() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(id = 1, result = null),
                input(id = 2, result = result(rxStatus = RxStatus.RX, rpe = 8))
            )
        )

        assertNull(summary.items.first().rxStatus)
        assertNull(summary.items.first().rpe)
        assertEquals(RxStatus.RX, summary.items.last().rxStatus)
        assertEquals(8, summary.items.last().rpe)
    }

    @Test
    fun generate_preservesRawSectionsMovementsAndResultForPromptAndCompare() {
        val sections = listOf(section("Strength", orderIndex = 1), section("Warmup", orderIndex = 0))
        val movements = listOf(
            movement(reps = 5, weightKg = 100.0, category = MovementCategory.STRENGTH).copy(orderIndex = 1),
            movement(distanceMeters = 400.0, category = MovementCategory.CARDIO).copy(orderIndex = 0)
        )
        val result = result(rxStatus = RxStatus.SCALED, rpe = 9)
        val summary = AnalysisSummaryGenerator.generate(
            listOf(
                input(
                    id = 1,
                    rawText = "Original WOD text",
                    notes = "Original memo",
                    sections = sections,
                    movements = movements,
                    result = result
                )
            )
        )

        val item = summary.items.single()
        assertEquals("Original WOD text", item.rawText)
        assertEquals("Original memo", item.notes)
        assertEquals(listOf("Warmup", "Strength"), item.sections.map { it.name })
        assertEquals(listOf(MovementCategory.CARDIO, MovementCategory.STRENGTH), item.movements.map { it.category })
        assertEquals(result, item.result)
    }

    @Test
    fun generate_createsSummaryForSingleInputWithoutEnoughComparisonData() {
        val summary = AnalysisSummaryGenerator.generate(listOf(input(1)))

        assertEquals(1, summary.items.size)
        assertEquals(ComparisonLabel.Current, summary.items.single().label)
        assertFalse(summary.hasEnoughDataForComparison)
    }

    @Test
    fun generate_neutralSummaryDoesNotContainJudgmentWords() {
        val summary = AnalysisSummaryGenerator.generate(
            listOf(input(1), input(2), input(3))
        )
        val summaryText = summary.neutralSummary.joinToString(separator = " ")
        val forbiddenWords = listOf("더 좋", "더 나쁘", "향상", "퇴보", "우수")

        forbiddenWords.forEach { word ->
            assertFalse("Forbidden word found: $word", summaryText.contains(word))
        }
    }

    private fun input(
        id: Long,
        title: String = "WOD $id",
        date: LocalDate = LocalDate.of(2026, 5, id.toInt().coerceAtLeast(1)),
        rawText: String? = null,
        notes: String? = null,
        sections: List<WodSection> = emptyList(),
        movements: List<Movement> = emptyList(),
        result: WodResult? = null
    ): WodAnalysisInput = WodAnalysisInput(
        wod = Wod(
            id = id,
            date = date,
            title = title,
            type = WodType.FOR_TIME,
            rawText = rawText,
            notes = notes,
            createdAt = NOW,
            updatedAt = NOW
        ),
        sections = sections,
        movements = movements,
        result = result
    )

    private fun section(
        name: String,
        orderIndex: Int
    ): WodSection = WodSection(
        wodId = 1L,
        name = name,
        orderIndex = orderIndex
    )

    private fun movement(
        reps: Int? = null,
        sets: Int? = null,
        rounds: Int? = null,
        weightKg: Double? = null,
        distanceMeters: Double? = null,
        calories: Double? = null,
        category: MovementCategory? = MovementCategory.OTHER
    ): Movement = Movement(
        wodId = 1L,
        name = "Movement",
        category = category,
        weightKg = weightKg,
        reps = reps,
        sets = sets,
        rounds = rounds,
        distanceMeters = distanceMeters,
        calories = calories,
        orderIndex = 0
    )

    private fun result(
        distanceMeters: Double? = null,
        calories: Double? = null,
        rxStatus: RxStatus = RxStatus.UNKNOWN,
        rpe: Int? = null
    ): WodResult = WodResult(
        wodId = 1L,
        scoreType = ScoreType.TIME,
        distanceMeters = distanceMeters,
        calories = calories,
        rxStatus = rxStatus,
        rpe = rpe,
        createdAt = NOW,
        updatedAt = NOW
    )

    private companion object {
        val NOW: Instant = Instant.parse("2026-05-04T00:00:00Z")
    }
}
