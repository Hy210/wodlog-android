package com.wodlog.app.presentation.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.presentation.theme.WodlogTheme
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeScreen_withoutCafeSources_hidesImportButtonAndKeepsCreateButton() {
        composeRule.setContent {
            WodlogTheme {
                HomeScreen(state = HomeUiState(isLoadingCafeSources = false))
            }
        }

        composeRule.onNodeWithTag("action-create-wod").assertIsDisplayed()
        composeRule.onAllNodesWithTag("action-import-wod").assertCountEquals(0)
    }

    @Test
    fun homeScreen_withOneCafeSource_showsImportButtonAndCallsCallback() {
        var importClicks = 0
        composeRule.setContent {
            WodlogTheme {
                HomeScreen(
                    state = HomeUiState(
                        cafeSources = listOf(cafeSource(1L, "Maple Box")),
                        isLoadingCafeSources = false
                    ),
                    onImportWodClick = { importClicks += 1 }
                )
            }
        }

        composeRule.onNodeWithTag("action-import-wod")
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, importClicks)
        }
    }

    @Test
    fun homeScreen_withMultipleCafeSources_displaysPickerWhenRequested() {
        var selectedCafeSourceId: Long? = null
        composeRule.setContent {
            WodlogTheme {
                HomeScreen(
                    state = HomeUiState(
                        cafeSources = listOf(
                            cafeSource(1L, "Maple Box"),
                            cafeSource(2L, "River Box")
                        ),
                        isLoadingCafeSources = false,
                        isCafeSourcePickerVisible = true
                    ),
                    onCafeSourceSelected = { selectedCafeSourceId = it.id }
                )
            }
        }

        composeRule.onNodeWithTag("dialog-cafe-source-picker").assertIsDisplayed()
        composeRule.onNodeWithTag("action-select-cafe-source-2").performClick()

        composeRule.runOnIdle {
            assertEquals(2L, selectedCafeSourceId)
        }
    }

    @Test
    fun homeScreen_createButtonKeepsManualWodFlowCallback() {
        var createClicks = 0
        composeRule.setContent {
            WodlogTheme {
                HomeScreen(
                    state = HomeUiState(
                        cafeSources = listOf(cafeSource(1L, "Maple Box")),
                        isLoadingCafeSources = false
                    ),
                    onCreateWodClick = { createClicks += 1 }
                )
            }
        }

        composeRule.onNodeWithTag("action-create-wod").performClick()

        composeRule.runOnIdle {
            assertEquals(1, createClicks)
        }
    }

    private fun cafeSource(id: Long, boxName: String): CafeSource {
        val now = Instant.parse("2026-05-05T00:00:00Z")
        return CafeSource(
            id = id,
            boxName = boxName,
            boardUrl = "https://cafe.naver.com/$id",
            titleKeywords = listOf("WOD"),
            preferMobileUrl = true,
            createdAt = now,
            updatedAt = now
        )
    }
}
