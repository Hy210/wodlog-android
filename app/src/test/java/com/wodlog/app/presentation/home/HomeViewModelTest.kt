package com.wodlog.app.presentation.home

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = HomeMainDispatcherRule()

    private lateinit var repository: FakeHomeRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeHomeRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun init_observesCafeSources() = runTest {
        repository.sources.value = listOf(cafeSource(1L, "Maple Box"))
        advanceUntilIdle()

        assertEquals(listOf("Maple Box"), viewModel.uiState.value.cafeSources.map { it.boxName })
        assertFalse(viewModel.uiState.value.isLoadingCafeSources)
    }

    @Test
    fun onImportClick_withNoCafeSources_doesNothing() {
        var openedCafeSourceId: Long? = null

        viewModel.onImportClick { openedCafeSourceId = it }

        assertEquals(null, openedCafeSourceId)
        assertFalse(viewModel.uiState.value.isCafeSourcePickerVisible)
    }

    @Test
    fun onImportClick_withOneCafeSource_opensCafeImportDirectly() = runTest {
        repository.sources.value = listOf(cafeSource(7L, "Maple Box"))
        advanceUntilIdle()
        var openedCafeSourceId: Long? = null

        viewModel.onImportClick { openedCafeSourceId = it }

        assertEquals(7L, openedCafeSourceId)
        assertFalse(viewModel.uiState.value.isCafeSourcePickerVisible)
    }

    @Test
    fun onImportClick_withMultipleCafeSources_showsPicker() = runTest {
        repository.sources.value = listOf(
            cafeSource(1L, "Maple Box"),
            cafeSource(2L, "River Box")
        )
        advanceUntilIdle()

        viewModel.onImportClick {}

        assertTrue(viewModel.uiState.value.isCafeSourcePickerVisible)
    }

    @Test
    fun onCafeSourceSelected_hidesPickerAndOpensSelectedSource() = runTest {
        val source = cafeSource(3L, "Selected Box")
        repository.sources.value = listOf(source, cafeSource(4L, "Other Box"))
        advanceUntilIdle()
        viewModel.onImportClick {}
        var openedCafeSourceId: Long? = null

        viewModel.onCafeSourceSelected(source) { openedCafeSourceId = it }

        assertEquals(3L, openedCafeSourceId)
        assertFalse(viewModel.uiState.value.isCafeSourcePickerVisible)
    }

    private fun cafeSource(id: Long, boxName: String): CafeSource {
        val now = Instant.parse("2026-05-05T00:00:00Z")
        return CafeSource(
            id = id,
            boxName = boxName,
            boardUrl = "https://cafe.naver.com/$id",
            titleKeywords = listOf("WOD"),
            preferMobileUrl = true,
            createdAt = now,
            updatedAt = now
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeMainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeHomeRepository : WodlogRepository {
    val sources = MutableStateFlow<List<CafeSource>>(emptyList())

    override fun observeCafeSources(): Flow<List<CafeSource>> = sources

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
        error("This repository method is not used by HomeViewModel tests.")
    }
}
