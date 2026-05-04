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
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogDangerButton
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WOD 작성",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "운동 직후 빠르게 제목, 유형, 원문을 남기세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        WodLogCard(
            title = "기본 정보",
            subtitle = "날짜, WOD 이름, 유형을 먼저 정합니다.",
            outlined = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.dateInput,
                onValueChange = onDateChange,
                label = "날짜",
                placeholder = "yyyy-MM-dd",
                supportingText = "예: 2026-05-04",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag("input-wod-date")
            )
            WodLogTextField(
                value = state.titleInput,
                onValueChange = onTitleChange,
                label = "WOD 이름",
                placeholder = "예: Fran",
                supportingText = "벤치마크 이름이나 오늘의 WOD 제목",
                singleLine = true,
                modifier = Modifier.testTag("input-wod-title")
            )
            EnumSelector(
                label = "WOD 유형",
                values = WodType.entries,
                selectedValue = state.wodType,
                onSelect = onWodTypeChange,
                tagPrefix = "input-wod-type",
                displayText = { it.displayName() }
            )
        }

        WodLogCard(
            title = "운동 내용",
            subtitle = "원문과 메모를 나눠 입력합니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            WodLogTextField(
                value = state.rawTextInput,
                onValueChange = onRawTextChange,
                label = "운동 설명",
                placeholder = "예: 21-15-9 Thruster, Pull-up",
                supportingText = "WOD 원문을 그대로 붙여넣어도 됩니다.",
                singleLine = false,
                minLines = 4,
                modifier = Modifier.testTag("input-wod-raw-text")
            )
            WodLogTextField(
                value = state.memoInput,
                onValueChange = onMemoChange,
                label = "기록 메모",
                placeholder = "컨디션, 전략, 특이사항",
                singleLine = false,
                minLines = 2,
                modifier = Modifier.testTag("input-wod-memo")
            )
        }

        WodLogSectionHeader(
            title = "섹션",
            description = "Warm-up, Strength, Metcon처럼 묶어둘 수 있습니다.",
            action = {
                WodLogSecondaryButton(
                    text = "섹션 추가",
                    onClick = onAddSection,
                    modifier = Modifier.testTag("action-add-section")
                )
            }
        )
        if (state.sections.isEmpty()) {
            WodLogCard(modifier = Modifier.fillMaxWidth()) {
                WodLogEmptyState(
                    title = "섹션이 없습니다",
                    description = "필요할 때만 섹션을 추가하세요."
                )
            }
        }
        state.sections.forEachIndexed { index, section ->
            SectionInput(
                index = index,
                section = section,
                onTitleChange = onSectionTitleChange,
                onMemoChange = onSectionMemoChange,
                onRemove = onRemoveSection
            )
        }

        WodLogSectionHeader(
            title = "Movement",
            description = "동작과 수치를 따로 남기면 나중에 비교하기 쉽습니다.",
            action = {
                WodLogSecondaryButton(
                    text = "Movement 추가",
                    onClick = onAddMovement,
                    modifier = Modifier.testTag("action-add-movement")
                )
            }
        )
        if (state.movements.isEmpty()) {
            WodLogCard(modifier = Modifier.fillMaxWidth()) {
                WodLogEmptyState(
                    title = "Movement가 없습니다",
                    description = "원문만 저장해도 되고, 필요한 동작만 추가해도 됩니다."
                )
            }
        }
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
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.validationErrors.joinToString(separator = "\n") { it.toDisplayText() },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("text-wod-validation-errors")
                )
            }
        }

        state.message?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                WodLogStatusChip(
                    text = state.savedWodId?.let { "$message (#$it)" } ?: message,
                    tone = WodLogStatusChipTone.Success,
                    modifier = Modifier.testTag("text-wod-message")
                )
            }
        }

        WodLogPrimaryButton(
            text = if (state.isSaving) "저장 중..." else "WOD 저장",
            onClick = onSaveClick,
            enabled = !state.isSaving,
            loading = state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-save-wod")
        )
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
    WodLogCard(
        title = "섹션 ${index + 1}",
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section-item-$index")
    ) {
        WodLogTextField(
            value = section.titleInput,
            onValueChange = { onTitleChange(section.localId, it) },
            label = "섹션 이름",
            placeholder = "예: Strength",
            singleLine = true,
            modifier = Modifier.testTag("input-section-title-$index")
        )
        WodLogTextField(
            value = section.memoInput,
            onValueChange = { onMemoChange(section.localId, it) },
            label = "섹션 메모",
            placeholder = "구성이나 의도를 짧게 남기세요",
            singleLine = false,
            minLines = 2,
            modifier = Modifier.testTag("input-section-memo-$index")
        )
        WodLogDangerButton(
            text = "섹션 삭제",
            onClick = { onRemove(section.localId) },
            modifier = Modifier.testTag("action-remove-section-$index")
        )
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
    WodLogCard(
        title = "Movement ${index + 1}",
        modifier = Modifier
            .fillMaxWidth()
            .testTag("movement-item-$index")
    ) {
        WodLogTextField(
            value = movement.nameInput,
            onValueChange = { onNameChange(movement.localId, it) },
            label = "Movement 이름",
            placeholder = "예: Thruster",
            singleLine = true,
            modifier = Modifier.testTag("input-movement-name-$index")
        )
        EnumSelector(
            label = "카테고리",
            values = MovementCategory.entries,
            selectedValue = movement.category,
            onSelect = { onCategoryChange(movement.localId, it) },
            tagPrefix = "input-movement-category-$index",
            displayText = { it.displayName() }
        )
        MetricRow {
            MetricInput("무게", "kg", movement.weightInput, "input-movement-weight-$index") {
                onWeightChange(movement.localId, it)
            }
            MetricInput("Reps", null, movement.repsInput, "input-movement-reps-$index") {
                onRepsChange(movement.localId, it)
            }
        }
        MetricRow {
            MetricInput("Sets", null, movement.setsInput, "input-movement-sets-$index") {
                onSetsChange(movement.localId, it)
            }
            MetricInput("Rounds", null, movement.roundsInput, "input-movement-rounds-$index") {
                onRoundsChange(movement.localId, it)
            }
        }
        MetricRow {
            MetricInput("거리", "m", movement.distanceInput, "input-movement-distance-$index") {
                onDistanceChange(movement.localId, it)
            }
            MetricInput("Calories", "kcal", movement.caloriesInput, "input-movement-calories-$index") {
                onCaloriesChange(movement.localId, it)
            }
        }
        WodLogTextField(
            value = movement.timeSecondsInput,
            onValueChange = { onTimeChange(movement.localId, it) },
            label = "시간",
            placeholder = "초 단위",
            supportingText = "예: 420",
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.testTag("input-movement-time-$index")
        )
        WodLogTextField(
            value = movement.memoInput,
            onValueChange = { onMemoChange(movement.localId, it) },
            label = "Movement 메모",
            placeholder = "스케일, 자세, 세트별 느낌",
            singleLine = false,
            minLines = 2,
            modifier = Modifier.testTag("input-movement-memo-$index")
        )
        WodLogDangerButton(
            text = "Movement 삭제",
            onClick = { onRemove(movement.localId) },
            modifier = Modifier.testTag("action-remove-movement-$index")
        )
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
    unit: String?,
    value: String,
    tag: String,
    onValueChange: (String) -> Unit
) {
    WodLogTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = unit ?: "숫자",
        supportingText = unit,
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
    tagPrefix: String,
    displayText: (T) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .testTag(tagPrefix),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                WodLogStatusChip(
                    text = displayText(value),
                    selected = value == selectedValue,
                    tone = if (value == selectedValue) {
                        WodLogStatusChipTone.Primary
                    } else {
                        WodLogStatusChipTone.Neutral
                    },
                    onClick = { onSelect(value) },
                    modifier = Modifier.testTag("$tagPrefix-${value.name}")
                )
            }
        }
    }
}

