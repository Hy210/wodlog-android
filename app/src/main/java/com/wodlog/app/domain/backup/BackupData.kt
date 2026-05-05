package com.wodlog.app.domain.backup

import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.domain.model.WodSourceType
import kotlinx.serialization.Serializable

@Serializable
data class WodlogBackup(
    val version: Int = BACKUP_VERSION,
    val exportedAt: String,
    val profile: BackupUserProfile? = null,
    val wods: List<BackupWod> = emptyList(),
    val sections: List<BackupWodSection> = emptyList(),
    val movements: List<BackupMovement> = emptyList(),
    val results: List<BackupWodResult> = emptyList(),
    val lifestyleLogs: List<BackupLifestyleLog> = emptyList(),
    val aiReports: List<BackupAiReport> = emptyList(),
) {
    companion object {
        const val BACKUP_VERSION = 1
    }
}

@Serializable
data class BackupUserProfile(
    val id: Long,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val crossfitStartDate: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class BackupWod(
    val id: Long,
    val date: String,
    val title: String,
    val type: WodType,
    val rawText: String? = null,
    val notes: String? = null,
    val sourceType: WodSourceType = WodSourceType.MANUAL,
    val sourceUrl: String? = null,
    val importedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class BackupWodSection(
    val id: Long,
    val wodId: Long,
    val name: String,
    val orderIndex: Int,
)

@Serializable
data class BackupMovement(
    val id: Long,
    val wodId: Long,
    val sectionId: Long? = null,
    val name: String,
    val category: MovementCategory? = null,
    val weightKg: Double? = null,
    val reps: Int? = null,
    val sets: Int? = null,
    val rounds: Int? = null,
    val distanceMeters: Double? = null,
    val calories: Double? = null,
    val durationSeconds: Int? = null,
    val orderIndex: Int,
    val notes: String? = null,
)

@Serializable
data class BackupWodResult(
    val id: Long,
    val wodId: Long,
    val scoreType: ScoreType,
    val timeSeconds: Int? = null,
    val rounds: Int? = null,
    val extraReps: Int? = null,
    val totalReps: Int? = null,
    val loadKg: Double? = null,
    val distanceMeters: Double? = null,
    val calories: Double? = null,
    val rxStatus: RxStatus = RxStatus.UNKNOWN,
    val rpe: Int? = null,
    val condition: Condition? = null,
    val memo: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class BackupLifestyleLog(
    val id: Long,
    val weekStartDate: String,
    val mealSummary: String? = null,
    val alcohol: Boolean? = null,
    val alcoholAmountPerWeek: String? = null,
    val smoking: Boolean? = null,
    val smokingAmountPerWeek: String? = null,
    val sleepAverageHours: Double? = null,
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class BackupAiReport(
    val id: Long,
    val targetWodId: Long,
    val promptText: String? = null,
    val reportText: String,
    val userMemo: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
