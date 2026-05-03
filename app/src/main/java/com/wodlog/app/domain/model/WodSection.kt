package com.wodlog.app.domain.model

data class WodSection(
    val id: Long = 0L,
    val wodId: Long,
    val name: String,
    val orderIndex: Int
)
