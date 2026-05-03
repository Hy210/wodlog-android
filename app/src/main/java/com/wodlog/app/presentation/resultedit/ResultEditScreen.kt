package com.wodlog.app.presentation.resultedit

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
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.util.ValidationError

@Composable
fun ResultEditRoute(
    viewModel: ResultEditViewModel,
    wodId: Long,
    onSaved: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(wodId) {
        viewModel.loadResult(wodId)
    }
    LaunchedEffect(state.savedResultId) {
        state.savedResultId?.let {
            onSaved()
            viewModel.onSavedNavigationHandled()
        }
    }

    ResultEditScreen(
        state = state,
        onScoreTypeChange = viewModel::onScoreTypeChange,
        onTimeSecondsChange = viewModel::onTimeSecondsChange,
        onRoundsChange = viewModel::onRoundsChange,
        onRepsChange = viewModel::onRepsChange,
        onTotalRepsChange = viewModel::onTotalRepsChange,
        onLoadChange = viewModel::onLoadChange,
        onDistanceChange = viewModel::onDistanceChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onRxStatusChange = viewModel::onRxStatusChange,
        onRpeChange = viewModel::onRpeChange,
        onConditionChange = viewModel::onConditionChange,
        onMemoChange = viewModel::onMemoChange,
        onSaveClick = viewModel::saveResult
    )
}

@Composable
fun ResultEditScreen(
    state: ResultEditUiState,
    onScoreTypeChange: (ScoreType) -> Unit,
    onTimeSecondsChange: (String) -> Unit,
    onRoundsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onTotalRepsChange: (String) -> Unit,
    onLoadChange: (String) -> Unit,
    onDistanceChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onRxStatusChange: (RxStatus) -> Unit,
    onRpeChange: (String) -> Unit,
    onConditionChange: (Condition) -> Unit,
    onMemoChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-result-edit")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Result Edit",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "WOD #${state.wodId}",
            style = MaterialTheme.typography.bodyMedium
        )

        EnumSelector(
            label = "Score Type",
            values = ScoreType.entries,
            selectedValue = state.scoreType,
            onSelect = onScoreTypeChange,
            tagPrefix = "input-result-score-type"
        )

        MetricRow {
            MetricInput("Time seconds", state.timeSecondsInput, "input-result-time", onTimeSecondsChange)
            MetricInput("Rounds", state.roundsInput, "input-result-rounds", onRoundsChange)
        }
        MetricRow {
            MetricInput("Reps", state.repsInput, "input-result-reps", onRepsChange)
            MetricInput("Total reps", state.totalRepsInput, "input-result-total-reps", onTotalRepsChange)
        }
        MetricRow {
            MetricInput("Load kg", state.loadInput, "input-result-load", onLoadChange)
            MetricInput("Distance m", state.distanceInput, "input-result-distance", onDistanceChange)
        }
        MetricRow {
            MetricInput("Calories", state.caloriesInput, "input-result-calories", onCaloriesChange)
            MetricInput("RPE", state.rpeInput, "input-result-rpe", onRpeChange)
        }

        EnumSelector(
            label = "Rx Status",
            values = RxStatus.entries,
            selectedValue = state.rxStatus,
            onSelect = onRxStatusChange,
            tagPrefix = "input-result-rx-status"
        )
        EnumSelector(
            label = "Condition",
            values = Condition.entries,
            selectedValue = state.condition,
            onSelect = onConditionChange,
            tagPrefix = "input-result-condition"
        )

        OutlinedTextField(
            value = state.memoInput,
            onValueChange = onMemoChange,
            label = { Text("Memo") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-result-memo")
        )

        if (state.validationErrors.isNotEmpty()) {
            Text(
                text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-result-validation-errors")
            )
        }

        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-result-message")
            )
        }

        Button(
            onClick = onSaveClick,
            enabled = !state.isSaving,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("action-save-result")
        ) {
            Text(if (state.isSaving) "Saving" else "Save Result")
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
        ValidationError.SCORE_TYPE_REQUIRED -> "Score type is required."
        ValidationError.RESULT_TIME_INVALID -> "Time must be numeric."
        ValidationError.RESULT_ROUNDS_INVALID -> "Rounds must be numeric."
        ValidationError.RESULT_REPS_INVALID -> "Reps must be numeric."
        ValidationError.RESULT_TOTAL_REPS_INVALID -> "Total reps must be numeric."
        ValidationError.RESULT_LOAD_INVALID -> "Load must be numeric."
        ValidationError.RESULT_DISTANCE_INVALID -> "Distance must be numeric."
        ValidationError.RESULT_CALORIES_INVALID -> "Calories must be numeric."
        ValidationError.RESULT_RPE_INVALID -> "RPE must be numeric."
        ValidationError.RESULT_TIME_NEGATIVE -> "Time must be 0 or greater."
        ValidationError.RESULT_ROUNDS_NEGATIVE -> "Rounds must be 0 or greater."
        ValidationError.RESULT_REPS_NEGATIVE -> "Reps must be 0 or greater."
        ValidationError.RESULT_TOTAL_REPS_NEGATIVE -> "Total reps must be 0 or greater."
        ValidationError.RESULT_LOAD_NEGATIVE -> "Load must be 0 or greater."
        ValidationError.RESULT_DISTANCE_NEGATIVE -> "Distance must be 0 or greater."
        ValidationError.RESULT_CALORIES_NEGATIVE -> "Calories must be 0 or greater."
        ValidationError.RESULT_RPE_OUT_OF_RANGE -> "RPE must be between 1 and 10."
        else -> name
    }
}
