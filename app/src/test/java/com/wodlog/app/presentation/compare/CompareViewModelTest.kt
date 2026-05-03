package com.wodlog.app.presentation.compare

import com.wodlog.app.domain.analysis.AnalysisSummary
import com.wodlog.app.domain.analysis.CategoryShare
import com.wodlog.app.domain.analysis.ComparisonLabel
import com.wodlog.app.domain.analysis.WodAnalysisInput
import com.wodlog.app.domain.analysis.WodComparisonItem
import com.wodlog.app.domain.model.AiReport
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class CompareViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadComparison_withoutWods_setsEmptyState() = runTest {
        val viewModel = CompareViewModel(FakeWodlogRepository())

        viewModel.loadComparison()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
        assertFalse(state.isLoading)
        assertEquals(0, state.wodCount)
        assertNull(state.summary)
    }

    @Test
    fun loadComparison_withSingleWod_createsSummary() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(wod(1)),
            movementsByWodId = mapOf(1L to listOf(movement(wodId = 1L, reps = 10)))
        )
        val viewModel = CompareViewModel(repository)

        viewModel.loadComparison()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isEmpty)
        assertEquals(1, state.wodCount)
        assertEquals(1, requireNotNull(state.summary).items.size)
        assertFalse(state.summary.hasEnoughDataForComparison)
    }

    @Test
    fun loadComparison_withThreeWods_putsGeneratorSummaryInState() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(wod(1), wod(2), wod(3))
        )
        val expectedSummary = summaryFor(wodId = 99L)
        val viewModel = CompareViewModel(
            repository = repository,
            summaryGenerator = { expectedSummary }
        )

        viewModel.loadComparison()
        advanceUntilIdle()

        assertSame(expectedSummary, viewModel.uiState.value.summary)
        assertEquals(3, viewModel.uiState.value.wodCount)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun loadComparison_queriesMovementsAndResultForEachWod() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(wod(1), wod(2), wod(3)),
            movementsByWodId = mapOf(
                1L to listOf(movement(wodId = 1L)),
                2L to listOf(movement(wodId = 2L)),
                3L to listOf(movement(wodId = 3L))
            ),
            resultsByWodId = mapOf(2L to result(wodId = 2L))
        )
        var capturedInputs: List<WodAnalysisInput> = emptyList()
        val viewModel = CompareViewModel(
            repository = repository,
            summaryGenerator = { inputs ->
                capturedInputs = inputs
                summaryFor(wodId = inputs.last().wod.id)
            }
        )

        viewModel.loadComparison()
        advanceUntilIdle()

        assertEquals(listOf(1L, 2L, 3L), repository.movementLookupIds)
        assertEquals(listOf(1L, 2L, 3L), repository.resultLookupIds)
        assertEquals(3, capturedInputs.size)
        assertEquals(1, capturedInputs.first { it.wod.id == 1L }.movements.size)
        assertEquals(RxStatus.RX, capturedInputs.first { it.wod.id == 2L }.result?.rxStatus)
    }

    @Test
    fun loadComparison_whenRepositoryThrows_setsErrorMessage() = runTest {
        val viewModel = CompareViewModel(
            FakeWodlogRepository(throwOnRecentWods = true)
        )

        viewModel.loadComparison()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("recent failed", state.errorMessage)
        assertFalse(state.isEmpty)
        assertNull(state.summary)
    }

    @Test
    fun refresh_loadsComparisonAgain() = runTest {
        val repository = FakeWodlogRepository(wods = listOf(wod(1)))
        val viewModel = CompareViewModel(repository)

        viewModel.loadComparison()
        advanceUntilIdle()
        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(2, repository.recentWodsCallCount)
    }

    @Test
    fun loadComparison_doesNotTransformGeneratorResult() = runTest {
        val expectedSummary = summaryFor(wodId = 7L).copy(
            neutralSummary = listOf("custom neutral line"),
            categoryBreakdown = listOf(
                CategoryShare(MovementCategory.CARDIO, count = 2, ratio = 1.0)
            )
        )
        val viewModel = CompareViewModel(
            repository = FakeWodlogRepository(wods = listOf(wod(7))),
            summaryGenerator = { expectedSummary }
        )

        viewModel.loadComparison()
        advanceUntilIdle()

        assertSame(expectedSummary, viewModel.uiState.value.summary)
    }

    private fun wod(id: Long): Wod {
        return Wod(
            id = id,
            date = LocalDate.of(2026, 5, id.toInt().coerceAtLeast(1)),
            title = "WOD $id",
            type = WodType.FOR_TIME,
            createdAt = NOW.plusSeconds(id),
            updatedAt = NOW.plusSeconds(id)
        )
    }

    private fun movement(
        wodId: Long,
        reps: Int? = null
    ): Movement {
        return Movement(
            wodId = wodId,
            name = "Movement",
            category = MovementCategory.CARDIO,
            reps = reps,
            orderIndex = 0
        )
    }

    private fun result(wodId: Long): WodResult {
        return WodResult(
            wodId = wodId,
            scoreType = ScoreType.TIME,
            rxStatus = RxStatus.RX,
            rpe = 8,
            createdAt = NOW,
            updatedAt = NOW
        )
    }

    private fun summaryFor(wodId: Long): AnalysisSummary {
        return AnalysisSummary(
            items = listOf(
                WodComparisonItem(
                    label = ComparisonLabel.Current,
                    wodId = wodId,
                    date = LocalDate.of(2026, 5, 1),
                    title = "Summary WOD",
                    wodType = WodType.FOR_TIME,
                    totalReps = 10,
                    totalLoadVolume = 100.0,
                    totalDistance = 0.0,
                    totalCalories = 0.0,
                    rxStatus = RxStatus.RX,
                    rpe = 8,
                    movementCategoryCounts = mapOf(MovementCategory.CARDIO to 1)
                )
            ),
            categoryBreakdown = emptyList(),
            neutralSummary = listOf("neutral"),
            hasEnoughDataForComparison = false
        )
    }

    private companion object {
        val NOW: Instant = Instant.parse("2026-05-04T00:00:00Z")
    }
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

