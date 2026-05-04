package com.wodlog.app.domain.backup

data class BackupImportPreview(
    val backup: WodlogBackup?,
    val isValid: Boolean,
    val errors: List<BackupImportError>,
    val wodCount: Int,
    val movementCount: Int,
    val resultCount: Int,
    val lifestyleLogCount: Int,
    val aiReportCount: Int,
)

data class BackupImportResult(
    val isSuccess: Boolean,
    val errors: List<BackupImportError> = emptyList(),
    val importedWodCount: Int = 0,
    val importedMovementCount: Int = 0,
    val importedResultCount: Int = 0,
    val importedLifestyleLogCount: Int = 0,
    val importedAiReportCount: Int = 0,
)

data class BackupImportError(
    val type: BackupImportErrorType,
    val message: String,
)

enum class BackupImportErrorType {
    INVALID_JSON,
    UNSUPPORTED_VERSION,
    DUPLICATE_WOD_ID,
    ORPHAN_SECTION,
    ORPHAN_MOVEMENT,
    ORPHAN_RESULT,
    ORPHAN_AI_REPORT,
    IMPORT_FAILED,
}
