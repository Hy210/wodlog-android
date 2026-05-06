package com.wodlog.app.presentation.prompt

import com.wodlog.app.domain.analysis.AnalysisSummary
import com.wodlog.app.domain.analysis.CategoryShare
import com.wodlog.app.domain.analysis.PromptInput
import com.wodlog.app.domain.analysis.WodAnalysisInput
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
import com.wodlog.app.presentation.profile.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PromptViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = Instant.parse("2026-05-04T00:00:00Z")

    private lateinit var repository: FakePromptRepository

    @Before
    fun setUp() {
        repository = FakePromptRepository()
    }

    @Test
    fun loadPrompt_whenWodDoesNotExist_setsErrorMessage() = runTest {
        val viewModel = viewModel()

        viewModel.loadPrompt(404L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("WOD not found.", state.errorMessage)
        assertFalse(state.isLoading)
        assertEquals("", state.promptText)
    }

    @Test
    fun loadPrompt_whenWodExists_generatesPromptText() = runTest {
        repository.wods[1L] = wod(id = 1L, title = "Fran")
        repository.movementsByWodId[1L] = listOf(movement(wodId = 1L, name = "Thruster"))
        val viewModel = viewModel()

        viewModel.loadPrompt(1L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals("Fran", state.wodTitle)
        assertTrue(state.promptText.contains("Fran"))
        assertTrue(state.promptText.contains("Thruster"))
    }

    @Test
    fun loadPrompt_usesWodDateWeekStartForLifestyleLog() = runTest {
        repository.wods[1L] = wod(id = 1L, date = LocalDate.of(2026, 5, 6))
        repository.lifestyleByWeekStart[LocalDate.of(2026, 5, 4)] = lifestyleLog(
            weekStartDate = LocalDate.of(2026, 5, 4),
            mealSummary = "High protein"
        )
        val viewModel = viewModel()

        viewModel.loadPrompt(1L)
        advanceUntilIdle()

        assertEquals(LocalDate.of(2026, 5, 4), repository.requestedLifestyleWeekStart)
        assertTrue(viewModel.uiState.value.promptText.contains("High protein"))
    }

    @Test
    fun loadPrompt_passesRecentSummaryToPromptGenerator() = runTest {
        repository.wods[1L] = wod(id = 1L, title = "Current")
        repository.sectionsByWodId[1L] = listOf(section(wodId = 1L, name = "Current section"))
        repository.recentWods = listOf(
            wod(id = 1L, title = "Current"),
            wod(id = 2L, title = "Previous")
        )
        repository.sectionsByWodId[2L] = listOf(section(wodId = 2L, name = "Previous section"))
        var capturedInput: PromptInput? = null
        var capturedSummaryInputs: List<WodAnalysisInput> = emptyList()
        val viewModel = viewModel(
            promptGenerator = { input ->
                capturedInput = input
                "has summary=${input.recentSummary != null}, sections=${input.sections.size}"
            },
            summaryGenerator = { inputs ->
                capturedSummaryInputs = inputs
                AnalysisSummary(
                    items = emptyList(),
                    categoryBreakdown = listOf(
                        CategoryShare(
                            category = MovementCategory.STRENGTH,
                            count = inputs.size,
                            ratio = 1.0
                        )
                    ),
                    neutralSummary = listOf("Quantitative summary only."),
                    hasEnoughDataForComparison = inputs.size >= 2
                )
            }
        )

        viewModel.loadPrompt(1L)
        advanceUntilIdle()

        assertNotNull(capturedInput?.recentSummary)
        assertEquals("Current section", capturedInput?.sections?.single()?.name)
        assertEquals(listOf("Current section", "Previous section"), capturedSummaryInputs.map { it.sections.single().name })
        assertTrue(repository.requestedSectionWodIds.containsAll(listOf(1L, 2L)))
        assertTrue(repository.requestedMovementWodIds.containsAll(listOf(1L, 2L)))
        assertTrue(viewModel.uiState.value.promptText.contains("has summary=true"))
    }

    @Test
    fun loadPrompt_whenPromptIsLong_setsLengthWarningMessage() = runTest {
        repository.wods[1L] = wod(id = 1L, title = "Long prompt")
        val viewModel = viewModel(
            promptGenerator = { "가".repeat(100_000) }
        )

        viewModel.loadPrompt(1L)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.lengthWarningMessage)
        assertEquals("Long prompt", viewModel.uiState.value.wodTitle)
    }

    @Test
    fun loadPrompt_whenRepositoryThrows_setsErrorMessage() = runTest {
        repository.throwOnGetWod = true
        val viewModel = viewModel()

        viewModel.loadPrompt(1L)
        advanceUntilIdle()

        assertEquals("repository failed", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun onCopied_setsCopyMessage() {
        val viewModel = viewModel()

        viewModel.onCopied()

        assertEquals("Prompt copied", viewModel.uiState.value.copyMessage)
    }

    private fun viewModel(
        promptGenerator: (PromptInput) -> String = { input ->
            buildString {
                append(input.currentWod.title)
                input.movements.forEach { append(" ${it.name}") }
                input.lifestyleLog?.mealSummary?.let { append(" $it") }
            }
        },
        summaryGenerator: (List<WodAnalysisInput>) -> AnalysisSummary = { inputs ->
            AnalysisSummary(
                items = emptyList(),
                categoryBreakdown = emptyList(),
                neutralSummary = listOf("Recent count: ${inputs.size}"),
                hasEnoughDataForComparison = inputs.size >= 2
            )
        }
    ): PromptViewModel = PromptViewModel(
        repository = repository,
        promptGenerator = promptGenerator,
        summaryGenerator = summaryGenerator
    )

    private fun wod(
        id: Long,
        title: String = "Test WOD",
        date: LocalDate = LocalDate.of(2026, 5, 4)
    ): Wod = Wod(
        id = id,
        date = date,
        title = title,
        type = WodType.FOR_TIME,
        rawText = null,
        notes = null,
        createdAt = now,
        updatedAt = now
    )

    private fun movement(
        wodId: Long,
        name: String
    ): Movement = Movement(
        wodId = wodId,
        name = name,
        category = MovementCategory.STRENGTH,
        reps = 10,
        sets = 3,
        orderIndex = 0
    )

    private fun section(
        wodId: Long,
        name: String
    ): WodSection = WodSection(
        wodId = wodId,
        name = name,
        orderIndex = 0
    )

    private fun lifestyleLog(
        weekStartDate: LocalDate,
        mealSummary: String
    ): LifestyleLog = LifestyleLog(
        weekStartDate = weekStartDate,
        mealSummary = mealSummary,
        createdAt = now,
        updatedAt = now
    )
}

private class FakePromptRepository : WodlogRepository {
    val wods = mutableMapOf<Long, Wod>()
    val movementsByWodId = mutableMapOf<Long, List<Movement>>()
    val sectionsByWodId = mutableMapOf<Long, List<WodSection>>()
    val resultsByWodId = mutableMapOf<Long, WodResult>()
    val lifestyleByWeekStart = mutableMapOf<LocalDate, LifestyleLog>()
    var profile: UserProfile? = null
    var recentWods: List<Wod> = emptyList()
    var throwOnGetWod = false
    var requestedLifestyleWeekStart: LocalDate? = null
    val requestedSectionWodIds = mutableListOf<Long>()
    val requestedMovementWodIds = mutableListOf<Long>()

    override suspend fun getUserProfile(): UserProfile? = profile

    override suspend fun saveUserProfile(profile: UserProfile): Long = unused()

    override suspend fun getWodById(id: Long): Wod? {
        if (throwOnGetWod) error("repository failed")
        return wods[id]
    }

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> = unused()

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> = unused()

    override suspend fun getRecentWods(limit: Int): List<Wod> = recentWods.take(limit)

    override suspend fun saveWod(wod: Wod): Long = unused()

    override suspend fun deleteWod(id: Long): Unit = unused()

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> {
        requestedSectionWodIds += wodId
        return sectionsByWodId[wodId].orEmpty()
    }

    override suspend fun saveWodSection(section: WodSection): Long = unused()

    override suspend fun deleteWodSection(id: Long): Unit = unused()

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> {
        requestedMovementWodIds += wodId
        return movementsByWodId[wodId].orEmpty()
    }

    override suspend fun saveMovement(movement: Movement): Long = unused()

    override suspend fun deleteMovement(id: Long): Unit = unused()

    override suspend fun getResultForWod(wodId: Long): WodResult? = resultsByWodId[wodId]

    override suspend fun saveWodResult(result: WodResult): Long = unused()

    override suspend fun deleteWodResult(id: Long): Unit = unused()

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? {
        requestedLifestyleWeekStart = weekStartDate
        return lifestyleByWeekStart[weekStartDate]
    }

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long = unused()

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> = unused()

    override suspend fun saveAiReport(report: AiReport): Long = unused()

    override suspend fun deleteAiReport(id: Long): Unit = unused()

    private fun unused(): Nothing {
        error("This repository method is not used by PromptViewModel tests.")
    }
}
