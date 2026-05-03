package com.wodlog.app.data.mapper

import com.wodlog.app.data.entity.AiReportEntity
import com.wodlog.app.data.entity.LifestyleLogEntity
import com.wodlog.app.data.entity.MovementEntity
import com.wodlog.app.data.entity.UserProfileEntity
import com.wodlog.app.data.entity.WodEntity
import com.wodlog.app.data.entity.WodResultEntity
import com.wodlog.app.data.entity.WodSectionEntity
import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.data.entity.Condition as DataCondition
import com.wodlog.app.data.entity.MovementCategory as DataMovementCategory
import com.wodlog.app.data.entity.RxStatus as DataRxStatus
import com.wodlog.app.data.entity.ScoreType as DataScoreType
import com.wodlog.app.data.entity.WodType as DataWodType
import com.wodlog.app.domain.model.Condition as DomainCondition
import com.wodlog.app.domain.model.MovementCategory as DomainMovementCategory
import com.wodlog.app.domain.model.RxStatus as DomainRxStatus
import com.wodlog.app.domain.model.ScoreType as DomainScoreType
import com.wodlog.app.domain.model.WodType as DomainWodType

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    heightCm = heightCm,
    weightKg = weightKg,
    crossfitStartDate = crossfitStartDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    heightCm = heightCm,
    weightKg = weightKg,
    crossfitStartDate = crossfitStartDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WodEntity.toDomain(): Wod = Wod(
    id = id,
    date = date,
    title = title,
    type = type.toDomain(),
    rawText = rawText,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Wod.toEntity(): WodEntity = WodEntity(
    id = id,
    date = date,
    title = title,
    type = type.toEntity(),
    rawText = rawText,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WodSectionEntity.toDomain(): WodSection = WodSection(
    id = id,
    wodId = wodId,
    name = name,
    orderIndex = orderIndex
)

fun WodSection.toEntity(): WodSectionEntity = WodSectionEntity(
    id = id,
    wodId = wodId,
    name = name,
    orderIndex = orderIndex
)

fun MovementEntity.toDomain(): Movement = Movement(
    id = id,
    wodId = wodId,
    sectionId = sectionId,
    name = movementName,
    category = category?.toDomain(),
    weightKg = weightKg,
    reps = reps,
    sets = sets,
    rounds = rounds,
    distanceMeters = distanceMeters,
    calories = calories,
    durationSeconds = durationSeconds,
    orderIndex = orderIndex,
    notes = notes
)

fun Movement.toEntity(): MovementEntity = MovementEntity(
    id = id,
    wodId = wodId,
    sectionId = sectionId,
    movementName = name,
    category = category?.toEntity(),
    weightKg = weightKg,
    reps = reps,
    sets = sets,
    rounds = rounds,
    distanceMeters = distanceMeters,
    calories = calories,
    durationSeconds = durationSeconds,
    orderIndex = orderIndex,
    notes = notes
)

fun WodResultEntity.toDomain(): WodResult = WodResult(
    id = id,
    wodId = wodId,
    scoreType = scoreType.toDomain(),
    timeSeconds = timeSeconds,
    rounds = rounds,
    extraReps = extraReps,
    totalReps = totalReps,
    loadKg = loadKg,
    distanceMeters = distanceMeters,
    calories = calories,
    rxStatus = rxStatus.toDomain(),
    rpe = rpe,
    condition = condition?.toDomain(),
    memo = memo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WodResult.toEntity(): WodResultEntity = WodResultEntity(
    id = id,
    wodId = wodId,
    scoreType = scoreType.toEntity(),
    timeSeconds = timeSeconds,
    rounds = rounds,
    extraReps = extraReps,
    totalReps = totalReps,
    loadKg = loadKg,
    distanceMeters = distanceMeters,
    calories = calories,
    rxStatus = rxStatus.toEntity(),
    rpe = rpe,
    condition = condition?.toEntity(),
    memo = memo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun LifestyleLogEntity.toDomain(): LifestyleLog = LifestyleLog(
    id = id,
    weekStartDate = weekStartDate,
    mealSummary = mealSummary,
    alcohol = alcohol,
    alcoholAmountPerWeek = alcoholAmountPerWeek,
    smoking = smoking,
    smokingAmountPerWeek = smokingAmountPerWeek,
    sleepAverageHours = sleepAverageHours,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun LifestyleLog.toEntity(): LifestyleLogEntity = LifestyleLogEntity(
    id = id,
    weekStartDate = weekStartDate,
    mealSummary = mealSummary,
    alcohol = alcohol,
    alcoholAmountPerWeek = alcoholAmountPerWeek,
    smoking = smoking,
    smokingAmountPerWeek = smokingAmountPerWeek,
    sleepAverageHours = sleepAverageHours,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AiReportEntity.toDomain(): AiReport = AiReport(
    id = id,
    targetWodId = targetWodId,
    promptText = promptText,
    reportText = reportText,
    userMemo = userMemo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AiReport.toEntity(): AiReportEntity = AiReportEntity(
    id = id,
    targetWodId = targetWodId,
    promptText = promptText,
    reportText = reportText,
    userMemo = userMemo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun DataWodType.toDomain(): DomainWodType = DomainWodType.valueOf(name)

private fun DomainWodType.toEntity(): DataWodType = DataWodType.valueOf(name)

private fun DataMovementCategory.toDomain(): DomainMovementCategory = DomainMovementCategory.valueOf(name)

private fun DomainMovementCategory.toEntity(): DataMovementCategory = DataMovementCategory.valueOf(name)

private fun DataScoreType.toDomain(): DomainScoreType = DomainScoreType.valueOf(name)

private fun DomainScoreType.toEntity(): DataScoreType = DataScoreType.valueOf(name)

private fun DataRxStatus.toDomain(): DomainRxStatus = DomainRxStatus.valueOf(name)

private fun DomainRxStatus.toEntity(): DataRxStatus = DataRxStatus.valueOf(name)

private fun DataCondition.toDomain(): DomainCondition = DomainCondition.valueOf(name)

private fun DomainCondition.toEntity(): DataCondition = DataCondition.valueOf(name)
