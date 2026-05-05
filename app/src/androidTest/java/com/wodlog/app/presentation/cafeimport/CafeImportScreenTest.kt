package com.wodlog.app.presentation.cafeimport

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.wodlog.app.presentation.theme.WodlogTheme
import org.junit.Rule
import org.junit.Test

class CafeImportScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cafeImportScreen_whenCafeSourceMissing_displaysKoreanError() {
        composeRule.setContent {
            WodlogTheme {
                CafeImportScreen(
                    cafeSource = null,
                    cafeSourceId = 404L,
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithTag("screen-cafe-import-missing-source").assertIsDisplayed()
        composeRule.onNodeWithTag("text-cafe-import-missing-source").assertIsDisplayed()
        composeRule.onNodeWithTag("text-cafe-import-manual-fallback").assertIsDisplayed()
        composeRule.onNodeWithTag("action-open-cafe-source-settings").assertIsDisplayed()
        composeRule.onNodeWithTag("action-create-manual-wod-from-cafe-import").assertIsDisplayed()
        composeRule.onNodeWithTag("action-back-from-cafe-import-missing-source").assertIsDisplayed()
    }
}
