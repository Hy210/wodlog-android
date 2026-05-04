package com.wodlog.app.presentation.profile

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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 5, 3)
    private val now = Instant.parse("2026-05-03T00:00:00Z")

    private lateinit var repository: FakeWodlogRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        repository = FakeWodlogRepository()
        viewModel = ProfileViewModel(
            repository = repository,
            todayProvider = { today },
            nowProvider = { now }
        )
    }

    @Test
    fun loadProfile_whenSavedProfileExists_fillsStateInputs() = runTest {
        repository.profile = UserProfile(
            id = 1L,
            heightCm = 180.0,
            weightKg = 82.5,
            crossfitStartDate = LocalDate.of(2026, 5, 1),
            createdAt = now,
            updatedAt = now
        )

        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("180", state.heightCmInput)
        assertEquals("82.5", state.weightKgInput)
        assertEquals("2026-05-01", state.crossfitStartDateInput)
        assertEquals(2L, state.trainingDays)
        assertTrue(state.hasProfile)
        assertFalse(state.isLoading)
    }

    @Test
    fun onHeightChange_updatesState() {
        viewModel.onHeightChange("175")

        assertEquals("175", viewModel.uiState.value.heightCmInput)
    }

    @Test
    fun saveProfile_withInvalidHeight_addsValidationErrorAndDoesNotSave() = runTest {
        viewModel.onHeightChange("30")
        viewModel.onWeightChange("80")
        viewModel.onCrossfitStartDateChange("2026-04-01")

        viewModel.saveProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.contains(ValidationError.HEIGHT_OUT_OF_RANGE))
        assertEquals(0, repository.saveUserProfileCount)
        assertNull(repository.profile)
        assertFalse(state.isSaving)
    }

    @Test
    fun saveProfile_withFutureStartDate_doesNotSave() = runTest {
        viewModel.onHeightChange("175")
        viewModel.onWeightChange("80")
        viewModel.onCrossfitStartDateChange("2026-05-04")

        viewModel.saveProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.contains(ValidationError.CROSSFIT_START_DATE_IN_FUTURE))
        assertEquals(0, repository.saveUserProfileCount)
    }

    @Test
    fun saveProfile_withValidInput_savesProfile() = runTest {
        viewModel.onHeightChange("175")
        viewModel.onWeightChange("80.5")
        viewModel.onCrossfitStartDateChange("2026-04-30")

        viewModel.saveProfile()
        advanceUntilIdle()

        val savedProfile = repository.profile
        requireNotNull(savedProfile)
        assertEquals(175.0, savedProfile.heightCm)
        assertEquals(80.5, savedProfile.weightKg)
        assertEquals(LocalDate.of(2026, 4, 30), savedProfile.crossfitStartDate)
        assertEquals(1, repository.saveUserProfileCount)
        assertTrue(viewModel.uiState.value.hasProfile)
        assertEquals(3L, viewModel.uiState.value.trainingDays)
    }

    @Test
    fun onCrossfitStartDateChange_calculatesTrainingDaysFromTodayProvider() {
        viewModel.onCrossfitStartDateChange("2026-04-28")

        assertEquals(5L, viewModel.uiState.value.trainingDays)
    }

    @Test
    fun loadProfile_whenProfileDoesNotExist_setsTodayAsStartDateDefault() = runTest {
        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.heightCmInput)
        assertEquals("", state.weightKgInput)
        assertEquals("2026-05-03", state.crossfitStartDateInput)
        assertEquals(0L, state.trainingDays)
        assertFalse(state.hasProfile)
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

private class FakeWodlogRepository : WodlogRepository {
    var profile: UserProfile? = null
    var saveUserProfileCount = 0

    override suspend fun getUserProfile(): UserProfile? = profile

    override suspend fun saveUserProfile(profile: UserProfile): Long {
        this.profile = profile
        saveUserProfileCount += 1
        return profile.id
    }

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

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by ProfileViewModel tests.")
    }
}
