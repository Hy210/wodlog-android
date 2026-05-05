package com.wodlog.app.presentation.cafeimport

import java.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportedWodTextExtractorTest {
    private val importedAt = Instant.parse("2026-05-05T01:02:03Z")
    private val extractor = ImportedWodTextExtractor(
        nowProvider = { importedAt }
    )
    private val json = Json

    @Test
    fun extract_returnsCleanImportedWodTextFromJavascriptStringResult() {
        val result = javascriptStringResult(
            """
            {
              "sourceUrl":"https://cafe.naver.com/box/123",
              "title":"  Today WOD   ",
              "text":"\n  Warm-up\n\n\n  21-15-9\n  Thruster\n  Pull-up  \n"
            }
            """.trimIndent()
        )

        val importedWodText = extractor.extract(
            evaluateJavascriptResult = result,
            fallbackSourceUrl = "https://cafe.naver.com/box/list"
        )

        requireNotNull(importedWodText)
        assertEquals("https://cafe.naver.com/box/123", importedWodText.sourceUrl)
        assertEquals("Today WOD", importedWodText.title)
        assertEquals("Warm-up\n\n21-15-9\nThruster\nPull-up", importedWodText.importedText)
        assertEquals(importedAt, importedWodText.importedAt)
    }

    @Test
    fun extract_usesFallbackSourceUrlWhenJavascriptUrlIsBlank() {
        val result = javascriptStringResult(
            """
            {
              "sourceUrl":" ",
              "title":"WOD",
              "text":"This is long enough imported workout text."
            }
            """.trimIndent()
        )

        val importedWodText = extractor.extract(
            evaluateJavascriptResult = result,
            fallbackSourceUrl = "https://cafe.naver.com/fallback"
        )

        requireNotNull(importedWodText)
        assertEquals("https://cafe.naver.com/fallback", importedWodText.sourceUrl)
    }

    @Test
    fun extract_returnsNullWhenTextIsTooShort() {
        val result = javascriptStringResult(
            """
            {
              "sourceUrl":"https://cafe.naver.com/box/123",
              "title":"WOD",
              "text":"short"
            }
            """.trimIndent()
        )

        val importedWodText = extractor.extract(
            evaluateJavascriptResult = result,
            fallbackSourceUrl = "https://cafe.naver.com/box/list"
        )

        assertNull(importedWodText)
    }

    @Test
    fun extract_capsVeryLongTextForPreview() {
        val longText = "A".repeat(MaxImportedWodTextLength + 100)
        val result = javascriptStringResult(
            """
            {
              "sourceUrl":"https://cafe.naver.com/box/123",
              "title":"WOD",
              "text":"$longText"
            }
            """.trimIndent()
        )

        val importedWodText = extractor.extract(
            evaluateJavascriptResult = result,
            fallbackSourceUrl = "https://cafe.naver.com/box/list"
        )

        requireNotNull(importedWodText)
        assertEquals(MaxImportedWodTextLength, importedWodText.importedText.length)
        assertTrue(importedWodText.importedText.all { it == 'A' })
    }

    @Test
    fun extract_withMalformedJavascriptResult_returnsNull() {
        val importedWodText = extractor.extract(
            evaluateJavascriptResult = "not-json",
            fallbackSourceUrl = "https://cafe.naver.com/box/list"
        )

        assertNull(importedWodText)
    }

    private fun javascriptStringResult(rawJson: String): String {
        return json.encodeToString(rawJson)
    }
}
