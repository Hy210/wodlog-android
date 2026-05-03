package com.wodlog.app.presentation.lifestyle

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.ValidationError
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
class LifestyleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 5, 3)
    private val currentWeekStart = LocalDate.of(2026, 4, 27)
    private val now = Instant.parse("2026-05-03T00:00:00Z")
    private val later = Instant.parse("2026-05-03T01:00:00Z")

    @Test
    fun initialState_usesTodayProviderWeekStartMonday() {
        val viewModel = LifestyleViewModel(
            repository = FakeWodlogRepository(),
            todayProvider = { today },
            nowProvider = { now }
        )

        assertEquals("2026-04-27", viewModel.uiState.value.weekStartDateInput)
    }

    @Test
    fun loadLifestyleLog_whenExistingLogExists_fillsStateInputs() = runTest {
        val repository = FakeWodlogRepository(
            lifestyleLog = LifestyleLog(
                id = 5L,
                weekStartDate = currentWeekStart,
                mealSummary = "mostly home meals",
                alcohol = true,
                alcoholAmountPerWeek = "2",
                smoking = true,
                smokingAmountPerWeek = "7",
                sleepAverageHours = 6.5,
                notes = "busy week",
                createdAt = now,
                updatedAt = now
            )
        )
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { later })

        viewModel.loadLifestyleLog(currentWeekStart)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2026-04-27", state.weekStartDateInput)
        assertEquals("mostly home meals", state.dietSummaryInput)
        assertTrue(state.hasAlcohol)
        assertEquals("2", state.alcoholAmountPerWeekInput)
        assertTrue(state.hasSmoking)
        assertEquals("7", state.smokingAmountPerWeekInput)
        assertEquals("6.5", state.averageSleepHoursInput)
        assertEquals("busy week", state.memoInput)
        assertTrue(state.hasExistingLog)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadLifestyleLog_whenExistingLogDoesNotExist_keepsEmptyInputState() = runTest {
        val viewModel = LifestyleViewModel(FakeWodlogRepository(), todayProvider = { today }, nowProvider = { now })

        viewModel.loadLifestyleLog(currentWeekStart)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2026-04-27", state.weekStartDateInput)
        assertEquals("", state.dietSummaryInput)
        assertFalse(state.hasAlcohol)
        assertEquals("", state.alcoholAmountPerWeekInput)
        assertFalse(state.hasExistingLog)
    }

    @Test
    fun inputChanges_updateState() {
        val viewModel = LifestyleViewModel(FakeWodlogRepository(), todayProvider = { today }, nowProvider = { now })

        viewModel.onHasAlcoholChange(true)
        viewModel.onAlcoholAmountChange("3")
        viewModel.onHasSmokingChange(true)
        viewModel.onSmokingAmountChange("5")
        viewModel.onAverageSleepHoursChange("7.5")
        viewModel.onDietSummaryChange("protein enough")
        viewModel.onMemoChange("recovered")

        val state = viewModel.uiState.value
        assertTrue(state.hasAlcohol)
        assertEquals("3", state.alcoholAmountPerWeekInput)
        assertTrue(state.hasSmoking)
        assertEquals("5", state.smokingAmountPerWeekInput)
        assertEquals("7.5", state.averageSleepHoursInput)
        assertEquals("protein enough", state.dietSummaryInput)
        assertEquals("recovered", state.memoInput)
    }

    @Test
    fun saveLifestyleLog_withAverageSleepHoursTwentyFive_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { now })
        viewModel.onAverageSleepHoursChange("25")

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE))
        assertEquals(0, repository.saveLifestyleLogCount)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun saveLifestyleLog_withNonMondayWeekStartDate_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { now })
        viewModel.onWeekStartDateChange("2026-05-03")

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY))
        assertEquals(0, repository.saveLifestyleLogCount)
    }

    @Test
    fun saveLifestyleLog_withInvalidNumericInput_addsParseErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { now })
        viewModel.onHasAlcoholChange(true)
        viewModel.onAlcoholAmountChange("many")

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_INVALID))
        assertEquals(0, repository.saveLifestyleLogCount)
    }

    @Test
    fun saveLifestyleLog_withValidInput_savesLogAndSetsSavedLogId() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { now })
        viewModel.onDietSummaryChange("balanced meals")
        viewModel.onHasAlcoholChange(true)
        viewModel.onAlcoholAmountChange("2")
        viewModel.onHasSmokingChange(true)
        viewModel.onSmokingAmountChange("0")
        viewModel.onAverageSleepHoursChange("7")
        viewModel.onMemoChange("felt steady")

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        val savedLog = requireNotNull(repository.lifestyleLog)
        assertEquals(currentWeekStart, savedLog.weekStartDate)
        assertEquals("balanced meals", savedLog.mealSummary)
        assertEquals(true, savedLog.alcohol)
        assertEquals("2", savedLog.alcoholAmountPerWeek)
        assertEquals(true, savedLog.smoking)
        assertEquals("0", savedLog.smokingAmountPerWeek)
        assertEquals(7.0, requireNotNull(savedLog.sleepAverageHours), 0.0)
        assertEquals("felt steady", savedLog.notes)
        assertEquals(10L, viewModel.uiState.value.savedLifestyleLogId)
        assertEquals("Lifestyle log saved", viewModel.uiState.value.message)
    }

    @Test
    fun saveLifestyleLog_whenExistingLogLoaded_keepsExistingIdAndCreatedAt() = runTest {
        val repository = FakeWodlogRepository(
            lifestyleLog = LifestyleLog(
                id = 5L,
                weekStartDate = currentWeekStart,
                mealSummary = "old",
                createdAt = now,
                updatedAt = now
            )
        )
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { later })
        viewModel.loadLifestyleLog(currentWeekStart)
        advanceUntilIdle()
        viewModel.onDietSummaryChange("updated")

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        val savedLog = requireNotNull(repository.lifestyleLog)
        assertEquals(5L, savedLog.id)
        assertEquals(now, savedLog.createdAt)
        assertEquals(later, savedLog.updatedAt)
        assertEquals("updated", savedLog.mealSummary)
        assertEquals(5L, viewModel.uiState.value.savedLifestyleLogId)
    }

    @Test
    fun saveLifestyleLog_whenAlcoholAndSmokingFalse_storesAmountsAsNull() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = LifestyleViewModel(repository, todayProvider = { today }, nowProvider = { now })
        viewModel.onHasAlcoholChange(true)
        viewModel.onAlcoholAmountChange("4")
        viewModel.onHasAlcoholChange(false)
        viewModel.onHasSmokingChange(true)
        viewModel.onSmokingAmountChange("10")
        viewModel.onHasSmokingChange(false)

        viewModel.saveLifestyleLog()
        advanceUntilIdle()

        val savedLog = requireNotNull(repository.lifestyleLog)
        assertEquals(false, savedLog.alcohol)
        assertNull(savedLog.alcoholAmountPerWeek)
        assertEquals(false, savedLog.smoking)
        assertNull(savedLog.smokingAmountPerWeek)
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
    var lifestyleLog: LifestyleLog? = null
) : WodlogRepository {
    var saveLifestyleLogCount = 0

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? {
        return lifestyleLog?.takeIf { it.weekStartDate == weekStartDate }
    }

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long {
        saveLifestyleLogCount += 1
        val id = log.id.takeIf { it != 0L } ?: 10L
        lifestyleLog = log.copy(id = id)
        return id
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

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by LifestyleViewModel tests.")
    }
}
