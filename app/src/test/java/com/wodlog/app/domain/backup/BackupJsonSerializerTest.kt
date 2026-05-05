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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupJsonSerializerTest {
    private val instant = Instant.parse("2026-05-04T10:15:30Z")

    @Test
    fun `encode includes root version and all backup collections`() {
        val json = BackupJsonSerializer.encode(sampleBackup())

        assertTrue(json.contains("\"version\""))
        assertTrue(json.contains("1"))
        assertTrue(json.contains("\"profile\""))
        assertTrue(json.contains("\"wods\""))
        assertTrue(json.contains("\"sections\""))
        assertTrue(json.contains("\"movements\""))
        assertTrue(json.contains("\"results\""))
        assertTrue(json.contains("\"lifestyleLogs\""))
        assertTrue(json.contains("\"aiReports\""))
    }

    @Test
    fun `encode then decode preserves important fields`() {
        val decoded = BackupJsonSerializer.decode(BackupJsonSerializer.encode(sampleBackup()))

        assertEquals(WodlogBackup.BACKUP_VERSION, decoded.version)
        assertEquals("2026-05-04T10:15:30Z", decoded.exportedAt)
        assertEquals(180.0, decoded.profile?.heightCm ?: error("heightCm missing"), 0.0)
        assertEquals(10L, decoded.wods.first().id)
        assertEquals(WodSourceType.NAVER_CAFE_WEBVIEW, decoded.wods.first().sourceType)
        assertEquals("https://cafe.naver.com/box/123", decoded.wods.first().sourceUrl)
        assertEquals("2026-05-04T10:15:30Z", decoded.wods.first().importedAt)
        assertEquals(10L, decoded.sections.first().wodId)
        assertEquals(10L, decoded.movements.first().wodId)
        assertEquals(20L, decoded.movements.first().sectionId)
        assertEquals(ScoreType.TIME, decoded.results.first().scoreType)
        assertEquals(RxStatus.RX, decoded.results.first().rxStatus)
        assertEquals(8, decoded.results.first().rpe)
        assertEquals(true, decoded.lifestyleLogs.first().alcohol)
        assertEquals(false, decoded.lifestyleLogs.first().smoking)
        assertEquals(7.5, decoded.lifestyleLogs.first().sleepAverageHours ?: error("sleep missing"), 0.0)
        assertEquals(10L, decoded.aiReports.first().targetWodId)
        assertEquals("GPT answer", decoded.aiReports.first().reportText)
    }

    @Test
    fun `optional null values round trip through json`() {
        val backup = sampleBackup().copy(
            profile = sampleBackup().profile?.copy(crossfitStartDate = null),
            movements = listOf(sampleBackup().movements.first().copy(sectionId = null, category = null)),
            results = listOf(sampleBackup().results.first().copy(condition = null, memo = null)),
        )

        val decoded = BackupJsonSerializer.decode(BackupJsonSerializer.encode(backup))

        assertEquals(null, decoded.profile?.crossfitStartDate)
        assertEquals(null, decoded.movements.first().sectionId)
        assertEquals(null, decoded.movements.first().category)
        assertEquals(null, decoded.results.first().condition)
        assertEquals(null, decoded.results.first().memo)
    }

    @Test
    fun `invalid json decode fails`() {
        assertThrows(Exception::class.java) {
            BackupJsonSerializer.decode("{ invalid json")
        }
    }

    @Test
    fun `backup json does not include unnecessary sensitive field names`() {
        val json = BackupJsonSerializer.encode(sampleBackup()).lowercase()

        assertFalse(json.contains("apikey"))
        assertFalse(json.contains("api_key"))
        assertFalse(json.contains("token"))
        assertFalse(json.contains("password"))
    }

    private fun sampleBackup(): WodlogBackup {
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
            importedAt = instant,
            createdAt = instant,
            updatedAt = instant,
        )
        val section = WodSection(id = 20L, wodId = 10L, name = "Metcon", orderIndex = 0)
        val movement = Movement(
            id = 30L,
            wodId = 10L,
            sectionId = 20L,
            name = "Thruster",
            category = MovementCategory.WEIGHTLIFTING,
            weightKg = 43.0,
            reps = 45,
            sets = 1,
            rounds = 1,
            distanceMeters = null,
            calories = null,
            durationSeconds = 320,
            orderIndex = 0,
            notes = "fast",
        )
        val result = WodResult(
            id = 40L,
            wodId = 10L,
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
            memo = "paced",
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
            notes = "normal",
            createdAt = instant,
            updatedAt = instant,
        )
        val report = AiReport(
            id = 60L,
            targetWodId = 10L,
            promptText = "prompt",
            reportText = "GPT answer",
            userMemo = "saved",
            createdAt = instant,
            updatedAt = instant,
        )

        return createWodlogBackup(
            exportedAt = instant,
            profile = profile,
            wods = listOf(wod),
            sections = listOf(section),
            movements = listOf(movement),
            results = listOf(result),
            lifestyleLogs = listOf(lifestyle),
            aiReports = listOf(report),
        )
    }
}
