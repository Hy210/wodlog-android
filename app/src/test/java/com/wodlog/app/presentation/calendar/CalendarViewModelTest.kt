package com.wodlog.app.presentation.calendar

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 5, 3)
    private val now = Instant.parse("2026-05-03T00:00:00Z")

    @Test
    fun initialState_usesTodayProviderForVisibleMonthAndSelectedDate() {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(),
            todayProvider = { today }
        )

        val state = viewModel.uiState.value
        assertEquals(2026, state.visibleYear)
        assertEquals(5, state.visibleMonth)
        assertEquals(today, state.selectedDate)
    }

    @Test
    fun loadMonth_reflectsMonthWodsRecordedDatesAndSelectedDateWods() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(
                wod(id = 1L, date = LocalDate.of(2026, 5, 3), title = "Fran"),
                wod(id = 2L, date = LocalDate.of(2026, 5, 4), title = "Helen"),
                wod(id = 3L, date = LocalDate.of(2026, 6, 1), title = "June WOD")
            )
        )
        val viewModel = CalendarViewModel(repository, todayProvider = { today })

        viewModel.loadMonth(2026, 5)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.monthWods.size)
        assertEquals(setOf(LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 4)), state.recordedDates)
        assertEquals(listOf("Fran"), state.selectedDateWods.map { it.title })
        assertFalse(state.isLoading)
    }

    @Test
    fun selectDate_updatesSelectedDateAndSelectedDateWods() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(
                wod(id = 1L, date = LocalDate.of(2026, 5, 3), title = "Fran"),
                wod(id = 2L, date = LocalDate.of(2026, 5, 4), title = "Helen"),
                wod(id = 3L, date = LocalDate.of(2026, 5, 4), title = "Grace")
            )
        )
        val viewModel = CalendarViewModel(repository, todayProvider = { today })
        viewModel.loadMonth(2026, 5)
        advanceUntilIdle()

        viewModel.selectDate(LocalDate.of(2026, 5, 4))

        val state = viewModel.uiState.value
        assertEquals(LocalDate.of(2026, 5, 4), state.selectedDate)
        assertEquals(listOf("Grace", "Helen"), state.selectedDateWods.map { it.title }.sorted())
    }

    @Test
    fun selectDate_withoutRecords_keepsSelectedDateWodsEmpty() = runTest {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(
                wods = listOf(wod(id = 1L, date = LocalDate.of(2026, 5, 3), title = "Fran"))
            ),
            todayProvider = { today }
        )
        viewModel.loadMonth(2026, 5)
        advanceUntilIdle()

        viewModel.selectDate(LocalDate.of(2026, 5, 10))

        assertTrue(viewModel.uiState.value.selectedDateWods.isEmpty())
    }

    @Test
    fun goToNextMonth_loadsNextMonth() = runTest {
        val repository = FakeWodlogRepository(
            wods = listOf(wod(id = 1L, date = LocalDate.of(2026, 6, 1), title = "June WOD"))
        )
        val viewModel = CalendarViewModel(repository, todayProvider = { today })

        viewModel.goToNextMonth()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2026, state.visibleYear)
        assertEquals(6, state.visibleMonth)
        assertEquals(LocalDate.of(2026, 6, 3), state.selectedDate)
        assertEquals(listOf("June WOD"), state.monthWods.map { it.title })
    }

    @Test
    fun goToNextMonth_fromDecemberMovesToNextYearJanuary() = runTest {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(),
            todayProvider = { LocalDate.of(2026, 12, 31) }
        )

        viewModel.goToNextMonth()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2027, state.visibleYear)
        assertEquals(1, state.visibleMonth)
        assertEquals(LocalDate.of(2027, 1, 31), state.selectedDate)
    }

    @Test
    fun goToPreviousMonth_loadsPreviousMonth() = runTest {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(
                wods = listOf(wod(id = 1L, date = LocalDate.of(2026, 4, 30), title = "April WOD"))
            ),
            todayProvider = { today }
        )

        viewModel.goToPreviousMonth()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2026, state.visibleYear)
        assertEquals(4, state.visibleMonth)
        assertEquals(LocalDate.of(2026, 4, 3), state.selectedDate)
        assertEquals(listOf("April WOD"), state.monthWods.map { it.title })
    }

    @Test
    fun goToPreviousMonth_fromJanuaryMovesToPreviousYearDecember() = runTest {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(),
            todayProvider = { LocalDate.of(2026, 1, 15) }
        )

        viewModel.goToPreviousMonth()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2025, state.visibleYear)
        assertEquals(12, state.visibleMonth)
        assertEquals(LocalDate.of(2025, 12, 15), state.selectedDate)
    }

    @Test
    fun loadMonth_withInvalidMonth_setsErrorMessage() {
        val viewModel = CalendarViewModel(
            repository = FakeWodlogRepository(),
            todayProvider = { today }
        )

        viewModel.loadMonth(2026, 13)

        assertEquals("Invalid calendar month", viewModel.uiState.value.errorMessage)
    }

    private fun wod(
        id: Long,
        date: LocalDate,
        title: String
    ): Wod {
        return Wod(
            id = id,
            date = date,
            title = title,
            type = WodType.FOR_TIME,
            createdAt = now.plusSeconds(id),
            updatedAt = now.plusSeconds(id)
        )
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
    private val wods: List<Wod> = emptyList()
) : WodlogRepository {
    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> {
        return wods.filter { it.date.year == year && it.date.monthValue == month }
    }

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> {
        return wods.filter { it.date == date }
    }

    override suspend fun getUserProfile(): UserProfile? = unused()

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodById(id: Long): Wod? = unused()

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

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by CalendarViewModel tests.")
    }
}
