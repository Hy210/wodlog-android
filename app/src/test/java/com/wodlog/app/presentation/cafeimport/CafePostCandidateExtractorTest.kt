package com.wodlog.app.presentation.cafeimport

import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CafePostCandidateExtractorTest {
    private val extractor = CafePostCandidateExtractor(
        todayProvider = { LocalDate.of(2026, 5, 5) }
    )
    private val json = Json

    @Test
    fun extract_returnsKeywordMatchedCandidatesSortedByConfidence() {
        val result = javascriptStringResult(
            """
            [
              {"title":"자유게시판","href":"/free/1","dateText":"2026.05.05"},
              {"title":"오늘의 와드 WOD","href":"/article/10","dateText":"2026.05.05"},
              {"title":"Metcon 안내","href":"https://cafe.naver.com/box/20","dateText":"05.04"}
            ]
            """.trimIndent()
        )

        val candidates = extractor.extract(
            evaluateJavascriptResult = result,
            currentPageUrl = "https://cafe.naver.com/box/list",
            titleKeywords = listOf("WOD", "Metcon")
        )

        assertEquals(listOf("오늘의 와드 WOD", "Metcon 안내"), candidates.map { it.title })
        assertEquals("https://cafe.naver.com/article/10", candidates.first().url)
        assertEquals("WOD", candidates.first().matchedKeyword)
    }

    @Test
    fun extract_filtersBlockedEmptyAndDuplicateLinks() {
        val result = javascriptStringResult(
            """
            [
              {"title":"","href":"/article/1","dateText":"2026.05.05"},
              {"title":"WOD one","href":"javascript:void(0)","dateText":"2026.05.05"},
              {"title":"WOD two","href":"mailto:test@example.com","dateText":"2026.05.05"},
              {"title":"WOD three","href":"/article/3","dateText":"2026.05.05"},
              {"title":"WOD three duplicate","href":"/article/3","dateText":"2026.05.05"}
            ]
            """.trimIndent()
        )

        val candidates = extractor.extract(
            evaluateJavascriptResult = result,
            currentPageUrl = "https://cafe.naver.com/box/list",
            titleKeywords = listOf("WOD")
        )

        assertEquals(1, candidates.size)
        assertEquals("WOD three", candidates.single().title)
    }

    @Test
    fun extract_usesDefaultKeywordsWhenCafeSourceKeywordsAreBlank() {
        val result = javascriptStringResult(
            """
            [
              {"title":"Workout for today","href":"/article/1","dateText":"2026.05.05"}
            ]
            """.trimIndent()
        )

        val candidates = extractor.extract(
            evaluateJavascriptResult = result,
            currentPageUrl = "https://cafe.naver.com/box/list",
            titleKeywords = listOf(" ")
        )

        assertEquals("Workout for today", candidates.single().title)
        assertEquals("Workout", candidates.single().matchedKeyword)
    }

    @Test
    fun extract_withMalformedJavascriptResult_returnsEmptyList() {
        val candidates = extractor.extract(
            evaluateJavascriptResult = "not-json",
            currentPageUrl = "https://cafe.naver.com/box/list",
            titleKeywords = listOf("WOD")
        )

        assertTrue(candidates.isEmpty())
    }

    private fun javascriptStringResult(rawJson: String): String {
        return json.encodeToString(rawJson)
    }
}
