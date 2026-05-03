package com.wodlog.app.presentation.wodedit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.util.ValidationError

@Composable
fun WodEditRoute(
    viewModel: WodEditViewModel,
    onSaved: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.savedWodId) {
        state.savedWodId?.let(onSaved)
        state.savedWodId?.let { viewModel.onSavedNavigationHandled() }
    }

    WodEditScreen(
        state = state,
        onDateChange = viewModel::onDateChange,
        onTitleChange = viewModel::onTitleChange,
        onWodTypeChange = viewModel::onWodTypeChange,
        onRawTextChange = viewModel::onRawTextChange,
        onMemoChange = viewModel::onMemoChange,
        onAddSection = viewModel::addSection,
        onRemoveSection = viewModel::removeSection,
        onSectionTitleChange = viewModel::updateSectionTitle,
        onSectionMemoChange = viewModel::updateSectionMemo,
        onAddMovement = { viewModel.addMovement() },
        onRemoveMovement = viewModel::removeMovement,
        onMovementNameChange = viewModel::updateMovementName,
        onMovementWeightChange = viewModel::updateMovementWeight,
        onMovementRepsChange = viewModel::updateMovementReps,
        onMovementSetsChange = viewModel::updateMovementSets,
        onMovementRoundsChange = viewModel::updateMovementRounds,
        onMovementDistanceChange = viewModel::updateMovementDistance,
        onMovementCaloriesChange = viewModel::updateMovementCalories,
        onMovementTimeChange = viewModel::updateMovementTimeSeconds,
        onMovementCategoryChange = viewModel::updateMovementCategory,
        onMovementMemoChange = viewModel::updateMovementMemo,
        onSaveClick = viewModel::saveWod
    )
}

