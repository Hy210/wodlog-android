package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.ImportedWodText
import java.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ImportedWodTextExtractor(
    private val nowProvider: () -> Instant = { Instant.now() },
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun extract(
        evaluateJavascriptResult: String,
        fallbackSourceUrl: String
    ): ImportedWodText? {
        val rawJson = decodeJavascriptStringResult(evaluateJavascriptResult)
        val rawText = runCatching {
            json.decodeFromString<RawImportedWodText>(rawJson)
        }.getOrNull() ?: return null

        val importedText = rawText.text
            .orEmpty()
            .cleanImportedText()
            .take(MaxImportedWodTextLength)
        if (importedText.length < MinImportedWodTextLength) return null

        return ImportedWodText(
            sourceUrl = rawText.sourceUrl?.trim().takeUnless { it.isNullOrEmpty() } ?: fallbackSourceUrl,
            title = rawText.title.orEmpty().cleanSingleLine(),
            importedText = importedText,
            importedAt = nowProvider()
        )
    }

    private fun decodeJavascriptStringResult(result: String): String {
        val trimmed = result.trim()
        if (trimmed == "null" || trimmed.isEmpty()) return "{}"
        return runCatching {
            json.decodeFromString<String>(trimmed)
        }.getOrElse {
            trimmed
        }
    }

    private fun String.cleanImportedText(): String {
        return lines()
            .map { it.trim() }
            .joinToString("\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    private fun String.cleanSingleLine(): String {
        return trim().replace(Regex("\\s+"), " ")
    }
}

@Serializable
private data class RawImportedWodText(
    @SerialName("sourceUrl") val sourceUrl: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("text") val text: String? = null
)

const val MinImportedWodTextLength = 20
const val MaxImportedWodTextLength = 12_000

const val ImportedWodTextExtractionScript = """
(function() {
  const selectors = [
    '.se-main-container',
    '.ContentRenderer',
    '#postViewArea',
    '.article_viewer',
    '.ArticleContentBox',
    'article',
    'main'
  ];
  let root = null;
  for (const selector of selectors) {
    const candidate = document.querySelector(selector);
    if (candidate && (candidate.innerText || '').trim().length > 20) {
      root = candidate;
      break;
    }
  }
  if (!root) {
    root = document.body;
  }
  const titleNode = document.querySelector('.title_text, .se-title-text, h1, title');
  return JSON.stringify({
    sourceUrl: window.location.href,
    title: titleNode ? (titleNode.innerText || titleNode.textContent || '').trim() : document.title,
    text: root ? (root.innerText || '').trim() : ''
  });
})()
"""
