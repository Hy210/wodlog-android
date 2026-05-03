package com.wodlog.app.presentation.profile

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
fun ProfileRoute(
    viewModel: ProfileViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadProfile()
    }

    ProfileScreen(
        state = state,
        onHeightChange = viewModel::onHeightChange,
        onWeightChange = viewModel::onWeightChange,
        onStartDateChange = viewModel::onCrossfitStartDateChange,
        onSaveClick = viewModel::saveProfile
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-profile")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "프로필",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (state.hasProfile) "저장된 프로필 있음" else "저장된 프로필 없음",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-profile-status")
        )

        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("progress-profile-loading"))
            }
        }

        OutlinedTextField(
            value = state.heightCmInput,
            onValueChange = onHeightChange,
            label = { Text("키 cm") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-profile-height")
        )

        OutlinedTextField(
            value = state.weightKgInput,
            onValueChange = onWeightChange,
            label = { Text("몸무게 kg") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-profile-weight")
        )

        OutlinedTextField(
            value = state.crossfitStartDateInput,
            onValueChange = onStartDateChange,
            label = { Text("크로스핏 시작일 yyyy-MM-dd") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input-profile-start-date")
        )

        Text(
            text = "운동 기간 ${state.trainingDays}일",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("text-training-days")
        )

        if (state.validationErrors.isNotEmpty()) {
            Text(
                text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-validation-errors")
            )
        }

        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-profile-message")
            )
        }

        Button(
            onClick = onSaveClick,
            enabled = !state.isSaving && !state.isLoading,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("action-save-profile")
        ) {
            Text(if (state.isSaving) "저장 중" else "저장")
        }
    }
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.HEIGHT_OUT_OF_RANGE -> "키는 50cm부터 250cm까지 입력할 수 있습니다."
        ValidationError.WEIGHT_OUT_OF_RANGE -> "몸무게는 20kg부터 300kg까지 입력할 수 있습니다."
        ValidationError.CROSSFIT_START_DATE_IN_FUTURE -> "크로스핏 시작일은 미래 날짜일 수 없습니다."
        else -> name
    }
}
