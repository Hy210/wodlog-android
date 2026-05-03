package com.wodlog.app.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.wodlog.app.data.entity.AiReportEntity
import com.wodlog.app.data.entity.LifestyleLogEntity
import com.wodlog.app.data.entity.MovementEntity
import com.wodlog.app.data.entity.ScoreType
import com.wodlog.app.data.entity.UserProfileEntity
import com.wodlog.app.data.entity.WodEntity
import com.wodlog.app.data.entity.WodResultEntity
import com.wodlog.app.data.entity.WodSectionEntity
import com.wodlog.app.data.entity.WodType
import com.wodlog.app.data.local.WodlogDatabase
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WodlogDaoInstrumentedTest {
    private lateinit var database: WodlogDatabase

    private val now = Instant.ofEpochMilli(1_777_777_777_000)

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, WodlogDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun wodDao_insertsAndFindsById() = runBlocking {
        val id = insertWod(date = LocalDate.of(2026, 5, 3), title = "Fran")

        val entity = database.wodDao().getById(id)

        assertNotNull(entity)
        assertEquals("Fran", entity?.title)
    }

    @Test
    fun wodDao_findsWodsByDate() = runBlocking {
        val targetDate = LocalDate.of(2026, 5, 3)
        insertWod(date = targetDate, title = "Fran")
        insertWod(date = targetDate, title = "Grace")
        insertWod(date = LocalDate.of(2026, 5, 4), title = "Helen")

        val wods = database.wodDao().getByDate(targetDate)

        assertEquals(listOf("Grace", "Fran"), wods.map { it.title })
    }

    @Test
    fun wodDao_findsWodsByMonthRange() = runBlocking {
        insertWod(date = LocalDate.of(2026, 4, 30), title = "April")
        insertWod(date = LocalDate.of(2026, 5, 1), title = "May One")
        insertWod(date = LocalDate.of(2026, 5, 31), title = "May Last")
        insertWod(date = LocalDate.of(2026, 6, 1), title = "June")

        val mayWods = database.wodDao().getByDateRange(
            startDate = LocalDate.of(2026, 5, 1),
            endDateExclusive = LocalDate.of(2026, 6, 1)
        )

        assertEquals(listOf("May One", "May Last"), mayWods.map { it.title })
    }

    @Test
    fun wodDao_findsRecentThreeLatestFirst() = runBlocking {
        insertWod(date = LocalDate.of(2026, 5, 1), title = "One")
        insertWod(date = LocalDate.of(2026, 5, 2), title = "Two")
        insertWod(date = LocalDate.of(2026, 5, 3), title = "Three")
        insertWod(date = LocalDate.of(2026, 5, 4), title = "Four")

        val recent = database.wodDao().getRecentThree()

        assertEquals(listOf("Four", "Three", "Two"), recent.map { it.title })
    }

    @Test
    fun wodDelete_cascadesRelatedData() = runBlocking {
        val wodId = insertWod(date = LocalDate.of(2026, 5, 3), title = "Cascade")
        val sectionId = database.wodSectionDao().insert(
            WodSectionEntity(wodId = wodId, name = "Metcon", orderIndex = 0)
        )
        database.movementDao().insert(
            MovementEntity(
                wodId = wodId,
                sectionId = sectionId,
                movementName = "Thruster",
                orderIndex = 0
            )
        )
        database.wodResultDao().insert(
            WodResultEntity(
                wodId = wodId,
                scoreType = ScoreType.TIME,
                timeSeconds = 420,
                createdAt = now,
                updatedAt = now
            )
        )
        database.aiReportDao().insert(
            AiReportEntity(
                targetWodId = wodId,
                promptText = "prompt",
                reportText = "report",
                createdAt = now,
                updatedAt = now
            )
        )

        database.wodDao().deleteById(wodId)

        assertEquals(emptyList<WodSectionEntity>(), database.wodSectionDao().getByWodId(wodId))
        assertEquals(emptyList<MovementEntity>(), database.movementDao().getByWodId(wodId))
        assertNull(database.wodResultDao().getByWodId(wodId))
        assertEquals(emptyList<AiReportEntity>(), database.aiReportDao().getByWodId(wodId))
    }

    @Test
    fun lifestyleLogDao_findsLogByWeekStartDate() = runBlocking {
        val weekStartDate = LocalDate.of(2026, 4, 27)
        database.lifestyleLogDao().insert(
            LifestyleLogEntity(
                weekStartDate = weekStartDate,
                mealSummary = "balanced",
                createdAt = now,
                updatedAt = now
            )
        )

        val log = database.lifestyleLogDao().getByWeekStartDate(weekStartDate)

        assertEquals("balanced", log?.mealSummary)
    }

    @Test
    fun userProfileDao_savesAndFindsProfile() = runBlocking {
        database.userProfileDao().insert(
            UserProfileEntity(
                heightCm = 180.0,
                weightKg = 80.0,
                crossfitStartDate = LocalDate.of(2025, 1, 1),
                createdAt = now,
                updatedAt = now
            )
        )

        val profile = database.userProfileDao().getProfile()

        assertEquals(180.0, profile?.heightCm ?: 0.0, 0.0)
        assertEquals(LocalDate.of(2025, 1, 1), profile?.crossfitStartDate)
    }

    @Test
    fun aiReportDao_findsReportsByWodId() = runBlocking {
        val wodId = insertWod(date = LocalDate.of(2026, 5, 3), title = "Report WOD")
        database.aiReportDao().insert(
            AiReportEntity(
                targetWodId = wodId,
                promptText = "prompt one",
                reportText = "report one",
                createdAt = now,
                updatedAt = now
            )
        )
        database.aiReportDao().insert(
            AiReportEntity(
                targetWodId = wodId,
                promptText = "prompt two",
                reportText = "report two",
                createdAt = now.plusSeconds(1),
                updatedAt = now.plusSeconds(1)
            )
        )

        val reports = database.aiReportDao().getByWodId(wodId)

        assertEquals(listOf("report two", "report one"), reports.map { it.reportText })
    }

    private suspend fun insertWod(
        date: LocalDate,
        title: String,
        type: WodType = WodType.FOR_TIME
    ): Long {
        return database.wodDao().insert(
            WodEntity(
                date = date,
                title = title,
                type = type,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