private class FakeWodlogRepository(
    private val wods: List<Wod> = emptyList(),
    private val movementsByWodId: Map<Long, List<Movement>> = emptyMap(),
    private val resultsByWodId: Map<Long, WodResult> = emptyMap(),
    private val throwOnRecentWods: Boolean = false
) : WodlogRepository {
    var recentWodsCallCount = 0
    val movementLookupIds = mutableListOf<Long>()
    val resultLookupIds = mutableListOf<Long>()

    override suspend fun getRecentWods(limit: Int): List<Wod> {
        recentWodsCallCount += 1
        if (throwOnRecentWods) error("recent failed")
        return wods.take(limit)
    }

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> {
        movementLookupIds += wodId
        return movementsByWodId[wodId].orEmpty()
    }

    override suspend fun getResultForWod(wodId: Long): WodResult? {
        resultLookupIds += wodId
        return resultsByWodId[wodId]
    }

    override suspend fun getUserProfile(): UserProfile? = unused()

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodById(id: Long): Wod? = unused()

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> = unused()

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = unused()

    override suspend fun saveWod(wod: Wod): Long = unused()

    override suspend fun deleteWod(id: Long): Unit = unused()

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> = unused()

    override suspend fun saveWodSection(section: WodSection): Long = unused()

    override suspend fun deleteWodSection(id: Long): Unit = unused()

    override suspend fun saveMovement(movement: Movement): Long = unused()

    override suspend fun deleteMovement(id: Long): Unit = unused()

    override suspend fun saveWodResult(result: WodResult): Long = unused()

    override suspend fun deleteWodResult(id: Long): Unit = unused()

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? = unused()

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long = unused()

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by CompareViewModel tests.")
    }
}
