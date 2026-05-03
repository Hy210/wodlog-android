package com.wodlog.app.domain.model

import java.time.Instant

data class AiReport(
    val id: Long = 0L,
    val targetWodId: Long,
    val promptText: String? = null,
    val reportText: String,
    val userMemo: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
