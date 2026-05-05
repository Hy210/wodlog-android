package com.wodlog.app.data.mapper

import com.wodlog.app.data.entity.AiReportEntity
import com.wodlog.app.data.entity.Condition
import com.wodlog.app.data.entity.LifestyleLogEntity
import com.wodlog.app.data.entity.MovementCategory
import com.wodlog.app.data.entity.MovementEntity
import com.wodlog.app.data.entity.RxStatus
import com.wodlog.app.data.entity.ScoreType
import com.wodlog.app.data.entity.UserProfileEntity
import com.wodlog.app.data.entity.WodEntity
import com.wodlog.app.data.entity.WodResultEntity
import com.wodlog.app.data.entity.WodSourceType
import com.wodlog.app.data.entity.WodType
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntityMappersTest {
    private val now = Instant.ofEpochMilli(1_777_777_777_000)

    @Test
    fun wodEntity_toDomain_preservesCoreFields() {
        val entity = WodEntity(
            id = 7L,
            date = LocalDate.of(2026, 5, 3),
            title = "Fran",
            type = WodType.FOR_TIME,
            rawText = "21-15-9",
            notes = "fast",
            sourceType = WodSourceType.NAVER_CAFE_WEBVIEW,
            sourceUrl = "https://cafe.naver.com/box/123",
            importedAt = now.minusSeconds(60),
            createdAt = now,
            updatedAt = now
        )

        val domain = entity.toDomain()

        assertEquals(7L, domain.id)
        assertEquals(LocalDate.of(2026, 5, 3), domain.date)
        assertEquals("Fran", domain.title)
        assertEquals(com.wodlog.app.domain.model.WodType.FOR_TIME, domain.type)
        assertEquals("21-15-9", domain.rawText)
        assertEquals("fast", domain.notes)
        assertEquals(com.wodlog.app.domain.model.WodSourceType.NAVER_CAFE_WEBVIEW, domain.sourceType)
        assertEquals("https://cafe.naver.com/box/123", domain.sourceUrl)
        assertEquals(now.minusSeconds(60), domain.importedAt)
    }

    @Test
    fun wod_toEntity_preservesCoreFields() {
        val domain = com.wodlog.app.domain.model.Wod(
            id = 8L,
            date = LocalDate.of(2026, 5, 4),
            title = "Grace",
            type = com.wodlog.app.domain.model.WodType.OTHER,
            rawText = null,
            notes = null,
            sourceType = com.wodlog.app.domain.model.WodSourceType.NAVER_CAFE_WEBVIEW,
            sourceUrl = "https://cafe.naver.com/box/456",
            importedAt = now.minusSeconds(30),
            createdAt = now,
            updatedAt = now
        )

        val entity = domain.toEntity()

        assertEquals(8L, entity.id)
        assertEquals(LocalDate.of(2026, 5, 4), entity.date)
        assertEquals("Grace", entity.title)
        assertEquals(WodType.OTHER, entity.type)
        assertNull(entity.rawText)
        assertNull(entity.notes)
        assertEquals(WodSourceType.NAVER_CAFE_WEBVIEW, entity.sourceType)
        assertEquals("https://cafe.naver.com/box/456", entity.sourceUrl)
        assertEquals(now.minusSeconds(30), entity.importedAt)
    }

    @Test
    fun movement_roundTrip_preservesMetricsAndNullableFields() {
        val entity = MovementEntity(
            id = 1L,
            wodId = 2L,
            sectionId = null,
            movementName = "Row",
            category = MovementCategory.CARDIO,
            weightKg = 42.5,
            reps = 21,
            sets = 3,
            rounds = 5,
            distanceMeters = 500.0,
            calories = 30.0,
            durationSeconds = null,
            orderIndex = 0,
            notes = null
        )

        val roundTrip = entity.toDomain().toEntity()

        assertEquals(entity.id, roundTrip.id)
        assertEquals(entity.wodId, roundTrip.wodId)
        assertEquals(entity.sectionId, roundTrip.sectionId)
        assertEquals(entity.movementName, roundTrip.movementName)
        assertEquals(entity.category, roundTrip.category)
        assertEquals(entity.weightKg, roundTrip.weightKg)
        assertEquals(entity.reps, roundTrip.reps)
        assertEquals(entity.sets, roundTrip.sets)
        assertEquals(entity.rounds, roundTrip.rounds)
        assertEquals(entity.distanceMeters, roundTrip.distanceMeters)
        assertEquals(entity.calories, roundTrip.calories)
        assertNull(roundTrip.durationSeconds)
        assertNull(roundTrip.notes)
    }

    @Test
    fun wodResult_roundTrip_preservesScoreAndEffortFields() {
        val entity = WodResultEntity(
            id = 3L,
            wodId = 4L,
            scoreType = ScoreType.ROUNDS_REPS,
            timeSeconds = 600,
            rounds = 4,
            extraReps = 12,
            totalReps = 132,
            loadKg = 60.0,
            distanceMeters = null,
            calories = null,
            rxStatus = RxStatus.SCALED,
            rpe = 8,
            condition = Condition.TIRED,
            memo = "hard",
            createdAt = now,
            updatedAt = now
        )

        val roundTrip = entity.toDomain().toEntity()

        assertEquals(entity.scoreType, roundTrip.scoreType)
        assertEquals(entity.timeSeconds, roundTrip.timeSeconds)
        assertEquals(entity.rounds, roundTrip.rounds)
        assertEquals(entity.extraReps, roundTrip.extraReps)
        assertEquals(entity.totalReps, roundTrip.totalReps)
        assertEquals(entity.rxStatus, roundTrip.rxStatus)
        assertEquals(entity.rpe, roundTrip.rpe)
        assertEquals(entity.condition, roundTrip.condition)
        assertNull(roundTrip.distanceMeters)
        assertNull(roundTrip.calories)
    }

    @Test
    fun userProfileEntity_roundTrip_preservesBodyInfo() {
        val entity = UserProfileEntity(
            id = 1L,
            heightCm = 180.0,
            weightKg = 82.5,
            crossfitStartDate = LocalDate.of(2025, 1, 1),
            createdAt = now,
            updatedAt = now
        )

        val roundTrip = entity.toDomain().toEntity()

        assertEquals(entity.heightCm, roundTrip.heightCm)
        assertEquals(entity.weightKg, roundTrip.weightKg)
        assertEquals(entity.crossfitStartDate, roundTrip.crossfitStartDate)
    }

    @Test
    fun lifestyleLog_roundTrip_preservesLifestyleFields() {
        val entity = LifestyleLogEntity(
            id = 5L,
            weekStartDate = LocalDate.of(2026, 4, 27),
            mealSummary = "balanced",
            alcohol = true,
            alcoholAmountPerWeek = "2 beers",
            smoking = false,
            smokingAmountPerWeek = null,
            sleepAverageHours = 7.5,
            notes = "felt good",
            createdAt = now,
            updatedAt = now
        )

        val roundTrip = entity.toDomain().toEntity()

        assertEquals(entity.weekStartDate, roundTrip.weekStartDate)
        assertEquals(entity.mealSummary, roundTrip.mealSummary)
        assertEquals(entity.alcohol, roundTrip.alcohol)
        assertEquals(entity.alcoholAmountPerWeek, roundTrip.alcoholAmountPerWeek)
        assertEquals(entity.smoking, roundTrip.smoking)
        assertNull(roundTrip.smokingAmountPerWeek)
        assertEquals(entity.sleepAverageHours, roundTrip.sleepAverageHours)
        assertEquals(entity.notes, roundTrip.notes)
    }

    @Test
    fun aiReport_roundTrip_preservesPromptAndReportFields() {
        val entity = AiReportEntity(
            id = 6L,
            targetWodId = 9L,
            promptText = "prompt",
            reportText = "report",
            userMemo = null,
            createdAt = now,
            updatedAt = now
        )

        val roundTrip = entity.toDomain().toEntity()

        assertEquals(entity.id, roundTrip.id)
        assertEquals(entity.targetWodId, roundTrip.targetWodId)
        assertEquals(entity.promptText, roundTrip.promptText)
        assertEquals(entity.reportText, roundTrip.reportText)
        assertNull(roundTrip.userMemo)
    }
}
