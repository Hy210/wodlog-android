package com.wodlog.app.presentation.settings

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.CafeSource
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class CafeSourceSettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = CafeSourceMainDispatcherRule()

    private val now = Instant.parse("2026-05-05T00:00:00Z")

    private lateinit var repository: FakeCafeSourceRepository
    private lateinit var viewModel: CafeSourceSettingsViewModel

    @Before
    fun setUp() {
        repository = FakeCafeSourceRepository()
        viewModel = CafeSourceSettingsViewModel(
            repository = repository,
            nowProvider = { now }
        )
    }

    @Test
    fun init_observesCafeSources() = runTest {
        repository.replaceSources(
            listOf(
                cafeSource(id = 1L, boxName = "Alpha"),
                cafeSource(id = 2L, boxName = "Bravo")
            )
        )
        advanceUntilIdle()

        assertEquals(listOf("Alpha", "Bravo"), viewModel.uiState.value.cafeSources.map { it.boxName })
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun saveCafeSource_withValidInput_savesAndResetsForm() = runTest {
        viewModel.onBoxNameChange(" Maple Box ")
        viewModel.onBoardUrlChange(" https://cafe.naver.com/maple ")
        viewModel.onTitleKeywordsTextChange(" WOD, , Metcon ")
        viewModel.onPreferMobileUrlChange(false)

        viewModel.saveCafeSource()
        advanceUntilIdle()

        val saved = repository.sources.value.single()
        assertEquals("Maple Box", saved.boxName)
        assertEquals("https://cafe.naver.com/maple", saved.boardUrl)
        assertEquals(listOf("WOD", "Metcon"), saved.titleKeywords)
        assertFalse(saved.preferMobileUrl)
        assertEquals("", viewModel.uiState.value.boxName)
        assertEquals("카페 소스를 저장했습니다.", viewModel.uiState.value.message)
    }

    @Test
    fun saveCafeSource_withBlankKeywords_savesDefaultKeywords() = runTest {
        viewModel.onBoxNameChange("Maple Box")
        viewModel.onBoardUrlChange("https://cafe.naver.com/maple")
        viewModel.onTitleKeywordsTextChange(" ")

        viewModel.saveCafeSource()
        advanceUntilIdle()

        assertEquals(DefaultCafeSourceKeywords, repository.sources.value.single().titleKeywords)
    }

    @Test
    fun saveCafeSource_withInvalidInputs_setsKoreanErrorsAndDoesNotSave() = runTest {
        viewModel.onBoxNameChange(" ")
        viewModel.onBoardUrlChange("ftp://cafe.naver.com/maple")

        viewModel.saveCafeSource()
        advanceUntilIdle()

        assertEquals("Box 이름을 입력해 주세요.", viewModel.uiState.value.boxNameError)
        assertEquals("올바른 URL을 입력해 주세요.", viewModel.uiState.value.boardUrlError)
        assertTrue(repository.sources.value.isEmpty())
    }

    @Test
    fun startEdit_thenSave_updatesExistingSource() = runTest {
        repository.replaceSources(listOf(cafeSource(id = 7L, boxName = "Before")))
        advanceUntilIdle()

        viewModel.startEdit(repository.sources.value.single())
        viewModel.onBoxNameChange("After")
        viewModel.onBoardUrlChange("https://cafe.naver.com/after")
        viewModel.saveCafeSource()
        advanceUntilIdle()

        val saved = repository.sources.value.single()
        assertEquals(7L, saved.id)
        assertEquals("After", saved.boxName)
        assertNull(viewModel.uiState.value.editingCafeSourceId)
    }

    @Test
    fun confirmDelete_deletesRequestedSource() = runTest {
        val source = cafeSource(id = 3L, boxName = "Delete Me")
        repository.replaceSources(listOf(source))
        advanceUntilIdle()

        viewModel.requestDelete(source)
        viewModel.confirmDelete()
        advanceUntilIdle()

        assertTrue(repository.sources.value.isEmpty())
        assertNull(viewModel.uiState.value.deleteTarget)
        assertEquals("카페 소스를 삭제했습니다.", viewModel.uiState.value.message)
    }

    private fun cafeSource(
        id: Long,
        boxName: String,
        boardUrl: String = "https://cafe.naver.com/$boxName"
    ): CafeSource {
        return CafeSource(
            id = id,
            boxName = boxName,
            boardUrl = boardUrl,
            titleKeywords = DefaultCafeSourceKeywords,
            preferMobileUrl = true,
            createdAt = now,
            updatedAt = now
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CafeSourceMainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeCafeSourceRepository : WodlogRepository {
    val sources = MutableStateFlow<List<CafeSource>>(emptyList())
    private var nextId = 1L

    fun replaceSources(newSources: List<CafeSource>) {
        sources.value = newSources
        nextId = (newSources.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    override fun observeCafeSources(): Flow<List<CafeSource>> = sources

    override suspend fun getCafeSource(id: Long): CafeSource? {
        return sources.value.firstOrNull { it.id == id }
    }

    override suspend fun saveCafeSource(cafeSource: CafeSource): Long {
        val id = cafeSource.id.takeIf { it != 0L } ?: nextId++
        val saved = cafeSource.copy(id = id)
        sources.value = sources.value.filterNot { it.id == id } + saved
        return id
    }

    override suspend fun deleteCafeSource(id: Long) {
        sources.value = sources.value.filterNot { it.id == id }
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

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by CafeSourceSettingsViewModel tests.")
    }
}
