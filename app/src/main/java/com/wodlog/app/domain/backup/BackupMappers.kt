package com.wodlog.app.domain.backup

import com.wodlog.app.domain.model.AiReport
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import java.time.Instant
import java.time.LocalDate

fun createWodlogBackup(
    exportedAt: Instant,
    profile: UserProfile? = null,
    wods: List<Wod> = emptyList(),
    sections: List<WodSection> = emptyList(),
    movements: List<Movement> = emptyList(),
    results: List<WodResult> = emptyList(),
    lifestyleLogs: List<LifestyleLog> = emptyList(),
    aiReports: List<AiReport> = emptyList(),
): WodlogBackup =
    WodlogBackup(
        exportedAt = exportedAt.toString(),
        profile = profile?.toBackup(),
        wods = wods.map { it.toBackup() },
        sections = sections.map { it.toBackup() },
        movements = movements.map { it.toBackup() },
        results = results.map { it.toBackup() },
        lifestyleLogs = lifestyleLogs.map { it.toBackup() },
        aiReports = aiReports.map { it.toBackup() },
    )

fun UserProfile.toBackup(): BackupUserProfile =
    BackupUserProfile(
        id = id,
        heightCm = heightCm,
        weightKg = weightKg,
        crossfitStartDate = crossfitStartDate?.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun BackupUserProfile.toDomain(): UserProfile =
    UserProfile(
        id = id,
        heightCm = heightCm,
        weightKg = weightKg,
        crossfitStartDate = crossfitStartDate?.let(LocalDate::parse),
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

fun Wod.toBackup(): BackupWod =
    BackupWod(
        id = id,
        date = date.toString(),
        title = title,
        type = type,
        rawText = rawText,
        notes = notes,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun BackupWod.toDomain(): Wod =
    Wod(
        id = id,
        date = LocalDate.parse(date),
        title = title,
        type = type,
        rawText = rawText,
        notes = notes,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

fun WodSection.toBackup(): BackupWodSection =
    BackupWodSection(
        id = id,
        wodId = wodId,
        name = name,
        orderIndex = orderIndex,
    )

fun BackupWodSection.toDomain(): WodSection =
    WodSection(
        id = id,
        wodId = wodId,
        name = name,
        orderIndex = orderIndex,
    )

fun Movement.toBackup(): BackupMovement =
    BackupMovement(
        id = id,
        wodId = wodId,
        sectionId = sectionId,
        name = name,
        category = category,
        weightKg = weightKg,
        reps = reps,
        sets = sets,
        rounds = rounds,
        distanceMeters = distanceMeters,
        calories = calories,
        durationSeconds = durationSeconds,
        orderIndex = orderIndex,
        notes = notes,
    )

fun BackupMovement.toDomain(): Movement =
    Movement(
        id = id,
        wodId = wodId,
        sectionId = sectionId,
        name = name,
        category = category,
        weightKg = weightKg,
        reps = reps,
        sets = sets,
        rounds = rounds,
        distanceMeters = distanceMeters,
        calories = calories,
        durationSeconds = durationSeconds,
        orderIndex = orderIndex,
        notes = notes,
    )

fun WodResult.toBackup(): BackupWodResult =
    BackupWodResult(
        id = id,
        wodId = wodId,
        scoreType = scoreType,
        timeSeconds = timeSeconds,
        rounds = rounds,
        extraReps = extraReps,
        totalReps = totalReps,
        loadKg = loadKg,
        distanceMeters = distanceMeters,
        calories = calories,
        rxStatus = rxStatus,
        rpe = rpe,
        condition = condition,
        memo = memo,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun BackupWodResult.toDomain(): WodResult =
    WodResult(
        id = id,
        wodId = wodId,
        scoreType = scoreType,
        timeSeconds = timeSeconds,
        rounds = rounds,
        extraReps = extraReps,
        totalReps = totalReps,
        loadKg = loadKg,
        distanceMeters = distanceMeters,
        calories = calories,
        rxStatus = rxStatus,
        rpe = rpe,
        condition = condition,
        memo = memo,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

fun LifestyleLog.toBackup(): BackupLifestyleLog =
    BackupLifestyleLog(
        id = id,
        weekStartDate = weekStartDate.toString(),
        mealSummary = mealSummary,
        alcohol = alcohol,
        alcoholAmountPerWeek = alcoholAmountPerWeek,
        smoking = smoking,
        smokingAmountPerWeek = smokingAmountPerWeek,
        sleepAverageHours = sleepAverageHours,
        notes = notes,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun BackupLifestyleLog.toDomain(): LifestyleLog =
    LifestyleLog(
        id = id,
        weekStartDate = LocalDate.parse(weekStartDate),
        mealSummary = mealSummary,
        alcohol = alcohol,
        alcoholAmountPerWeek = alcoholAmountPerWeek,
        smoking = smoking,
        smokingAmountPerWeek = smokingAmountPerWeek,
        sleepAverageHours = sleepAverageHours,
        notes = notes,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

fun AiReport.toBackup(): BackupAiReport =
    BackupAiReport(
        id = id,
        targetWodId = targetWodId,
        promptText = promptText,
        reportText = reportText,
        userMemo = userMemo,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun BackupAiReport.toDomain(): AiReport =
    AiReport(
        id = id,
        targetWodId = targetWodId,
        promptText = promptText,
        reportText = reportText,
        userMemo = userMemo,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )
