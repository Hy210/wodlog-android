package com.wodlog.app.domain.model

data class CafePostCandidate(
    val title: String,
    val url: String,
    val dateText: String?,
    val matchedKeyword: String?,
    val confidence: Double
)
