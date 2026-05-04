package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodType
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptGeneratorTest {
    private val now = Instant.parse("2026-05-04T00:00:00Z")

    @Test
    fun generate_includesCoachRoleInstruction() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("너는 CrossFit 코치이자 운동 기록 분석가다."))
        assertTrue(prompt.contains("다음 훈련을 더 잘 준비할 수 있게 분석해줘."))
    }

    @Test
    fun generate_includesCurrentWodDateTitleAndType() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("2026-05-04"))
        assertTrue(prompt.contains("Fran"))
        assertTrue(prompt.contains(WodType.FOR_TIME.name))
    }

    @Test
    fun generate_includesMovementInformation() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("Thruster"))
        assertTrue(prompt.contains(MovementCategory.WEIGHTLIFTING.name))
        assertTrue(prompt.contains("42.5 kg"))
        assertTrue(prompt.contains("21"))
        assertTrue(prompt.contains("3"))
    }

    @Test
    fun generate_whenResultExists_includesScoreRpeAndRxInformation() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains(ScoreType.TIME.name))
        assertTrue(prompt.contains(RxStatus.RX.name))
        assertTrue(prompt.contains("RPE: 8"))
        assertTrue(prompt.contains(Condition.GOOD.name))
    }

    @Test
    fun generate_whenProfileExists_includesProfileInformation() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("180 cm"))
        assertTrue(prompt.contains("82.5 kg"))
        assertTrue(prompt.contains("2024-01-01"))
    }

    @Test
    fun generate_whenLifestyleExists_includesDietAlcoholSmokingAndSleepInformation() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("protein focused"))
        assertTrue(prompt.contains("2 drinks"))
        assertTrue(prompt.contains("0 cigarettes"))
        assertTrue(prompt.contains("7.5 hours"))
    }

    @Test
    fun generate_whenRecentSummaryExists_includesQuantitativeSummary() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("최근 3회 비교 요약"))
        assertTrue(prompt.contains("전전 WOD"))
        assertTrue(prompt.contains("Total reps: 90"))
        assertTrue(prompt.contains("Load volume: 1200"))
        assertTrue(prompt.contains("STRENGTH: 2개, 67%"))
    }

    @Test
    fun generate_withOptionalNulls_doesNotExposeNullString() {
        val prompt = PromptGenerator.generate(
            PromptInput(
                currentWod = sampleWod().copy(rawText = null, notes = null),
                movements = listOf(
                    sampleMovement().copy(
                        category = null,
                        weightKg = null,
                        notes = null
                    )
                ),
                result = null,
                profile = null,
                lifestyleLog = null,
                recentSummary = null,
                additionalUserMemo = null
            )
        )

        assertFalse(prompt.contains("null", ignoreCase = true))
        assertTrue(prompt.contains("미입력"))
    }

    @Test
    fun generate_doesNotContainApiKeyEndpointOrNetworkCallPhrases() {
        val prompt = PromptGenerator.generate(sampleInput())
        val lowerPrompt = prompt.lowercase()

        assertFalse(lowerPrompt.contains("api key"))
        assertFalse(lowerPrompt.contains("endpoint"))
        assertFalse(lowerPrompt.contains("openai"))
        assertFalse(lowerPrompt.contains("http"))
        assertFalse(lowerPrompt.contains("model"))
    }

    @Test
    fun generate_includesNoSimpleSuperiorityJudgementInstruction() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("단순히 더 좋다/나쁘다로 판단하지 말고"))
        assertTrue(prompt.contains("정량 지표 중심"))
    }

    @Test
    fun generate_includesMedicalSafetyInstruction() {
        val prompt = PromptGenerator.generate(sampleInput())

        assertTrue(prompt.contains("의학적 진단이 아니라 일반적인 운동 기록 분석"))
    }

    private fun sampleInput(): PromptInput {
        return PromptInput(
            profile = UserProfile(
                heightCm = 180.0,
                weightKg = 82.5,
                crossfitStartDate = LocalDate.of(2024, 1, 1),
                createdAt = now,
                updatedAt = now
            ),
            currentWod = sampleWod(),
            movements = listOf(sampleMovement()),
            result = sampleResult(),
            recentSummary = sampleSummary(),
            lifestyleLog = LifestyleLog(
                weekStartDate = LocalDate.of(2026, 4, 27),
                mealSummary = "protein focused",
                alcohol = true,
                alcoholAmountPerWeek = "2 drinks",
                smoking = false,
                smokingAmountPerWeek = "0 cigarettes",
                sleepAverageHours = 7.5,
                notes = "slept well",
                createdAt = now,
                updatedAt = now
            ),
            additionalUserMemo = "Shoulder felt tight"
        )
    }

    private fun sampleWod(): Wod {
        return Wod(
            id = 3L,
            date = LocalDate.of(2026, 5, 4),
            title = "Fran",
            type = WodType.FOR_TIME,
            rawText = "21-15-9 thrusters and pull-ups",
            notes = "fast transitions",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun sampleMovement(): Movement {
        return Movement(
            id = 1L,
            wodId = 3L,
            name = "Thruster",
            category = MovementCategory.WEIGHTLIFTING,
            weightKg = 42.5,
            reps = 21,
            sets = 3,
            rounds = 1,
            orderIndex = 0,
            notes = "unbroken first set"
        )
    }

    private fun sampleResult(): WodResult {
        return WodResult(
            id = 1L,
            wodId = 3L,
            scoreType = ScoreType.TIME,
            timeSeconds = 320,
            rxStatus = RxStatus.RX,
            rpe = 8,
            condition = Condition.GOOD,
            memo = "paced well",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun sampleSummary(): AnalysisSummary {
        return AnalysisSummary(
            items = listOf(
                summaryItem(ComparisonLabel.Older, 1L, LocalDate.of(2026, 4, 30), "Older WOD", 90),
                summaryItem(ComparisonLabel.Previous, 2L, LocalDate.of(2026, 5, 2), "Previous WOD", 100),
                summaryItem(ComparisonLabel.Current, 3L, LocalDate.of(2026, 5, 4), "Fran", 110)
            ),
            categoryBreakdown = listOf(
                CategoryShare(MovementCategory.STRENGTH, count = 2, ratio = 2.0 / 3.0),
                CategoryShare(MovementCategory.CARDIO, count = 1, ratio = 1.0 / 3.0)
            ),
            neutralSummary = listOf("정량 지표만 요약했습니다."),
            hasEnoughDataForComparison = true
        )
    }

    private fun summaryItem(
        label: ComparisonLabel,
        wodId: Long,
        date: LocalDate,
        title: String,
        reps: Int
    ): WodComparisonItem {
        return WodComparisonItem(
            label = label,
            wodId = wodId,
            date = date,
            title = title,
            wodType = WodType.FOR_TIME,
            totalReps = reps,
            totalLoadVolume = 1200.0,
            totalDistance = 0.0,
            totalCalories = 15.0,
            rxStatus = RxStatus.RX,
            rpe = 8,
            movementCategoryCounts = mapOf(MovementCategory.STRENGTH to 1)
        )
    }
}
