package com.wodlog.app.domain.backup

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodSourceType
import com.wodlog.app.domain.model.WodType
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackupMappersTest {
    private val instant = Instant.parse("2026-05-04T10:15:30Z")

    @Test
    fun `domain values round trip through backup models`() {
        val profile = UserProfile(
            id = 1L,
            heightCm = 180.0,
            weightKg = 82.5,
            crossfitStartDate = LocalDate.parse("2025-01-06"),
            createdAt = instant,
            updatedAt = instant,
        )
        val wod = Wod(
            id = 10L,
            date = LocalDate.parse("2026-05-04"),
            title = "Fran",
            type = WodType.FOR_TIME,
            rawText = "21-15-9",
            notes = "steady",
            sourceType = WodSourceType.NAVER_CAFE_WEBVIEW,
            sourceUrl = "https://cafe.naver.com/box/123",
            importedAt = instant.minusSeconds(60),
            createdAt = instant,
            updatedAt = instant,
        )
        val section = WodSection(id = 20L, wodId = wod.id, name = "Metcon", orderIndex = 0)
        val movement = Movement(
            id = 30L,
            wodId = wod.id,
            sectionId = section.id,
            name = "Thruster",
            category = MovementCategory.WEIGHTLIFTING,
            weightKg = 43.0,
            reps = 45,
            sets = 1,
            rounds = 1,
            distanceMeters = 0.0,
            calories = 0.0,
            durationSeconds = 320,
            orderIndex = 0,
            notes = "unbroken first set",
        )
        val result = WodResult(
            id = 40L,
            wodId = wod.id,
            scoreType = ScoreType.TIME,
            timeSeconds = 320,
            rounds = 3,
            extraReps = 0,
            totalReps = 90,
            loadKg = 43.0,
            distanceMeters = null,
            calories = null,
            rxStatus = RxStatus.RX,
            rpe = 8,
            condition = Condition.GOOD,
            memo = "paced well",
            createdAt = instant,
            updatedAt = instant,
        )
        val lifestyle = LifestyleLog(
            id = 50L,
            weekStartDate = LocalDate.parse("2026-05-04"),
            mealSummary = "balanced",
            alcohol = true,
            alcoholAmountPerWeek = "2 beers",
            smoking = false,
            smokingAmountPerWeek = null,
            sleepAverageHours = 7.5,
            notes = "normal week",
            createdAt = instant,
            updatedAt = instant,
        )
        val report = AiReport(
            id = 60L,
            targetWodId = wod.id,
            promptText = "prompt",
            reportText = "answer",
            userMemo = "saved",
            createdAt = instant,
            updatedAt = instant,
        )

        assertEquals(profile, profile.toBackup().toDomain())
        assertEquals(wod, wod.toBackup().toDomain())
        assertEquals(section, section.toBackup().toDomain())
        assertEquals(movement, movement.toBackup().toDomain())
        assertEquals(result, result.toBackup().toDomain())
        assertEquals(lifestyle, lifestyle.toBackup().toDomain())
        assertEquals(report, report.toBackup().toDomain())
    }

    @Test
    fun `relationship ids and nullable values are preserved`() {
        val movement = Movement(
            id = 3L,
            wodId = 1L,
            sectionId = null,
            name = "Run",
            category = null,
            orderIndex = 2,
        )
        val profile = UserProfile(
            id = 1L,
            heightCm = null,
            weightKg = null,
            crossfitStartDate = null,
            createdAt = instant,
            updatedAt = instant,
        )

        val movementDomain = movement.toBackup().toDomain()
        val profileDomain = profile.toBackup().toDomain()

        assertEquals(1L, movementDomain.wodId)
        assertNull(movementDomain.sectionId)
        assertNull(movementDomain.category)
        assertNull(profileDomain.crossfitStartDate)
        assertNull(profileDomain.heightCm)
        assertNull(profileDomain.weightKg)
    }
}
