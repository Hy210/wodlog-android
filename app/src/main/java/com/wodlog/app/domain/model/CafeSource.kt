package com.wodlog.app.domain.model

import java.time.Instant

data class CafeSource(
    val id: Long = 0L,
    val boxName: String,
    val boardUrl: String,
    val titleKeywords: List<String>,
    val preferMobileUrl: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
