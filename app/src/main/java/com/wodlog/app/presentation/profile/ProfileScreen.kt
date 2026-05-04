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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogDateField
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "프로필",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WodLogStatusChip(
                text = if (state.hasProfile) "저장된 프로필" else "프로필 없음",
                tone = if (state.hasProfile) WodLogStatusChipTone.Success else WodLogStatusChipTone.Neutral,
                modifier = Modifier.testTag("text-profile-status")
            )
            WodLogMetricChip(
                label = "운동 기간",
                value = "${state.trainingDays}",
                unit = "일",
                modifier = Modifier.testTag("text-training-days")
            )
        }

        WodLogPrimaryButton(
            text = if (state.isSaving) "저장 중..." else "프로필 저장",
            onClick = onSaveClick,
            enabled = !state.isSaving && !state.isLoading,
            loading = state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-save-profile")
        )

        if (state.isLoading) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.testTag("progress-profile-loading"))
                Text(
                    text = "프로필을 불러오는 중입니다",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (state.validationErrors.isNotEmpty()) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-validation-errors")
                )
            }
        }

        state.message?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                WodLogStatusChip(
                    text = message,
                    tone = WodLogStatusChipTone.Success,
                    modifier = Modifier.testTag("text-profile-message")
                )
            }
        }

        WodLogSectionHeader(
            title = "기본 정보",
            description = "몸 상태와 운동 경력을 질문지에 반영합니다."
        )
        WodLogCard(
            title = "신체 정보",
            subtitle = "선택 입력입니다. 모르면 비워둬도 됩니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.heightCmInput,
                onValueChange = onHeightChange,
                label = "키",
                placeholder = "cm",
                supportingText = "예: 175",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.testTag("input-profile-height")
            )

            WodLogTextField(
                value = state.weightKgInput,
                onValueChange = onWeightChange,
                label = "몸무게",
                placeholder = "kg",
                supportingText = "예: 72.5",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.testTag("input-profile-weight")
            )
        }

        WodLogCard(
            title = "CrossFit 정보",
            subtitle = "시작일을 기준으로 운동 기간을 계산합니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogDateField(
                value = state.crossfitStartDateInput,
                onValueChange = onStartDateChange,
                label = "CrossFit 시작일",
                placeholder = "yyyy-MM-dd",
                supportingText = "직접 입력하거나 달력에서 선택하세요.",
                modifier = Modifier.testTag("input-profile-start-date")
            )
        }
    }
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.HEIGHT_OUT_OF_RANGE -> "키는 50cm부터 250cm까지 입력할 수 있습니다."
        ValidationError.WEIGHT_OUT_OF_RANGE -> "몸무게는 20kg부터 300kg까지 입력할 수 있습니다."
        ValidationError.CROSSFIT_START_DATE_IN_FUTURE -> "CrossFit 시작일은 미래 날짜일 수 없습니다."
        else -> name
    }
}
