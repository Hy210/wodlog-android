package com.wodlog.app.domain.model

import java.time.Instant

data class ImportedWodText(
    val sourceUrl: String,
    val title: String,
    val importedText: String,
    val importedAt: Instant
)
