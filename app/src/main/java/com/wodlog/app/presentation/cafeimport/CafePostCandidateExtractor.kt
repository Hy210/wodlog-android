package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.CafePostCandidate
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CafePostCandidateExtractor(
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun extract(
        evaluateJavascriptResult: String,
        currentPageUrl: String,
        titleKeywords: List<String>
    ): List<CafePostCandidate> {
        val rawJson = decodeJavascriptStringResult(evaluateJavascriptResult)
        val rawLinks = runCatching {
            json.decodeFromString<List<RawCafeLinkCandidate>>(rawJson)
        }.getOrElse {
            return emptyList()
        }
        val keywords = titleKeywords
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { DefaultCafePostKeywords }
        val baseUri = runCatching { URI(currentPageUrl) }.getOrNull()

        return rawLinks
            .asSequence()
            .mapNotNull { rawLink ->
                val title = rawLink.title.orEmpty().trim()
                val href = rawLink.href.orEmpty().trim()
                if (title.isEmpty() || href.isEmpty() || href.isBlockedHref()) return@mapNotNull null

                val url = href.toAbsoluteUrl(baseUri) ?: return@mapNotNull null
                val matchedKeyword = keywords.firstOrNull { keyword ->
                    title.contains(keyword, ignoreCase = true)
                }
                val containsDefaultKeyword = DefaultCafePostKeywords.any {
                    title.contains(it, ignoreCase = true)
                }
                if (matchedKeyword == null && !containsDefaultKeyword) return@mapNotNull null
                val dateText = rawLink.dateText?.trim()?.takeIf { it.isNotEmpty() }
                val confidence = calculateConfidence(
                    title = title,
                    url = url,
                    matchedKeyword = matchedKeyword,
                    dateText = dateText
                )
                if (confidence <= 0.0) return@mapNotNull null

                CafePostCandidate(
                    title = title,
                    url = url,
                    dateText = dateText,
                    matchedKeyword = matchedKeyword,
                    confidence = confidence
                )
            }
            .distinctBy { it.url }
            .sortedWith(
                compareByDescending<CafePostCandidate> { it.confidence }
                    .thenBy { it.title }
            )
            .take(MaxCafePostCandidates)
            .toList()
    }

    private fun calculateConfidence(
        title: String,
        url: String,
        matchedKeyword: String?,
        dateText: String?
    ): Double {
        var confidence = 0.0
        if (matchedKeyword != null) confidence += 0.55
        if (DefaultCafePostKeywords.any { title.contains(it, ignoreCase = true) }) confidence += 0.15
        if (dateText?.looksLikeToday() == true) confidence += 0.2
        if (dateText?.isNotBlank() == true) confidence += 0.05
        if (url.contains("cafe", ignoreCase = true)) confidence += 0.05
        return confidence.coerceAtMost(1.0)
    }

    private fun decodeJavascriptStringResult(result: String): String {
        val trimmed = result.trim()
        if (trimmed == "null" || trimmed.isEmpty()) return "[]"
        return runCatching {
            json.decodeFromString<String>(trimmed)
        }.getOrElse {
            trimmed
        }
    }

    private fun String.isBlockedHref(): Boolean {
        val lower = lowercase()
        return lower.startsWith("#") ||
            lower.startsWith("javascript:") ||
            lower.startsWith("mailto:") ||
            lower.startsWith("tel:")
    }

    private fun String.toAbsoluteUrl(baseUri: URI?): String? {
        return runCatching {
            val uri = URI(this)
            val resolved = if (uri.isAbsolute) uri else baseUri?.resolve(uri)
            resolved?.takeIf { it.scheme == "http" || it.scheme == "https" }?.toString()
        }.getOrNull()
    }

    private fun String.looksLikeToday(): Boolean {
        val today = todayProvider()
        val candidates = listOf(
            today.format(DateTimeFormatter.ISO_LOCAL_DATE),
            today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
            today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            today.format(DateTimeFormatter.ofPattern("MM.dd")),
            today.format(DateTimeFormatter.ofPattern("M.d")),
            "${today.monthValue}월 ${today.dayOfMonth}일",
            "${today.monthValue}/${today.dayOfMonth}"
        )
        return candidates.any { contains(it) }
    }
}

@Serializable
private data class RawCafeLinkCandidate(
    @SerialName("title") val title: String? = null,
    @SerialName("href") val href: String? = null,
    @SerialName("dateText") val dateText: String? = null
)

val DefaultCafePostKeywords = listOf(
    "WOD",
    "오늘의 와드",
    "오늘 와드",
    "Metcon",
    "Workout"
)

const val MaxCafePostCandidates = 10

const val CafePostCandidateExtractionScript = """
(function() {
  const datePattern = /(\d{4}[.\-/]\d{1,2}[.\-/]\d{1,2}|\d{1,2}[.\/]\d{1,2}|\d{1,2}월\s*\d{1,2}일)/;
  return JSON.stringify(Array.from(document.querySelectorAll('a')).slice(0, 200).map(function(anchor) {
    const title = (anchor.innerText || anchor.textContent || '').trim().replace(/\s+/g, ' ');
    const href = anchor.getAttribute('href') || '';
    const nearby = ((anchor.closest('li, tr, article, div') || anchor.parentElement || anchor).textContent || '').slice(0, 240);
    const dateMatch = nearby.match(datePattern);
    return {
      title: title,
      href: href,
      dateText: dateMatch ? dateMatch[0] : null
    };
  }));
})()
"""