@Composable
fun WodEditScreen(
    state: WodEditUiState,
    onDateChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onWodTypeChange: (WodType) -> Unit,
    onRawTextChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onAddSection: () -> Unit,
    onRemoveSection: (String) -> Unit,
    onSectionTitleChange: (String, String) -> Unit,
    onSectionMemoChange: (String, String) -> Unit,
    onAddMovement: () -> Unit,
    onRemoveMovement: (String) -> Unit,
    onMovementNameChange: (String, String) -> Unit,
    onMovementWeightChange: (String, String) -> Unit,
    onMovementRepsChange: (String, String) -> Unit,
    onMovementSetsChange: (String, String) -> Unit,
    onMovementRoundsChange: (String, String) -> Unit,
    onMovementDistanceChange: (String, String) -> Unit,
    onMovementCaloriesChange: (String, String) -> Unit,
    onMovementTimeChange: (String, String) -> Unit,
    onMovementCategoryChange: (String, MovementCategory) -> Unit,
    onMovementMemoChange: (String, String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-wod-edit")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD Edit",
            style = MaterialTheme.typography.headlineMedium
        )

        SectionTitle("Basic")
        OutlinedTextField(
            value = state.dateInput,
            onValueChange = onDateChange,
            label = { Text("Date yyyy-MM-dd") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-wod-date")
        )
        OutlinedTextField(
            value = state.titleInput,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-wod-title")
        )
        EnumSelector(
            label = "WOD Type",
            values = WodType.entries,
            selectedValue = state.wodType,
            onSelect = onWodTypeChange,
            tagPrefix = "input-wod-type"
        )
        OutlinedTextField(
            value = state.rawTextInput,
            onValueChange = onRawTextChange,
            label = { Text("Raw text") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-wod-raw-text")
        )
        OutlinedTextField(
            value = state.memoInput,
            onValueChange = onMemoChange,
            label = { Text("Memo") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-wod-memo")
        )

        SectionHeader(
            title = "Sections",
            buttonText = "Add section",
            buttonTag = "action-add-section",
            onClick = onAddSection
        )
        state.sections.forEachIndexed { index, section ->
            SectionInput(
                index = index,
                section = section,
                onTitleChange = onSectionTitleChange,
                onMemoChange = onSectionMemoChange,
                onRemove = onRemoveSection
            )
        }

        SectionHeader(
            title = "Movements",
            buttonText = "Add movement",
            buttonTag = "action-add-movement",
            onClick = onAddMovement
        )
        state.movements.forEachIndexed { index, movement ->
            MovementInput(
                index = index,
                movement = movement,
                onNameChange = onMovementNameChange,
                onWeightChange = onMovementWeightChange,
                onRepsChange = onMovementRepsChange,
                onSetsChange = onMovementSetsChange,
                onRoundsChange = onMovementRoundsChange,
                onDistanceChange = onMovementDistanceChange,
                onCaloriesChange = onMovementCaloriesChange,
                onTimeChange = onMovementTimeChange,
                onCategoryChange = onMovementCategoryChange,
                onMemoChange = onMovementMemoChange,
                onRemove = onRemoveMovement
            )
        }

        if (state.validationErrors.isNotEmpty()) {
            Text(
                text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-wod-validation-errors")
            )
        }

        state.message?.let { message ->
            Text(
                text = state.savedWodId?.let { "$message (#$it)" } ?: message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-wod-message")
            )
        }

        Button(
            onClick = onSaveClick,
            enabled = !state.isSaving,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("action-save-wod")
        ) {
            Text(if (state.isSaving) "Saving" else "Save WOD")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun SectionHeader(
    title: String,
    buttonText: String,
    buttonTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(title)
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.testTag(buttonTag)
        ) {
            Text(buttonText)
        }
    }
}

@Composable
private fun SectionInput(
    index: Int,
    section: WodSectionInputState,
    onTitleChange: (String, String) -> Unit,
    onMemoChange: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section-item-$index"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider()
        OutlinedTextField(
            value = section.titleInput,
            onValueChange = { onTitleChange(section.localId, it) },
            label = { Text("Section title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-section-title-$index")
        )
        OutlinedTextField(
            value = section.memoInput,
            onValueChange = { onMemoChange(section.localId, it) },
            label = { Text("Section memo") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-section-memo-$index")
        )
        OutlinedButton(
            onClick = { onRemove(section.localId) },
            modifier = Modifier.testTag("action-remove-section-$index")
        ) {
            Text("Remove section")
        }
    }
}

@Composable
private fun MovementInput(
    index: Int,
    movement: MovementInputState,
    onNameChange: (String, String) -> Unit,
    onWeightChange: (String, String) -> Unit,
    onRepsChange: (String, String) -> Unit,
    onSetsChange: (String, String) -> Unit,
    onRoundsChange: (String, String) -> Unit,
    onDistanceChange: (String, String) -> Unit,
    onCaloriesChange: (String, String) -> Unit,
    onTimeChange: (String, String) -> Unit,
    onCategoryChange: (String, MovementCategory) -> Unit,
    onMemoChange: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("movement-item-$index"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider()
        OutlinedTextField(
            value = movement.nameInput,
            onValueChange = { onNameChange(movement.localId, it) },
            label = { Text("Movement name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-movement-name-$index")
        )
        EnumSelector(
            label = "Category",
            values = MovementCategory.entries,
            selectedValue = movement.category,
            onSelect = { onCategoryChange(movement.localId, it) },
            tagPrefix = "input-movement-category-$index"
        )
        MetricRow {
            MetricInput("Weight", movement.weightInput, "input-movement-weight-$index") {
                onWeightChange(movement.localId, it)
            }
            MetricInput("Reps", movement.repsInput, "input-movement-reps-$index") {
                onRepsChange(movement.localId, it)
            }
        }
        MetricRow {
            MetricInput("Sets", movement.setsInput, "input-movement-sets-$index") {
                onSetsChange(movement.localId, it)
            }
            MetricInput("Rounds", movement.roundsInput, "input-movement-rounds-$index") {
                onRoundsChange(movement.localId, it)
            }
        }
        MetricRow {
            MetricInput("Distance", movement.distanceInput, "input-movement-distance-$index") {
                onDistanceChange(movement.localId, it)
            }
            MetricInput("Calories", movement.caloriesInput, "input-movement-calories-$index") {
                onCaloriesChange(movement.localId, it)
            }
        }
        OutlinedTextField(
            value = movement.timeSecondsInput,
            onValueChange = { onTimeChange(movement.localId, it) },
            label = { Text("Time seconds") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-movement-time-$index")
        )
        OutlinedTextField(
            value = movement.memoInput,
            onValueChange = { onMemoChange(movement.localId, it) },
            label = { Text("Movement memo") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-movement-memo-$index")
        )
        OutlinedButton(
            onClick = { onRemove(movement.localId) },
            modifier = Modifier.testTag("action-remove-movement-$index")
        ) {
            Text("Remove movement")
        }
    }
}

@Composable
private fun MetricRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun RowScope.MetricInput(
    label: String,
    value: String,
    tag: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .weight(1f)
            .testTag(tag)
    )
}

@Composable
private fun <T : Enum<T>> EnumSelector(
    label: String,
    values: List<T>,
    selectedValue: T?,
    onSelect: (T) -> Unit,
    tagPrefix: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .testTag(tagPrefix),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                if (value == selectedValue) {
                    Button(
                        onClick = { onSelect(value) },
                        modifier = Modifier.testTag("$tagPrefix-${value.name}")
                    ) {
                        Text(value.name)
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSelect(value) },
                        modifier = Modifier.testTag("$tagPrefix-${value.name}")
                    ) {
                        Text(value.name)
                    }
                }
            }
        }
    }
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.WOD_DATE_REQUIRED -> "Date is required."
        ValidationError.WOD_DATE_INVALID -> "Date must use yyyy-MM-dd."
        ValidationError.WOD_TITLE_BLANK -> "Title is required."
        ValidationError.WOD_TYPE_REQUIRED -> "WOD type is required."
        ValidationError.MOVEMENT_NAME_BLANK -> "Movement name is required."
        ValidationError.MOVEMENT_WEIGHT_INVALID -> "Movement weight must be numeric."
        ValidationError.MOVEMENT_REPS_INVALID -> "Movement reps must be numeric."
        ValidationError.MOVEMENT_SETS_INVALID -> "Movement sets must be numeric."
        ValidationError.MOVEMENT_ROUNDS_INVALID -> "Movement rounds must be numeric."
        ValidationError.MOVEMENT_DISTANCE_INVALID -> "Movement distance must be numeric."
        ValidationError.MOVEMENT_CALORIES_INVALID -> "Movement calories must be numeric."
        ValidationError.MOVEMENT_DURATION_INVALID -> "Movement time must be numeric."
        else -> name
    }
}
