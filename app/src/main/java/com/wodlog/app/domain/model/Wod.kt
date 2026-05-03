package com.wodlog.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class Wod(
    val id: Long = 0L,
    val date: LocalDate,
    val title: String,
    val type: WodType,
    val rawText: String? = null,
    val notes: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
