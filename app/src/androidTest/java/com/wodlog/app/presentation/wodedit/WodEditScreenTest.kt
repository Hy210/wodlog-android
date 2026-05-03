package com.wodlog.app.presentation.wodedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.theme.WodlogTheme
import com.wodlog.app.util.ValidationError
import org.junit.Rule
import org.junit.Test

class WodEditScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun wodEditScreen_displaysBasicInputsAndSaveButton() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen()
            }
        }

        composeRule.onNodeWithTag("input-wod-date").assertIsDisplayed()
        composeRule.onNodeWithTag("input-wod-title").assertIsDisplayed()
        composeRule.onNodeWithTag("action-save-wod").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun titleInput_acceptsText() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen()
            }
        }

        composeRule.onNodeWithTag("input-wod-title").performTextInput("Fran")

        composeRule.onNodeWithTag("input-wod-title").assertIsDisplayed()
    }

    @Test
    fun addSection_displaysSectionInputs() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen()
            }
        }

        composeRule.onNodeWithTag("action-add-section").performScrollTo().performClick()

        composeRule.onNodeWithTag("input-section-title-0").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("input-section-memo-0").assertIsDisplayed()
    }

    @Test
    fun addMovement_displaysMovementInputs() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen()
            }
        }

        composeRule.onNodeWithTag("action-add-movement").performScrollTo().performClick()

        composeRule.onNodeWithTag("input-movement-name-0").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("input-movement-reps-0").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("input-movement-time-0").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun validationErrorState_isDisplayed() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen(
                    initialState = WodEditUiState(
                        validationErrors = listOf(ValidationError.WOD_TITLE_BLANK)
                    )
                )
            }
        }

        composeRule.onNodeWithTag("text-wod-validation-errors").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun saveClick_displaysSavedMessageWhenInputIsValid() {
        composeRule.setContent {
            WodlogTheme {
                TestableWodEditScreen()
            }
        }

        composeRule.onNodeWithTag("input-wod-title").performTextInput("Fran")
        composeRule.onNodeWithTag("input-wod-type-FOR_TIME").performScrollTo().performClick()
        composeRule.onNodeWithTag("action-save-wod").performScrollTo().performClick()

        composeRule.onNodeWithTag("text-wod-message").performScrollTo().assertIsDisplayed()
    }
}

@Composable
private fun TestableWodEditScreen(
    initialState: WodEditUiState = WodEditUiState(dateInput = "2026-05-03")
) {
    var state by remember { mutableStateOf(initialState) }
    var nextSectionId by remember { mutableStateOf(0) }
    var nextMovementId by remember { mutableStateOf(0) }

    WodEditScreen(
        state = state,
        onDateChange = { state = state.copy(dateInput = it, message = null) },
        onTitleChange = { state = state.copy(titleInput = it, message = null) },
        onWodTypeChange = { state = state.copy(wodType = it, message = null) },
        onRawTextChange = { state = state.copy(rawTextInput = it, message = null) },
        onMemoChange = { state = state.copy(memoInput = it, message = null) },
        onAddSection = {
            state = state.copy(
                sections = state.sections + WodSectionInputState(localId = "section-${nextSectionId++}")
            )
        },
        onRemoveSection = { localId ->
            state = state.copy(sections = state.sections.filterNot { it.localId == localId })
        },
        onSectionTitleChange = { localId, value ->
            state = state.copy(
                sections = state.sections.map { if (it.localId == localId) it.copy(titleInput = value) else it }
            )
        },
        onSectionMemoChange = { localId, value ->
            state = state.copy(
                sections = state.sections.map { if (it.localId == localId) it.copy(memoInput = value) else it }
            )
        },
        onAddMovement = {
            state = state.copy(
                movements = state.movements + MovementInputState(localId = "movement-${nextMovementId++}")
            )
        },
        onRemoveMovement = { localId ->
            state = state.copy(movements = state.movements.filterNot { it.localId == localId })
        },
        onMovementNameChange = { localId, value -> state = state.updateMovement(localId) { copy(nameInput = value) } },
        onMovementWeightChange = { localId, value -> state = state.updateMovement(localId) { copy(weightInput = value) } },
        onMovementRepsChange = { localId, value -> state = state.updateMovement(localId) { copy(repsInput = value) } },
        onMovementSetsChange = { localId, value -> state = state.updateMovement(localId) { copy(setsInput = value) } },
        onMovementRoundsChange = { localId, value -> state = state.updateMovement(localId) { copy(roundsInput = value) } },
        onMovementDistanceChange = { localId, value -> state = state.updateMovement(localId) { copy(distanceInput = value) } },
        onMovementCaloriesChange = { localId, value -> state = state.updateMovement(localId) { copy(caloriesInput = value) } },
        onMovementTimeChange = { localId, value -> state = state.updateMovement(localId) { copy(timeSecondsInput = value) } },
        onMovementCategoryChange = { localId, value -> state = state.updateMovement(localId) { copy(category = value) } },
        onMovementMemoChange = { localId, value -> state = state.updateMovement(localId) { copy(memoInput = value) } },
        onSaveClick = {
            state = if (state.titleInput.isBlank() || state.wodType == null) {
                state.copy(validationErrors = listOf(ValidationError.WOD_TITLE_BLANK))
            } else {
                state.copy(validationErrors = emptyList(), message = "WOD saved", savedWodId = 1L)
            }
        }
    )
}

private fun WodEditUiState.updateMovement(
    localId: String,
    transform: MovementInputState.() -> MovementInputState
): WodEditUiState {
    return copy(movements = movements.map { if (it.localId == localId) it.transform() else it })
}
