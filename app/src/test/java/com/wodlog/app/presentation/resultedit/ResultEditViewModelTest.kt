package com.wodlog.app.presentation.resultedit

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
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
class ResultEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = Instant.parse("2026-05-03T00:00:00Z")
    private val later = Instant.parse("2026-05-03T01:00:00Z")

    @Test
    fun loadResult_whenExistingResultExists_fillsStateInputs() = runTest {
        val repository = FakeWodlogRepository(
            result = WodResult(
                id = 5L,
                wodId = 1L,
                scoreType = ScoreType.ROUNDS_REPS,
                timeSeconds = 420,
                rounds = 7,
                extraReps = 12,
                totalReps = 222,
                loadKg = 60.0,
                distanceMeters = 500.5,
                calories = 30.0,
                rxStatus = RxStatus.SCALED,
                rpe = 8,
                condition = Condition.GOOD,
                memo = "paced well",
                createdAt = now,
                updatedAt = now
            )
        )
        val viewModel = ResultEditViewModel(repository, nowProvider = { later })

        viewModel.loadResult(1L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1L, state.wodId)
        assertEquals(ScoreType.ROUNDS_REPS, state.scoreType)
        assertEquals("420", state.timeSecondsInput)
        assertEquals("7", state.roundsInput)
        assertEquals("12", state.repsInput)
        assertEquals("222", state.totalRepsInput)
        assertEquals("60", state.loadInput)
        assertEquals("500.5", state.distanceInput)
        assertEquals("30", state.caloriesInput)
        assertEquals(RxStatus.SCALED, state.rxStatus)
        assertEquals("8", state.rpeInput)
        assertEquals(Condition.GOOD, state.condition)
        assertEquals("paced well", state.memoInput)
        assertTrue(state.hasExistingResult)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadResult_whenExistingResultDoesNotExist_keepsEmptyInputState() = runTest {
        val viewModel = ResultEditViewModel(FakeWodlogRepository(), nowProvider = { now })

        viewModel.loadResult(1L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1L, state.wodId)
        assertNull(state.scoreType)
        assertEquals("", state.timeSecondsInput)
        assertFalse(state.hasExistingResult)
        assertFalse(state.isLoading)
    }

    @Test
    fun inputChanges_updateState() {
        val viewModel = ResultEditViewModel(FakeWodlogRepository(), nowProvider = { now })

        viewModel.onScoreTypeChange(ScoreType.TIME)
        viewModel.onRxStatusChange(RxStatus.RX)
        viewModel.onConditionChange(Condition.GREAT)
        viewModel.onTimeSecondsChange("300")
        viewModel.onRoundsChange("5")
        viewModel.onRepsChange("12")
        viewModel.onTotalRepsChange("72")
        viewModel.onLoadChange("100.5")
        viewModel.onDistanceChange("1000")
        viewModel.onCaloriesChange("50")
        viewModel.onRpeChange("9")
        viewModel.onMemoChange("strong finish")

        val state = viewModel.uiState.value
        assertEquals(ScoreType.TIME, state.scoreType)
        assertEquals(RxStatus.RX, state.rxStatus)
        assertEquals(Condition.GREAT, state.condition)
        assertEquals("300", state.timeSecondsInput)
        assertEquals("5", state.roundsInput)
        assertEquals("12", state.repsInput)
        assertEquals("72", state.totalRepsInput)
        assertEquals("100.5", state.loadInput)
        assertEquals("1000", state.distanceInput)
        assertEquals("50", state.caloriesInput)
        assertEquals("9", state.rpeInput)
        assertEquals("strong finish", state.memoInput)
    }

    @Test
    fun saveResult_withRpeEleven_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = ResultEditViewModel(repository, nowProvider = { now })
        viewModel.loadResult(1L)
        advanceUntilIdle()
        viewModel.onScoreTypeChange(ScoreType.TIME)
        viewModel.onRpeChange("11")

        viewModel.saveResult()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.RESULT_RPE_OUT_OF_RANGE))
        assertEquals(0, repository.saveWodResultCount)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun saveResult_withNegativeTotalReps_addsValidationErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = ResultEditViewModel(repository, nowProvider = { now })
        viewModel.loadResult(1L)
        advanceUntilIdle()
        viewModel.onScoreTypeChange(ScoreType.REPS)
        viewModel.onTotalRepsChange("-1")

        viewModel.saveResult()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.RESULT_TOTAL_REPS_NEGATIVE))
        assertEquals(0, repository.saveWodResultCount)
    }

    @Test
    fun saveResult_withInvalidTime_addsParseErrorAndDoesNotSave() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = ResultEditViewModel(repository, nowProvider = { now })
        viewModel.loadResult(1L)
        advanceUntilIdle()
        viewModel.onScoreTypeChange(ScoreType.TIME)
        viewModel.onTimeSecondsChange("not-a-number")

        viewModel.saveResult()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.RESULT_TIME_INVALID))
        assertEquals(0, repository.saveWodResultCount)
    }

    @Test
    fun saveResult_withValidInput_savesResultAndSetsSavedResultId() = runTest {
        val repository = FakeWodlogRepository()
        val viewModel = ResultEditViewModel(repository, nowProvider = { now })
        viewModel.loadResult(1L)
        advanceUntilIdle()
        viewModel.onScoreTypeChange(ScoreType.TIME)
        viewModel.onTimeSecondsChange("300")
        viewModel.onRxStatusChange(RxStatus.RX)
        viewModel.onConditionChange(Condition.GOOD)
        viewModel.onRpeChange("8")
        viewModel.onMemoChange("clean")

        viewModel.saveResult()
        advanceUntilIdle()

        val savedResult = requireNotNull(repository.result)
        assertEquals(1L, savedResult.wodId)
        assertEquals(ScoreType.TIME, savedResult.scoreType)
        assertEquals(300, savedResult.timeSeconds)
        assertEquals(RxStatus.RX, savedResult.rxStatus)
        assertEquals(Condition.GOOD, savedResult.condition)
        assertEquals("clean", savedResult.memo)
        assertEquals(10L, viewModel.uiState.value.savedResultId)
        assertEquals("Result saved", viewModel.uiState.value.message)
        assertTrue(viewModel.uiState.value.hasExistingResult)
    }

    @Test
    fun saveResult_whenExistingResultLoaded_keepsExistingId() = runTest {
        val repository = FakeWodlogRepository(
            result = WodResult(
                id = 5L,
                wodId = 1L,
                scoreType = ScoreType.REPS,
                totalReps = 100,
                createdAt = now,
                updatedAt = now
            )
        )
        val viewModel = ResultEditViewModel(repository, nowProvider = { later })
        viewModel.loadResult(1L)
        advanceUntilIdle()
        viewModel.onTotalRepsChange("120")

        viewModel.saveResult()
        advanceUntilIdle()

        val savedResult = requireNotNull(repository.result)
        assertEquals(5L, savedResult.id)
        assertEquals(120, savedResult.totalReps)
        assertEquals(now, savedResult.createdAt)
        assertEquals(later, savedResult.updatedAt)
        assertEquals(5L, viewModel.uiState.value.savedResultId)
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
    var result: WodResult? = null
) : WodlogRepository {
    var saveWodResultCount = 0

    override suspend fun getResultForWod(wodId: Long): WodResult? {
        return result?.takeIf { it.wodId == wodId }
    }

    override suspend fun saveWodResult(result: WodResult): Long {
        saveWodResultCount += 1
        val id = result.id.takeIf { it != 0L } ?: 10L
        this.result = result.copy(id = id)
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

    override suspend fun deleteWodResult(id: Long): Unit = unused()

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? = unused()

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long = unused()

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by ResultEditViewModel tests.")
    }
}
