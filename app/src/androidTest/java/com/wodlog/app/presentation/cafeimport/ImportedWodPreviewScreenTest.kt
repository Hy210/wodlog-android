package com.wodlog.app.presentation.cafeimport

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.Instant
import org.junit.Rule
import org.junit.Test

class ImportedWodPreviewScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun importedWodPreviewScreen_displaysImportedTextAndSource() {
        composeRule.setContent {
            WodlogTheme {
                ImportedWodPreviewScreen(
                    importedWodText = ImportedWodText(
                        sourceUrl = "https://cafe.naver.com/box/123",
                        title = "Today WOD",
                        importedText = "Warm-up\n21-15-9\nThruster\nPull-up",
                        importedAt = Instant.parse("2026-05-05T01:02:03Z")
                    ),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("screen-imported-wod-preview").assertIsDisplayed()
        composeRule.onNodeWithTag("text-imported-wod-source-url").assertIsDisplayed()
        composeRule.onNodeWithTag("text-imported-wod-body").assertIsDisplayed()
        composeRule.onNodeWithTag("action-copy-imported-wod").assertIsDisplayed()
    }

    @Test
    fun importedWodPreviewScreen_whenImportedTextMissing_displaysError() {
        composeRule.setContent {
            WodlogTheme {
                ImportedWodPreviewScreen(
                    importedWodText = null,
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("screen-imported-wod-preview").assertIsDisplayed()
        composeRule.onNodeWithTag("text-imported-wod-preview-missing").assertIsDisplayed()
        composeRule.onNodeWithTag("action-back-imported-wod-preview").assertIsDisplayed()
    }
}
