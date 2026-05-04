package com.wodlog.app.data.repository

import com.wodlog.app.data.dao.AiReportDao
import com.wodlog.app.data.dao.LifestyleLogDao
import com.wodlog.app.data.dao.MovementDao
import com.wodlog.app.data.dao.UserProfileDao
import com.wodlog.app.data.dao.WodDao
import com.wodlog.app.data.dao.WodResultDao
import com.wodlog.app.data.dao.WodSectionDao
import com.wodlog.app.data.mapper.toDomain
import com.wodlog.app.data.mapper.toEntity
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.repository.WodlogRepository
import java.time.LocalDate

class RoomWodlogRepository(
    private val userProfileDao: UserProfileDao,
    private val wodDao: WodDao,
    private val wodSectionDao: WodSectionDao,
    private val movementDao: MovementDao,
    private val wodResultDao: WodResultDao,
    private val lifestyleLogDao: LifestyleLogDao,
    private val aiReportDao: AiReportDao
) : WodlogRepository {
    override suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getProfile()?.toDomain()
    }

    override suspend fun saveUserProfile(profile: UserProfile): Long {
        return userProfileDao.insert(profile.toEntity())
    }

    override suspend fun getWodById(id: Long): Wod? {
        return wodDao.getById(id)?.toDomain()
    }

    override suspend fun getWodsByDate(date: LocalDate): List<Wod> {
        return wodDao.getByDate(date).map { it.toDomain() }
    }

    override suspend fun getWodsByMonth(year: Int, month: Int): List<Wod> {
        val startDate = LocalDate.of(year, month, 1)
        return wodDao.getByDateRange(
            startDate = startDate,
            endDateExclusive = startDate.plusMonths(1)
        ).map { it.toDomain() }
    }

    override suspend fun getRecentWods(limit: Int): List<Wod> {
        if (limit <= 0) return emptyList()
        return wodDao.getAllLatestFirst()
            .take(limit)
            .map { it.toDomain() }
    }

    override suspend fun getAllWods(): List<Wod> {
        return wodDao.getAllLatestFirst().map { it.toDomain() }
    }

    override suspend fun saveWod(wod: Wod): Long {
        if (wod.id == 0L) {
            return wodDao.insert(wod.toEntity())
        }

        val updatedRows = wodDao.update(wod.toEntity())
        if (updatedRows == 0) {
            wodDao.insert(wod.toEntity())
        }
        return wod.id
    }

    override suspend fun deleteWod(id: Long) {
        wodDao.deleteById(id)
    }

    override suspend fun getSectionsForWod(wodId: Long): List<WodSection> {
        return wodSectionDao.getByWodId(wodId).map { it.toDomain() }
    }

    override suspend fun saveWodSection(section: WodSection): Long {
        if (section.id == 0L) {
            return wodSectionDao.insert(section.toEntity())
        }

        val updatedRows = wodSectionDao.update(section.toEntity())
        if (updatedRows == 0) {
            wodSectionDao.insert(section.toEntity())
        }
        return section.id
    }

    override suspend fun deleteWodSection(id: Long) {
        wodSectionDao.deleteById(id)
    }

    override suspend fun getMovementsForWod(wodId: Long): List<Movement> {
        return movementDao.getByWodId(wodId).map { it.toDomain() }
    }

    override suspend fun saveMovement(movement: Movement): Long {
        if (movement.id == 0L) {
            return movementDao.insert(movement.toEntity())
        }

        val updatedRows = movementDao.update(movement.toEntity())
        if (updatedRows == 0) {
            movementDao.insert(movement.toEntity())
        }
        return movement.id
    }

    override suspend fun deleteMovement(id: Long) {
        movementDao.deleteById(id)
    }

    override suspend fun getResultForWod(wodId: Long): WodResult? {
        return wodResultDao.getByWodId(wodId)?.toDomain()
    }

    override suspend fun saveWodResult(result: WodResult): Long {
        if (result.id == 0L) {
            return wodResultDao.insert(result.toEntity())
        }

        val updatedRows = wodResultDao.update(result.toEntity())
        if (updatedRows == 0) {
            wodResultDao.insert(result.toEntity())
        }
        return result.id
    }

    override suspend fun deleteWodResult(id: Long) {
        wodResultDao.deleteById(id)
    }

    override suspend fun getLifestyleLogByWeekStart(weekStartDate: LocalDate): LifestyleLog? {
        return lifestyleLogDao.getByWeekStartDate(weekStartDate)?.toDomain()
    }

    override suspend fun getAllLifestyleLogs(): List<LifestyleLog> {
        return lifestyleLogDao.getAllLatestFirst().map { it.toDomain() }
    }

    override suspend fun saveLifestyleLog(log: LifestyleLog): Long {
        if (log.id == 0L) {
            return lifestyleLogDao.insert(log.toEntity())
        }

        val updatedRows = lifestyleLogDao.update(log.toEntity())
        if (updatedRows == 0) {
            lifestyleLogDao.insert(log.toEntity())
        }
        return log.id
    }

    override suspend fun getAiReportsForWod(wodId: Long): List<AiReport> {
        return aiReportDao.getByWodId(wodId).map { it.toDomain() }
    }

    override suspend fun saveAiReport(report: AiReport): Long {
        if (report.id == 0L) {
            return aiReportDao.insert(report.toEntity())
        }

        val updatedRows = aiReportDao.update(report.toEntity())
        if (updatedRows == 0) {
            aiReportDao.insert(report.toEntity())
        }
        return report.id
    }

    override suspend fun deleteAiReport(id: Long) {
        aiReportDao.deleteById(id)
    }
}
