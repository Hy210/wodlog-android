package com.wodlog.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.wodlog.app.data.local.WodlogDatabase
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodType
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoomWodlogRepositoryInstrumentedTest {
    private lateinit var database: WodlogDatabase
    private lateinit var repository: RoomWodlogRepository

    private val now = Instant.ofEpochMilli(1_777_777_777_000)

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, WodlogDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomWodlogRepository(
            userProfileDao = database.userProfileDao(),
            wodDao = database.wodDao(),
            wodSectionDao = database.wodSectionDao(),
            movementDao = database.movementDao(),
            wodResultDao = database.wodResultDao(),
            lifestyleLogDao = database.lifestyleLogDao(),
            aiReportDao = database.aiReportDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveWod_thenGetWodById_returnsDomainWod() = runBlocking {
        val id = repository.saveWod(newWod(date = LocalDate.of(2026, 5, 3), title = "Fran"))

        val wod = repository.getWodById(id)

        assertNotNull(wod)
        assertEquals("Fran", wod?.title)
        assertEquals(WodType.FOR_TIME, wod?.type)
    }

    @Test
    fun getWodsByDate_returnsDomainWodsForDate() = runBlocking {
        val date = LocalDate.of(2026, 5, 3)
        repository.saveWod(newWod(date = date, title = "Fran"))
        repository.saveWod(newWod(date = date, title = "Grace"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 4), title = "Helen"))

        val wods = repository.getWodsByDate(date)

        assertEquals(listOf("Grace", "Fran"), wods.map { it.title })
    }

    @Test
    fun getWodsByMonth_returnsOnlyRequestedMonth() = runBlocking {
        repository.saveWod(newWod(date = LocalDate.of(2026, 4, 30), title = "April"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 1), title = "May One"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 31), title = "May Last"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 6, 1), title = "June"))

        val mayWods = repository.getWodsByMonth(year = 2026, month = 5)

        assertEquals(listOf("May One", "May Last"), mayWods.map { it.title })
    }

    @Test
    fun getRecentWods_returnsRequestedLatestCount() = runBlocking {
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 1), title = "One"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 2), title = "Two"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 3), title = "Three"))
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 4), title = "Four"))

        val recent = repository.getRecentWods(3)

        assertEquals(listOf("Four", "Three", "Two"), recent.map { it.title })
    }

    @Test
    fun getRecentWods_withZeroLimit_returnsEmptyList() = runBlocking {
        repository.saveWod(newWod(date = LocalDate.of(2026, 5, 1), title = "One"))

        val recent = repository.getRecentWods(0)

        assertTrue(recent.isEmpty())
    }

    @Test
    fun saveUserProfile_thenGetUserProfile_returnsDomainProfile() = runBlocking {
        repository.saveUserProfile(
            UserProfile(
                heightCm = 180.0,
                weightKg = 82.5,
                crossfitStartDate = LocalDate.of(2025, 1, 1),
                createdAt = now,
                updatedAt = now
            )
        )

        val profile = repository.getUserProfile()

        assertEquals(180.0, profile?.heightCm ?: 0.0, 0.0)
        assertEquals(LocalDate.of(2025, 1, 1), profile?.crossfitStartDate)
    }

    @Test
    fun saveLifestyleLog_thenGetByWeekStart_returnsDomainLog() = runBlocking {
        val weekStartDate = LocalDate.of(2026, 4, 27)
        repository.saveLifestyleLog(
            LifestyleLog(
                weekStartDate = weekStartDate,
                mealSummary = "balanced",
                alcohol = false,
                createdAt = now,
                updatedAt = now
            )
        )

        val log = repository.getLifestyleLogByWeekStart(weekStartDate)

        assertEquals("balanced", log?.mealSummary)
        assertEquals(false, log?.alcohol)
    }

    @Test
    fun saveAiReport_thenGetReportsForWod_returnsDomainReports() = runBlocking {
        val wodId = repository.saveWod(newWod(date = LocalDate.of(2026, 5, 3), title = "Report WOD"))
        repository.saveAiReport(
            AiReport(
                targetWodId = wodId,
                promptText = "prompt one",
                reportText = "report one",
                createdAt = now,
                updatedAt = now
            )
        )
        repository.saveAiReport(
            AiReport(
                targetWodId = wodId,
                promptText = "prompt two",
                reportText = "report two",
                createdAt = now.plusSeconds(1),
                updatedAt = now.plusSeconds(1)
            )
        )

        val reports = repository.getAiReportsForWod(wodId)

        assertEquals(listOf("report two", "report one"), reports.map { it.reportText })
    }

    private fun newWod(
        date: LocalDate,
        title: String,
        type: WodType = WodType.FOR_TIME
    ): Wod {
        return Wod(
            date = date,
            title = title,
            type = type,
            createdAt = now,
            updatedAt = now
        )
    }
}
