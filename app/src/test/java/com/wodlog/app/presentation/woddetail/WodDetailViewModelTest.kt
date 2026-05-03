package com.wodlog.app.presentation.woddetail

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class WodDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = Instant.parse("2026-05-03T00:00:00Z")

    @Test
    fun loadWod_whenWodExists_fillsDetailState() = runTest {
        val repository = FakeWodlogRepository(
            wod = Wod(
                id = 1L,
                date = LocalDate.of(2026, 5, 3),
                title = "Fran",
                type = WodType.FOR_TIME,
                rawText = "21-15-9",
                notes = "benchmark",
                createdAt = now,
                updatedAt = now
            ),
            sections = listOf(
                WodSection(id = 10L, wodId = 1L, name = "Metcon", orderIndex = 0)
            ),
            movements = listOf(
                Movement(
                    id = 20L,
                    wodId = 1L,
                    sectionId = 10L,
                    name = "Thruster",
                    category = MovementCategory.WEIGHTLIFTING,
                    reps = 21,
                    orderIndex = 0
                )
            )
        )
        val viewModel = WodDetailViewModel(repository)

        viewModel.loadWod(1L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Fran", state.wod?.title)
        assertEquals("Metcon", state.sections.single().name)
        assertEquals("Thruster", state.movements.single().name)
        assertNull(state.result)
        assertTrue(state.aiReports.isEmpty())
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadWod_whenWodDoesNotExist_setsErrorState() = runTest {
        val viewModel = WodDetailViewModel(FakeWodlogRepository())

        viewModel.loadWod(404L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.wod)
        assertEquals("WOD not found", state.errorMessage)
        assertFalse(state.isLoading)
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
    private val wod: Wod? = null,
    private val sections: List<WodSection> = emptyList(),
    private val movements: List<Movement> = emptyList(),
    private val result: WodResult? = null,
    private val reports: List<AiReport> = emptyList()
) : WodlogRepository {
    override suspend fun getWodById(id: Long): Wod? = wod?.takeIf { it.id == id }

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> =
        sections.filter { it.wodId == wodId }

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> =
        movements.filter { it.wodId == wodId }

    override suspend fun getResultForWod(wodId: Long): WodResult? =
        result?.takeIf { it.wodId == wodId }

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> =
        reports.filter { it.targetWodId == wodId }

    override suspend fun getUserProfile(): UserProfile? = unused()

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> = unused()

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = unused()

    override suspend fun getRecentWods(limit: Int): List<Wod> = unused()

    override suspend fun saveWod(wod: Wod): Long = unused()

    override suspend fun deleteWod(id: Long): Unit = unused()

    override suspend fun saveWodSection(section: WodSection): Long = unused()

    override suspend fun deleteWodSection(id: Long): Unit = unused()

    override suspend fun saveMovement(movement: Movement): Long = unused()

    override suspend fun deleteMovement(id: Long): Unit = unused()

    override suspend fun saveWodResult(result: WodResult): Long = unused()

    override suspend fun deleteWodResult(id: Long): Unit = unused()

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? = unused()

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by WodDetailViewModel tests.")
    }
}
