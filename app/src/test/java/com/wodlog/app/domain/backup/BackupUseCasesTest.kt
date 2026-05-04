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
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.domain.repository.WodlogRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupUseCasesTest {
    private val instant = Instant.parse("2026-05-04T10:15:30Z")

    @Test
    fun `exportJson gathers repository data into backup json`() = runTest {
        val repository = FakeBackupRepository()
        val useCase = BackupExportUseCase(
            repository = repository,
            exportedAtProvider = { instant },
        )

        val decoded = BackupJsonSerializer.decode(useCase.exportJson())

        assertEquals("2026-05-04T10:15:30Z", decoded.exportedAt)
        assertEquals(1L, decoded.profile?.id)
        assertEquals(2, decoded.wods.size)
        assertEquals(1, decoded.sections.size)
        assertEquals(2, decoded.movements.size)
        assertEquals(1, decoded.results.size)
        assertEquals(1, decoded.lifestyleLogs.size)
        assertEquals(1, decoded.aiReports.size)
        assertEquals(0, repository.writeCallCount)
    }

    @Test
    fun `exportJson works when profile is missing`() = runTest {
        val repository = FakeBackupRepository(profile = null)
        val useCase = BackupExportUseCase(
            repository = repository,
            exportedAtProvider = { instant },
        )

        val decoded = BackupJsonSerializer.decode(useCase.exportJson())

        assertNull(decoded.profile)
        assertEquals(2, decoded.wods.size)
    }

    @Test
    fun `preview returns valid counts for normal backup json`() {
        val json = BackupJsonSerializer.encode(sampleBackup())

        val preview = BackupImportPreviewUseCase().preview(json)

        assertTrue(preview.isValid)
        assertEquals(emptyList<BackupImportError>(), preview.errors)
        assertEquals(1, preview.wodCount)
        assertEquals(1, preview.movementCount)
        assertEquals(1, preview.resultCount)
        assertEquals(1, preview.lifestyleLogCount)
        assertEquals(1, preview.aiReportCount)
    }

    @Test
    fun `preview returns invalid for malformed json`() {
        val preview = BackupImportPreviewUseCase().preview("{ invalid json")

        assertFalse(preview.isValid)
        assertNull(preview.backup)
        assertEquals(BackupImportErrorType.INVALID_JSON, preview.errors.single().type)
    }

    @Test
    fun `preview returns invalid for unsupported version`() {
        val json = BackupJsonSerializer.encode(sampleBackup().copy(version = 99))

        val preview = BackupImportPreviewUseCase().preview(json)

        assertFalse(preview.isValid)
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.UNSUPPORTED_VERSION })
    }

    @Test
    fun `preview returns invalid for duplicate wod ids`() {
        val backup = sampleBackup().copy(
            wods = listOf(sampleWod().toBackup(), sampleWod().copy(title = "Duplicate").toBackup()),
        )

        val preview = BackupImportPreviewUseCase().preview(BackupJsonSerializer.encode(backup))

        assertFalse(preview.isValid)
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.DUPLICATE_WOD_ID })
    }

    @Test
    fun `preview returns invalid for orphan references`() {
        val backup = sampleBackup().copy(
            sections = listOf(sampleSection().copy(wodId = 999L).toBackup()),
            movements = listOf(sampleMovement().copy(wodId = 999L).toBackup()),
            results = listOf(sampleResult().copy(wodId = 999L).toBackup()),
            aiReports = listOf(sampleReport().copy(targetWodId = 999L).toBackup()),
        )

        val preview = BackupImportPreviewUseCase().preview(BackupJsonSerializer.encode(backup))

        assertFalse(preview.isValid)
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.ORPHAN_SECTION })
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.ORPHAN_MOVEMENT })
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.ORPHAN_RESULT })
        assertTrue(preview.errors.any { it.type == BackupImportErrorType.ORPHAN_AI_REPORT })
    }

    @Test
    fun `apply returns failure and does not write when preview is invalid`() = runTest {
        val repository = FakeBackupRepository()
        val useCase = BackupImportApplyUseCase(repository)

        val result = useCase.apply("{ invalid json")

        assertFalse(result.isSuccess)
        assertEquals(BackupImportErrorType.INVALID_JSON, result.errors.single().type)
        assertEquals(0, repository.writeCallCount)
    }

    @Test
    fun `apply saves valid backup data in relationship order`() = runTest {
        val repository = FakeBackupRepository()
        val useCase = BackupImportApplyUseCase(repository)

        val result = useCase.apply(sampleBackup())

        assertTrue(result.isSuccess)
        assertEquals(1, result.importedWodCount)
        assertEquals(1, result.importedMovementCount)
        assertEquals(1, result.importedResultCount)
        assertEquals(1, result.importedLifestyleLogCount)
        assertEquals(1, result.importedAiReportCount)
        assertEquals(
            listOf("profile", "wod", "section", "movement", "result", "lifestyle", "report"),
            repository.savedOperations
        )
    }

    @Test
    fun `apply returns failure when repository save throws`() = runTest {
        val repository = FakeBackupRepository(failOnSave = true)
        val useCase = BackupImportApplyUseCase(repository)

        val result = useCase.apply(sampleBackup())

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.type == BackupImportErrorType.IMPORT_FAILED })
    }

    private fun sampleBackup(): WodlogBackup =
        createWodlogBackup(
            exportedAt = instant,
            profile = sampleProfile(),
            wods = listOf(sampleWod()),
            sections = listOf(sampleSection()),
            movements = listOf(sampleMovement()),
            results = listOf(sampleResult()),
            lifestyleLogs = listOf(sampleLifestyle()),
            aiReports = listOf(sampleReport()),
        )

    private fun sampleProfile(): UserProfile =
        UserProfile(
            id = 1L,
            heightCm = 180.0,
            weightKg = 82.5,
            crossfitStartDate = LocalDate.parse("2025-01-06"),
            createdAt = instant,
            updatedAt = instant,
        )

    private fun sampleWod(id: Long = 10L): Wod =
        Wod(
            id = id,
            date = LocalDate.parse("2026-05-04"),
            title = "Fran",
            type = WodType.FOR_TIME,
            rawText = "21-15-9",
            notes = "steady",
            createdAt = instant,
            updatedAt = instant,
        )

    private fun sampleSection(): WodSection =
        WodSection(id = 20L, wodId = 10L, name = "Metcon", orderIndex = 0)

    private fun sampleMovement(id: Long = 30L, wodId: Long = 10L): Movement =
        Movement(
            id = id,
            wodId = wodId,
            sectionId = 20L,
            name = "Thruster",
            category = MovementCategory.WEIGHTLIFTING,
            weightKg = 43.0,
            reps = 45,
            sets = 1,
            rounds = 1,
            orderIndex = 0,
        )

    private fun sampleResult(): WodResult =
        WodResult(
            id = 40L,
            wodId = 10L,
            scoreType = ScoreType.TIME,
            timeSeconds = 320,
            rxStatus = RxStatus.RX,
            rpe = 8,
            condition = Condition.GOOD,
            memo = "paced",
            createdAt = instant,
            updatedAt = instant,
        )

    private fun sampleLifestyle(): LifestyleLog =
        LifestyleLog(
            id = 50L,
            weekStartDate = LocalDate.parse("2026-05-04"),
            mealSummary = "balanced",
            alcohol = false,
            smoking = false,
            sleepAverageHours = 7.5,
            createdAt = instant,
            updatedAt = instant,
        )

    private fun sampleReport(): AiReport =
        AiReport(
            id = 60L,
            targetWodId = 10L,
            promptText = "prompt",
            reportText = "answer",
            userMemo = "saved",
            createdAt = instant,
            updatedAt = instant,
        )

    private inner class FakeBackupRepository(
        private val profile: UserProfile? = sampleProfile(),
        private val failOnSave: Boolean = false,
    ) : WodlogRepository {
        var writeCallCount = 0
        val savedOperations = mutableListOf<String>()

        override suspend fun getUserProfile(): UserProfile? = profile

        override suspend fun saveUserProfile(profile: UserProfile): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "profile"
            return profile.id
        }

        override suspend fun getWodById(id: Long): Wod? = sampleWod(id)

        override suspend fun getWodsByDate(date: LocalDate): List<Wod> = emptyList()

        override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = emptyList()

        override suspend fun getRecentWods(limit: Int): List<Wod> = getAllWods().take(limit)

        override suspend fun getAllWods(): List<Wod> = listOf(sampleWod(10L), sampleWod(11L))

        override suspend fun saveWod(wod: Wod): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "wod"
            return wod.id
        }

        override suspend fun deleteWod(id: Long) {
            writeCallCount++
        }

        override suspend fun getSectionsForWod(wodId: Long): List<WodSection> =
            if (wodId == 10L) listOf(sampleSection()) else emptyList()

        override suspend fun saveWodSection(section: WodSection): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "section"
            return section.id
        }

        override suspend fun deleteWodSection(id: Long) {
            writeCallCount++
        }

        override suspend fun getMovementsForWod(wodId: Long): List<Movement> =
            listOf(sampleMovement(id = wodId + 20L, wodId = wodId))

        override suspend fun saveMovement(movement: Movement): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "movement"
            return movement.id
        }

        override suspend fun deleteMovement(id: Long) {
            writeCallCount++
        }

        override suspend fun getResultForWod(wodId: Long): WodResult? =
            if (wodId == 10L) sampleResult() else null

        override suspend fun saveWodResult(result: WodResult): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "result"
            return result.id
        }

        override suspend fun deleteWodResult(id: Long) {
            writeCallCount++
        }

        override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? = null

        override suspend fun getAllLifestyleLogs(): List<LifestyleLog> = listOf(sampleLifestyle())

        override suspend fun saveLifestyleLog(log: LifestyleLog): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "lifestyle"
            return log.id
        }

        override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> =
            if (wodId == 10L) listOf(sampleReport()) else emptyList()

        override suspend fun saveAiReport(report: AiReport): Long {
            maybeFail()
            writeCallCount++
            savedOperations += "report"
            return report.id
        }

        override suspend fun deleteAiReport(id: Long) {
            writeCallCount++
        }

        private fun maybeFail() {
            if (failOnSave) error("save failed")
        }
    }
}
