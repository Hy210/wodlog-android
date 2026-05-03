package com.wodlog.app.presentation.lifestyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.wodlog.app.util.ValidationError

@Composable
fun LifestyleRoute(
    viewModel: LifestyleViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadCurrentWeek()
    }

    LifestyleScreen(
        state = state,
        onWeekStartDateChange = viewModel::onWeekStartDateChange,
        onDietSummaryChange = viewModel::onDietSummaryChange,
        onHasAlcoholChange = viewModel::onHasAlcoholChange,
        onAlcoholAmountChange = viewModel::onAlcoholAmountChange,
        onHasSmokingChange = viewModel::onHasSmokingChange,
        onSmokingAmountChange = viewModel::onSmokingAmountChange,
        onAverageSleepHoursChange = viewModel::onAverageSleepHoursChange,
        onMemoChange = viewModel::onMemoChange,
        onSaveClick = viewModel::saveLifestyleLog
    )
}

@Composable
fun LifestyleScreen(
    state: LifestyleUiState,
    onWeekStartDateChange: (String) -> Unit = {},
    onDietSummaryChange: (String) -> Unit = {},
    onHasAlcoholChange: (Boolean) -> Unit = {},
    onAlcoholAmountChange: (String) -> Unit = {},
    onHasSmokingChange: (Boolean) -> Unit = {},
    onSmokingAmountChange: (String) -> Unit = {},
    onAverageSleepHoursChange: (String) -> Unit = {},
    onMemoChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-lifestyle")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lifestyle",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (state.hasExistingLog) "Saved weekly log" else "No weekly log yet",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-lifestyle-status")
        )

        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("progress-lifestyle-loading"))
            }
        }

        OutlinedTextField(
            value = state.weekStartDateInput,
            onValueChange = onWeekStartDateChange,
            label = { Text("Week start yyyy-MM-dd") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-week-start")
        )

        OutlinedTextField(
            value = state.dietSummaryInput,
            onValueChange = onDietSummaryChange,
            label = { Text("Diet summary") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-diet-summary")
        )

        ToggleRow(
            label = "Alcohol",
            checked = state.hasAlcohol,
            onCheckedChange = onHasAlcoholChange,
            tag = "toggle-lifestyle-alcohol"
        )

        OutlinedTextField(
            value = state.alcoholAmountPerWeekInput,
            onValueChange = onAlcoholAmountChange,
            label = { Text("Alcohol amount per week") },
            enabled = state.hasAlcohol,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-alcohol-amount")
        )

        ToggleRow(
            label = "Smoking",
            checked = state.hasSmoking,
            onCheckedChange = onHasSmokingChange,
            tag = "toggle-lifestyle-smoking"
        )

        OutlinedTextField(
            value = state.smokingAmountPerWeekInput,
            onValueChange = onSmokingAmountChange,
            label = { Text("Smoking amount per week") },
            enabled = state.hasSmoking,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-smoking-amount")
        )

        OutlinedTextField(
            value = state.averageSleepHoursInput,
            onValueChange = onAverageSleepHoursChange,
            label = { Text("Average sleep hours") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-sleep-hours")
        )

        OutlinedTextField(
            value = state.memoInput,
            onValueChange = onMemoChange,
            label = { Text("Memo") },
            minLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-lifestyle-memo")
        )

        if (state.validationErrors.isNotEmpty()) {
            Text(
                text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-lifestyle-errors")
            )
        }

        state.message?.let { message ->
            Text(
                text = state.savedLifestyleLogId?.let { "$message (#$it)" } ?: message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-lifestyle-message")
            )
        }

        Button(
            onClick = onSaveClick,
            enabled = !state.isSaving && !state.isLoading,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("action-save-lifestyle")
        ) {
            Text(if (state.isSaving) "Saving" else "Save lifestyle")
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.WOD_DATE_INVALID -> "Week start date must use yyyy-MM-dd."
        ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY -> "Week start date must be Monday."
        ValidationError.LIFESTYLE_SLEEP_HOURS_INVALID -> "Sleep hours must be numeric."
        ValidationError.LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE -> "Sleep hours must be between 0 and 24."
        ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_INVALID -> "Alcohol amount must be numeric."
        ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_NEGATIVE -> "Alcohol amount must be 0 or more."
        ValidationError.LIFESTYLE_SMOKING_AMOUNT_INVALID -> "Smoking amount must be numeric."
        ValidationError.LIFESTYLE_SMOKING_AMOUNT_NEGATIVE -> "Smoking amount must be 0 or more."
        else -> name
    }
}
