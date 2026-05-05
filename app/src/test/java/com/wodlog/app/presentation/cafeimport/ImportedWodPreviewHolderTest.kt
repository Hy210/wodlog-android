package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.ImportedWodText
import java.time.Instant
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ImportedWodPreviewHolderTest {
    @After
    fun tearDown() {
        ImportedWodPreviewHolder.current = null
        ImportedWodPreviewHolder.editorPrefill = null
    }

    @Test
    fun consumeEditorPrefill_returnsValueOnce() {
        val importedWodText = ImportedWodText(
            sourceUrl = "https://cafe.naver.com/box/123",
            title = "Today WOD",
            importedText = "21-15-9\nThruster\nPull-up",
            importedAt = Instant.parse("2026-05-05T01:02:03Z")
        )
        ImportedWodPreviewHolder.editorPrefill = importedWodText

        assertSame(importedWodText, ImportedWodPreviewHolder.consumeEditorPrefill())
        assertNull(ImportedWodPreviewHolder.consumeEditorPrefill())
    }
}
