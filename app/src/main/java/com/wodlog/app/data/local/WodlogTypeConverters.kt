package com.wodlog.app.data.local

import androidx.room.TypeConverter
import com.wodlog.app.data.entity.Condition
import com.wodlog.app.data.entity.MovementCategory
import com.wodlog.app.data.entity.RxStatus
import com.wodlog.app.data.entity.ScoreType
import com.wodlog.app.data.entity.WodType
import java.time.Instant
import java.time.LocalDate
import java.util.Base64

class WodlogTypeConverters {
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun wodTypeToString(value: WodType?): String? = value?.name

    @TypeConverter
    fun stringToWodType(value: String?): WodType? = value?.let(WodType::valueOf)

    @TypeConverter
    fun movementCategoryToString(value: MovementCategory?): String? = value?.name

    @TypeConverter
    fun stringToMovementCategory(value: String?): MovementCategory? = value?.let(MovementCategory::valueOf)

    @TypeConverter
    fun scoreTypeToString(value: ScoreType?): String? = value?.name

    @TypeConverter
    fun stringToScoreType(value: String?): ScoreType? = value?.let(ScoreType::valueOf)

    @TypeConverter
    fun rxStatusToString(value: RxStatus?): String? = value?.name

    @TypeConverter
    fun stringToRxStatus(value: String?): RxStatus? = value?.let(RxStatus::valueOf)

    @TypeConverter
    fun conditionToString(value: Condition?): String? = value?.name

    @TypeConverter
    fun stringToCondition(value: String?): Condition? = value?.let(Condition::valueOf)

    @TypeConverter
    fun stringListToString(value: List<String>?): String? {
        return value?.joinToString(separator = ",") { item ->
            Base64.getEncoder().encodeToString(item.toByteArray(Charsets.UTF_8))
        }
    }

    @TypeConverter
    fun stringToStringList(value: String?): List<String>? {
        return value?.takeIf { it.isNotEmpty() }?.split(",")?.map { item ->
            String(Base64.getDecoder().decode(item), Charsets.UTF_8)
        } ?: value?.let { emptyList() }
    }
}