private fun WodType.displayName(): String = when (this) {
    WodType.FOR_TIME -> "For Time"
    WodType.AMRAP -> "AMRAP"
    WodType.EMOM -> "EMOM"
    WodType.RFT -> "RFT"
    WodType.STRENGTH -> "Strength"
    WodType.SKILL -> "Skill"
    WodType.INTERVAL -> "Interval"
    WodType.OTHER -> "Other"
}

private fun MovementCategory.displayName(): String = when (this) {
    MovementCategory.STRENGTH -> "Strength"
    MovementCategory.CARDIO -> "Cardio"
    MovementCategory.GYMNASTICS -> "Gymnastics"
    MovementCategory.WEIGHTLIFTING -> "Weightlifting"
    MovementCategory.BODYWEIGHT -> "Bodyweight"
    MovementCategory.OTHER -> "Other"
}

private fun ValidationError.toDisplayText(): String {
    return when (this) {
        ValidationError.WOD_DATE_REQUIRED -> "날짜를 입력하세요."
        ValidationError.WOD_DATE_INVALID -> "날짜는 yyyy-MM-dd 형식으로 입력하세요."
        ValidationError.WOD_TITLE_BLANK -> "WOD 이름을 입력하세요."
        ValidationError.WOD_TYPE_REQUIRED -> "WOD 유형을 선택하세요."
        ValidationError.MOVEMENT_NAME_BLANK -> "Movement 이름을 입력하세요."
        ValidationError.MOVEMENT_WEIGHT_INVALID -> "Movement 무게는 숫자로 입력하세요."
        ValidationError.MOVEMENT_REPS_INVALID -> "Movement reps는 숫자로 입력하세요."
        ValidationError.MOVEMENT_SETS_INVALID -> "Movement sets는 숫자로 입력하세요."
        ValidationError.MOVEMENT_ROUNDS_INVALID -> "Movement rounds는 숫자로 입력하세요."
        ValidationError.MOVEMENT_DISTANCE_INVALID -> "Movement 거리는 숫자로 입력하세요."
        ValidationError.MOVEMENT_CALORIES_INVALID -> "Movement calories는 숫자로 입력하세요."
        ValidationError.MOVEMENT_DURATION_INVALID -> "Movement 시간은 숫자로 입력하세요."
        ValidationError.MOVEMENT_WEIGHT_NEGATIVE -> "Movement 무게는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_REPS_NEGATIVE -> "Movement reps는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_SETS_NEGATIVE -> "Movement sets는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_ROUNDS_NEGATIVE -> "Movement rounds는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_DISTANCE_NEGATIVE -> "Movement 거리는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_CALORIES_NEGATIVE -> "Movement calories는 0 이상이어야 합니다."
        ValidationError.MOVEMENT_DURATION_NEGATIVE -> "Movement 시간은 0 이상이어야 합니다."
        else -> name
    }
}
