package com.wodlog.app.presentation.wodedit

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodSourceType
import com.wodlog.app.domain.model.WodType
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
class WodEditViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 5, 3)
    private val now = Instant.parse("2026-05-03T00:00:00Z")
    private val localIds = ArrayDeque(listOf("section-1", "movement-1", "movement-2"))

    private lateinit var repository: FakeWodlogRepository
    private lateinit var viewModel: WodEditViewModel

    @Before
    fun setUp() {
        repository = FakeWodlogRepository()
        viewModel = WodEditViewModel(
            repository = repository,
            todayProvider = { today },
            nowProvider = { now },
            localIdProvider = { localIds.removeFirst() }
        )
    }

    @Test
    fun initialState_usesTodayAsDefaultDate() {
        assertEquals("2026-05-03", viewModel.uiState.value.dateInput)
        assertEquals(WodSourceType.MANUAL, viewModel.uiState.value.sourceType)
        assertNull(viewModel.uiState.value.sourceUrl)
    }

    @Test
    fun initialState_withImportedWod_prefillsEditableInputsWithoutSaving() {
        val importedAt = Instant.parse("2026-05-02T12:00:00Z")
        val importedViewModel = WodEditViewModel(
            repository = repository,
            importedWodText = ImportedWodText(
                sourceUrl = "https://cafe.naver.com/box/123",
                title = "Today WOD",
                importedText = "21-15-9\nThruster\nPull-up",
                importedAt = importedAt
            ),
            todayProvider = { today },
            nowProvider = { now },
            localIdProvider = { localIds.removeFirst() }
        )

        val state = importedViewModel.uiState.value
        assertEquals("2026-05-03", state.dateInput)
        assertEquals("Today WOD", state.titleInput)
        assertEquals("21-15-9\nThruster\nPull-up", state.rawTextInput)
        assertEquals(WodType.OTHER, state.wodType)
        assertEquals(WodSourceType.NAVER_CAFE_WEBVIEW, state.sourceType)
        assertEquals("https://cafe.naver.com/box/123", state.sourceUrl)
        assertEquals(importedAt, state.importedAt)
        assertTrue(repository.savedWods.isEmpty())
    }

    @Test
    fun importedPrefill_doesNotOverwriteUserEdits() {
        val importedViewModel = WodEditViewModel(
            repository = repository,
            importedWodText = ImportedWodText(
                sourceUrl = "https://cafe.naver.com/box/123",
                title = "Today WOD",
                importedText = "Imported text",
                importedAt = Instant.parse("2026-05-02T12:00:00Z")
            ),
            todayProvider = { today },
            nowProvider = { now },
            localIdProvider = { localIds.removeFirst() }
        )

        importedViewModel.onTitleChange("Edited title")
        importedViewModel.onRawTextChange("Edited raw text")

        assertEquals("Edited title", importedViewModel.uiState.value.titleInput)
        assertEquals("Edited raw text", importedViewModel.uiState.value.rawTextInput)
    }

    @Test
    fun inputChanges_updateState() {
        viewModel.onDateChange("2026-05-01")
        viewModel.onTitleChange("Fran")
        viewModel.onWodTypeChange(WodType.FOR_TIME)
        viewModel.onRawTextChange("21-15-9")
        viewModel.onMemoChange("felt good")

        val state = viewModel.uiState.value
        assertEquals("2026-05-01", state.dateInput)
        assertEquals("Fran", state.titleInput)
        assertEquals(WodType.FOR_TIME, state.wodType)
        assertEquals("21-15-9", state.rawTextInput)
        assertEquals("felt good", state.memoInput)
    }

    @Test
    fun addAndRemoveSection_updatesState() {
        viewModel.addSection()
        val sectionId = viewModel.uiState.value.sections.first().localId
        viewModel.updateSectionTitle(sectionId, "Warm-up")
        viewModel.updateSectionMemo(sectionId, "easy")

        assertEquals("Warm-up", viewModel.uiState.value.sections.first().titleInput)
        assertEquals("easy", viewModel.uiState.value.sections.first().memoInput)

        viewModel.removeSection(sectionId)

        assertTrue(viewModel.uiState.value.sections.isEmpty())
    }

    @Test
    fun addAndRemoveMovement_updatesState() {
        viewModel.addMovement()
        val movementId = viewModel.uiState.value.movements.first().localId

        viewModel.updateMovementName(movementId, "Thruster")
        viewModel.updateMovementReps(movementId, "21")
        viewModel.updateMovementWeight(movementId, "43")

        val movement = viewModel.uiState.value.movements.first()
        assertEquals("Thruster", movement.nameInput)
        assertEquals("21", movement.repsInput)
        assertEquals("43", movement.weightInput)

        viewModel.removeMovement(movementId)

        assertTrue(viewModel.uiState.value.movements.isEmpty())
    }

    @Test
    fun saveWod_withBlankTitle_addsValidationErrorAndDoesNotSave() = runTest {
        viewModel.onDateChange("2026-05-01")
        viewModel.onWodTypeChange(WodType.AMRAP)

        viewModel.saveWod()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.WOD_TITLE_BLANK))
        assertTrue(repository.savedWods.isEmpty())
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun saveWod_withInvalidDate_addsValidationErrorAndDoesNotSave() = runTest {
        viewModel.onDateChange("not-a-date")
        viewModel.onTitleChange("Fran")
        viewModel.onWodTypeChange(WodType.FOR_TIME)

        viewModel.saveWod()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.WOD_DATE_INVALID))
        assertTrue(repository.savedWods.isEmpty())
    }

    @Test
    fun saveWod_withNegativeReps_addsValidationErrorAndDoesNotSave() = runTest {
        viewModel.onTitleChange("Fran")
        viewModel.onWodTypeChange(WodType.FOR_TIME)
        viewModel.addMovement()
        val movementId = viewModel.uiState.value.movements.first().localId
        viewModel.updateMovementName(movementId, "Thruster")
        viewModel.updateMovementReps(movementId, "-1")

        viewModel.saveWod()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationErrors.contains(ValidationError.MOVEMENT_REPS_NEGATIVE))
        assertTrue(repository.savedWods.isEmpty())
        assertTrue(repository.savedMovements.isEmpty())
    }

    @Test
    fun saveWod_withValidInput_savesWodSectionAndMovement() = runTest {
        viewModel.onDateChange("2026-05-01")
        viewModel.onTitleChange("Fran")
        viewModel.onWodTypeChange(WodType.FOR_TIME)
        viewModel.onRawTextChange("21-15-9 thrusters and pull-ups")
        viewModel.onMemoChange("benchmark")
        viewModel.addSection()
        val sectionId = viewModel.uiState.value.sections.first().localId
        viewModel.updateSectionTitle(sectionId, "Metcon")
        viewModel.addMovement(sectionId)
        val movementId = viewModel.uiState.value.movements.first().localId
        viewModel.updateMovementName(movementId, "Thruster")
        viewModel.updateMovementWeight(movementId, "43")
        viewModel.updateMovementReps(movementId, "21")
        viewModel.updateMovementSets(movementId, "1")
        viewModel.updateMovementCategory(movementId, MovementCategory.WEIGHTLIFTING)
        viewModel.updateMovementMemo(movementId, "scaled weight")

        viewModel.saveWod()
        advanceUntilIdle()

        assertEquals(1L, viewModel.uiState.value.savedWodId)
        assertEquals("WOD saved", viewModel.uiState.value.message)
        assertEquals(1, repository.savedWods.size)
        assertEquals(1, repository.savedSections.size)
        assertEquals(1, repository.savedMovements.size)

        val savedWod = repository.savedWods.single()
        assertEquals(LocalDate.of(2026, 5, 1), savedWod.date)
        assertEquals("Fran", savedWod.title)
        assertEquals(WodType.FOR_TIME, savedWod.type)
        assertEquals(WodSourceType.MANUAL, savedWod.sourceType)
        assertNull(savedWod.sourceUrl)

        val savedSection = repository.savedSections.single()
        assertEquals(1L, savedSection.wodId)
        assertEquals("Metcon", savedSection.name)

        val savedMovement = repository.savedMovements.single()
        assertEquals(1L, savedMovement.wodId)
        assertEquals(100L, savedMovement.sectionId)
        assertEquals("Thruster", savedMovement.name)
        assertEquals(43.0, savedMovement.weightKg)
        assertEquals(21, savedMovement.reps)
        assertEquals(MovementCategory.WEIGHTLIFTING, savedMovement.category)
        assertEquals("scaled weight", savedMovement.notes)
    }

    @Test
    fun saveWod_withImportedPrefill_savesSourceMetadataOnlyAfterSaveClick() = runTest {
        val importedAt = Instant.parse("2026-05-02T12:00:00Z")
        val importedViewModel = WodEditViewModel(
            repository = repository,
            importedWodText = ImportedWodText(
                sourceUrl = "https://cafe.naver.com/box/123",
                title = "Today WOD",
                importedText = "21-15-9\nThruster\nPull-up",
                importedAt = importedAt
            ),
            todayProvider = { today },
            nowProvider = { now },
            localIdProvider = { localIds.removeFirst() }
        )

        assertTrue(repository.savedWods.isEmpty())

        importedViewModel.saveWod()
        advanceUntilIdle()

        val savedWod = repository.savedWods.single()
        assertEquals("Today WOD", savedWod.title)
        assertEquals("21-15-9\nThruster\nPull-up", savedWod.rawText)
        assertEquals(WodSourceType.NAVER_CAFE_WEBVIEW, savedWod.sourceType)
        assertEquals("https://cafe.naver.com/box/123", savedWod.sourceUrl)
        assertEquals(importedAt, savedWod.importedAt)
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
    val savedWods = mutableListOf<Wod>()
    val savedSections = mutableListOf<WodSection>()
    val savedMovements = mutableListOf<Movement>()

    override suspend fun saveWod(wod: Wod): Long {
        val saved = wod.copy(id = 1L)
        savedWods += saved
        return saved.id
    }

    override suspend fun saveWodSection(section: WodSection): Long {
        val sectionId = 100L + savedSections.size
        savedSections += section.copy(id = sectionId)
        return sectionId
    }

    override suspend fun saveMovement(movement: Movement): Long {
        val movementId = 200L + savedMovements.size
        savedMovements += movement.copy(id = movementId)
        return movementId
    }

    override suspend fun getUserProfile(): UserProfile? = unused()

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodById(id: Long): Wod? = unused()

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> = unused()

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = unused()

    override suspend fun getRecentWods(limit: Int): List<Wod> = unused()

    override suspend fun deleteWod(id: Long): Unit = unused()

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> = unused()

    override suspend fun deleteWodSection(id: Long): Unit = unused()

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> = unused()

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
        error("This repository method is not used by WodEditViewModel tests.")
    }
}
