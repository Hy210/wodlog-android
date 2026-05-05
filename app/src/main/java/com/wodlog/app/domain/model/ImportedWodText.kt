package com.wodlog.app.domain.model

import java.time.Instant

data class ImportedWodText(
    val sourceType: WodSourceType = WodSourceType.NAVER_CAFE_WEBVIEW,
    val sourceUrl: String,
    val title: String,
    val importedText: String,
    val importedAt: Instant
)
