package com.wodlog.app.domain.repository

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface WodlogRepository {
    suspend fun getUserProfile(): UserProfile?

    suspend fun saveUserProfile(profile: UserProfile): Long

    suspend fun getWodById(id: Long): Wod?

    suspend fun getWodsByDate(date: LocalDate): List<Wod>

    suspend fun getWodsByMonth(year: Int, month: Int): List<Wod>

    suspend fun getRecentWods(limit: Int = 3): List<Wod>

    suspend fun getAllWods(): List<Wod> = getRecentWods(Int.MAX_VALUE)

    suspend fun saveWod(wod: Wod): Long

    suspend fun deleteWod(id: Long)

    suspend fun getSectionsForWod(wodId: Long): List<WodSection>

    suspend fun saveWodSection(section: WodSection): Long

    suspend fun deleteWodSection(id: Long)

    suspend fun getMovementsForWod(wodId: Long): List<Movement>

    suspend fun saveMovement(movement: Movement): Long

    suspend fun deleteMovement(id: Long)

    suspend fun getResultForWod(wodId: Long): WodResult?

    suspend fun saveWodResult(result: WodResult): Long

    suspend fun deleteWodResult(id: Long)

    suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog?

    suspend fun getAllLifestyleLogs(): List<LifestyleLog> = emptyList()

    suspend fun saveLifestyleLog(log: LifestyleLog): Long

    suspend fun getAiReportsForWod(wodId: Long): List<AiReport>

    suspend fun saveAiReport(report: AiReport): Long

    suspend fun deleteAiReport(id: Long)

    fun observeCafeSources(): Flow<List<CafeSource>> = flowOf(emptyList())

    suspend fun getCafeSource(id: Long): CafeSource? = null

    suspend fun saveCafeSource(cafeSource: CafeSource): Long {
        throw UnsupportedOperationException("Cafe source persistence is not implemented.")
    }

    suspend fun deleteCafeSource(id: Long) = Unit

    fun observeCafeSourceCount(): Flow<Int> = flowOf(0)
}
