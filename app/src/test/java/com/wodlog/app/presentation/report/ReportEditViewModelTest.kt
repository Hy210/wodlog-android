package com.wodlog.app.presentation.report

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.repository.WodlogRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class ReportEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = Instant.parse("2026-05-04T00:00:00Z")
    private val later = Instant.parse("2026-05-04T01:00:00Z")

    @Test
    fun loadReports_fillsReportsForWod() = runTest {
        val repository = FakeReportRepository(
            reports = mutableListOf(
                report(id = 1L, wodId = 7L, text = "First report"),
                report(id = 2L, wodId = 8L, text = "Other report")
            )
        )
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })

        viewModel.loadReports(7L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(7L, state.wodId)
        assertEquals(listOf(1L), state.reports.map { it.id })
        assertFalse(state.isLoading)
    }

    @Test
    fun onAnswerChange_updatesState() {
        val viewModel = ReportEditViewModel(FakeReportRepository(), nowProvider = { now })

        viewModel.onAnswerChange("Copied answer")

        assertEquals("Copied answer", viewModel.uiState.value.answerInput)
    }

    @Test
    fun saveReport_withBlankAnswer_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeReportRepository()
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.loadReports(7L)
        advanceUntilIdle()

        viewModel.saveReport()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ReportEditViewModel.ERROR_BLANK_ANSWER))
        assertEquals(0, repository.saveCount)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun saveReport_withInvalidWodId_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeReportRepository()
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.onAnswerChange("Answer")

        viewModel.saveReport()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ReportEditViewModel.ERROR_INVALID_WOD_ID))
        assertEquals(0, repository.saveCount)
    }

    @Test
    fun saveReport_withNewAnswer_savesReportAndRefreshesList() = runTest {
        val repository = FakeReportRepository()
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.loadReports(7L)
        advanceUntilIdle()
        viewModel.onAnswerChange("New GPT answer")

        viewModel.saveReport()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, repository.saveCount)
        assertEquals(1L, state.selectedReportId)
        assertEquals("New GPT answer", state.answerInput)
        assertEquals(listOf("New GPT answer"), state.reports.map { it.reportText })
        assertEquals("Report saved", state.message)
        assertFalse(state.isSaving)
    }

    @Test
    fun selectReport_fillsAnswerInputAndSelectedReportId() = runTest {
        val repository = FakeReportRepository(
            reports = mutableListOf(report(id = 5L, wodId = 7L, text = "Saved answer"))
        )
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.loadReports(7L)
        advanceUntilIdle()

        viewModel.selectReport(5L)

        val state = viewModel.uiState.value
        assertEquals(5L, state.selectedReportId)
        assertEquals("Saved answer", state.answerInput)
    }

    @Test
    fun saveReport_whenReportSelected_keepsExistingIdAndCreatedAt() = runTest {
        val existing = report(id = 5L, wodId = 7L, text = "Old answer", createdAt = now)
        val repository = FakeReportRepository(reports = mutableListOf(existing))
        val viewModel = ReportEditViewModel(repository, nowProvider = { later })
        viewModel.loadReports(7L)
        advanceUntilIdle()
        viewModel.selectReport(5L)
        viewModel.onAnswerChange("Updated answer")

        viewModel.saveReport()
        advanceUntilIdle()

        val savedReport = repository.reports.single()
        assertEquals(5L, savedReport.id)
        assertEquals(now, savedReport.createdAt)
        assertEquals(later, savedReport.updatedAt)
        assertEquals("Updated answer", savedReport.reportText)
        assertEquals(5L, viewModel.uiState.value.selectedReportId)
    }

    @Test
    fun clearSelection_clearsSelectedReportAndAnswerInput() = runTest {
        val repository = FakeReportRepository(
            reports = mutableListOf(report(id = 5L, wodId = 7L, text = "Saved answer"))
        )
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.loadReports(7L)
        advanceUntilIdle()
        viewModel.selectReport(5L)

        viewModel.clearSelection()

        val state = viewModel.uiState.value
        assertEquals(null, state.selectedReportId)
        assertEquals("", state.answerInput)
    }

    @Test
    fun deleteReport_deletesFromRepositoryAndRefreshesList() = runTest {
        val repository = FakeReportRepository(
            reports = mutableListOf(
                report(id = 5L, wodId = 7L, text = "Saved answer"),
                report(id = 6L, wodId = 7L, text = "Another answer")
            )
        )
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })
        viewModel.loadReports(7L)
        advanceUntilIdle()
        viewModel.selectReport(5L)

        viewModel.deleteReport(5L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(6L), state.reports.map { it.id })
        assertEquals(null, state.selectedReportId)
        assertEquals("", state.answerInput)
        assertEquals("Report deleted", state.message)
        assertEquals(1, repository.deleteCount)
        assertFalse(state.isDeleting)
    }

    @Test
    fun repositoryException_setsErrorMessage() = runTest {
        val repository = FakeReportRepository(throwOnLoad = true)
        val viewModel = ReportEditViewModel(repository, nowProvider = { now })

        viewModel.loadReports(7L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("load failed", state.errorMessage)
        assertFalse(state.isLoading)
    }

    private fun report(
        id: Long,
        wodId: Long,
        text: String,
        createdAt: Instant = now
    ): AiReport = AiReport(
        id = id,
        targetWodId = wodId,
        promptText = null,
        reportText = text,
        userMemo = null,
        createdAt = createdAt,
        updatedAt = createdAt
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeReportRepository(
    val reports: MutableList<AiReport> = mutableListOf(),
    private val throwOnLoad: Boolean = false
) : WodlogRepository {
    var saveCount = 0
    var deleteCount = 0
    private var nextId = (reports.maxOfOrNull { it.id } ?: 0L) + 1L

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> {
        if (throwOnLoad) error("load failed")
        return reports.filter { it.targetWodId == wodId }
    }

    override suspend fun saveAiReport(report: AiReport): Long {
        saveCount += 1
        val id = report.id.takeIf { it != 0L } ?: nextId++
        reports.removeAll { it.id == id }
        reports += report.copy(id = id)
        return id
    }

    override suspend fun deleteAiReport(id: Long) {
        deleteCount += 1
        reports.removeAll { it.id == id }
    }

    override suspend fun getUserProfile(): UserProfile? = unused()

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodById(id: Long): Wod? = unused()

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> = unused()

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = unused()

    override suspend fun getRecentWods(limit: Int): List<Wod> = unused()

    override suspend fun saveWod(wod: Wod): Long = unused()

    override suspend fun deleteWod(id: Long): Unit = unused()

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> = unused()

    override suspend fun saveWodSection(section: WodSection): Long = unused()

    override suspend fun deleteWodSection(id: Long): Unit = unused()

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> = unused()

    override suspend fun saveMovement(movement: Movement): Long = unused()

    override suspend fun deleteMovement(id: Long): Unit = unused()

    override suspend fun getResultForWod(wodId: Long): WodResult? = unused()

    override suspend fun saveWodResult(result: WodResult): Long = unused()

    override suspend fun deleteWodResult(id: Long): Unit = unused()

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? = unused()

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by ReportEditViewModel tests.")
    }
}
