package com.wodlog.app.domain.backup

import com.wodlog.app.domain.repository.WodlogRepository
import java.time.Instant

class BackupExportUseCase(
    private val repository: WodlogRepository,
    private val exportedAtProvider: () -> Instant = { Instant.now() },
) {
    suspend fun buildBackup(): WodlogBackup {
        val profile = repository.getUserProfile()
        val wods = repository.getAllWods()
        val sections = wods.flatMap { repository.getSectionsForWod(it.id) }
        val movements = wods.flatMap { repository.getMovementsForWod(it.id) }
        val results = wods.mapNotNull { repository.getResultForWod(it.id) }
        val lifestyleLogs = repository.getAllLifestyleLogs()
        val aiReports = wods.flatMap { repository.getAiReportsForWod(it.id) }

        return createWodlogBackup(
            exportedAt = exportedAtProvider(),
            profile = profile,
            wods = wods,
            sections = sections,
            movements = movements,
            results = results,
            lifestyleLogs = lifestyleLogs,
            aiReports = aiReports,
        )
    }

    suspend fun exportJson(): String =
        BackupJsonSerializer.encode(buildBackup())
}

class BackupImportPreviewUseCase {
    fun preview(json: String): BackupImportPreview {
        val backup = try {
            BackupJsonSerializer.decode(json)
        } catch (error: Exception) {
            return BackupImportPreview(
                backup = null,
                isValid = false,
                errors = listOf(
                    BackupImportError(
                        type = BackupImportErrorType.INVALID_JSON,
                        message = "Backup JSON could not be parsed.",
                    ),
                ),
                wodCount = 0,
                movementCount = 0,
                resultCount = 0,
                lifestyleLogCount = 0,
                aiReportCount = 0,
            )
        }

        val errors = validateBackup(backup)
        return BackupImportPreview(
            backup = backup,
            isValid = errors.isEmpty(),
            errors = errors,
            wodCount = backup.wods.size,
            movementCount = backup.movements.size,
            resultCount = backup.results.size,
            lifestyleLogCount = backup.lifestyleLogs.size,
            aiReportCount = backup.aiReports.size,
        )
    }

    private fun validateBackup(backup: WodlogBackup): List<BackupImportError> {
        val errors = mutableListOf<BackupImportError>()
        val supportedVersion = WodlogBackup.BACKUP_VERSION
        if (backup.version != supportedVersion) {
            errors += BackupImportError(
                type = BackupImportErrorType.UNSUPPORTED_VERSION,
                message = "Backup version ${backup.version} is not supported.",
            )
        }

        val wodIds = backup.wods.map { it.id }
        val duplicateWodIds = wodIds
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys
        duplicateWodIds.forEach { wodId ->
            errors += BackupImportError(
                type = BackupImportErrorType.DUPLICATE_WOD_ID,
                message = "WOD id $wodId appears more than once.",
            )
        }

        val knownWodIds = wodIds.toSet()
        backup.sections
            .filter { it.wodId !in knownWodIds }
            .forEach { section ->
                errors += BackupImportError(
                    type = BackupImportErrorType.ORPHAN_SECTION,
                    message = "Section ${section.id} references missing WOD ${section.wodId}.",
                )
            }
        backup.movements
            .filter { it.wodId !in knownWodIds }
            .forEach { movement ->
                errors += BackupImportError(
                    type = BackupImportErrorType.ORPHAN_MOVEMENT,
                    message = "Movement ${movement.id} references missing WOD ${movement.wodId}.",
                )
            }
        backup.results
            .filter { it.wodId !in knownWodIds }
            .forEach { result ->
                errors += BackupImportError(
                    type = BackupImportErrorType.ORPHAN_RESULT,
                    message = "Result ${result.id} references missing WOD ${result.wodId}.",
                )
            }
        backup.aiReports
            .filter { it.targetWodId !in knownWodIds }
            .forEach { report ->
                errors += BackupImportError(
                    type = BackupImportErrorType.ORPHAN_AI_REPORT,
                    message = "AI report ${report.id} references missing WOD ${report.targetWodId}.",
                )
            }

        return errors
    }
}
