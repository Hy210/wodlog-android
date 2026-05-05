package com.wodlog.app.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogConfirmDialog
import com.wodlog.app.presentation.components.WodLogDangerButton
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone
import com.wodlog.app.presentation.components.WodLogTextField

@Composable
fun CafeSourceSettingsRoute(
    repository: WodlogRepository,
    onBackClick: () -> Unit = {}
) {
    val viewModel: CafeSourceSettingsViewModel = viewModel(
        factory = CafeSourceSettingsViewModelFactory(repository)
    )
    val state by viewModel.uiState.collectAsState()

    CafeSourceSettingsScreen(
        state = state,
        onBackClick = onBackClick,
        onBoxNameChange = viewModel::onBoxNameChange,
        onBoardUrlChange = viewModel::onBoardUrlChange,
        onTitleKeywordsTextChange = viewModel::onTitleKeywordsTextChange,
        onPreferMobileUrlChange = viewModel::onPreferMobileUrlChange,
        onSaveClick = viewModel::saveCafeSource,
        onEditClick = viewModel::startEdit,
        onCancelEditClick = viewModel::cancelEdit,
        onDeleteClick = viewModel::requestDelete,
        onDeleteConfirm = viewModel::confirmDelete,
        onDeleteDismiss = viewModel::cancelDelete
    )
}

@Composable
fun CafeSourceSettingsScreen(
    state: CafeSourceSettingsUiState,
    onBackClick: () -> Unit,
    onBoxNameChange: (String) -> Unit,
    onBoardUrlChange: (String) -> Unit,
    onTitleKeywordsTextChange: (String) -> Unit,
    onPreferMobileUrlChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onEditClick: (CafeSource) -> Unit,
    onCancelEditClick: () -> Unit,
    onDeleteClick: (CafeSource) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-cafe-source-settings")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "카페 소스 설정",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Box별 WOD 게시판 URL을 등록해 가져오기 준비를 합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        WodLogSecondaryButton(
            text = "설정으로 돌아가기",
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-back-settings"),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        )

        state.message?.let { message ->
            WodLogStatusChip(
                text = message,
                tone = WodLogStatusChipTone.Success,
                modifier = Modifier.testTag("text-cafe-source-message")
            )
        }
        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-cafe-source-error")
            )
        }

        CafeSourceForm(
            state = state,
            onBoxNameChange = onBoxNameChange,
            onBoardUrlChange = onBoardUrlChange,
            onTitleKeywordsTextChange = onTitleKeywordsTextChange,
            onPreferMobileUrlChange = onPreferMobileUrlChange,
            onSaveClick = onSaveClick,
            onCancelEditClick = onCancelEditClick
        )

        Text(
            text = "등록된 카페 소스",
            style = MaterialTheme.typography.titleMedium
        )

        when {
            state.isLoading -> {
                Text(
                    text = "카페 소스를 불러오는 중입니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("cafe-source-loading")
                )
            }
            state.cafeSources.isEmpty() -> {
                WodLogEmptyState(
                    title = "등록된 카페 소스가 없습니다.",
                    description = "Box 이름과 게시판 URL을 입력한 뒤 추가해 주세요.",
                    modifier = Modifier.testTag("cafe-source-empty")
                )
            }
            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("cafe-source-list")
                ) {
                    state.cafeSources.forEach { cafeSource ->
                        CafeSourceItem(
                            cafeSource = cafeSource,
                            isEditing = state.editingCafeSourceId == cafeSource.id,
                            isBusy = state.isSaving,
                            onEditClick = { onEditClick(cafeSource) },
                            onDeleteClick = { onDeleteClick(cafeSource) }
                        )
                    }
                }
            }
        }
    }

    state.deleteTarget?.let { target ->
        WodLogConfirmDialog(
            title = "카페 소스 삭제",
            message = "${target.boxName} 카페 소스를 삭제할까요?",
            onConfirm = onDeleteConfirm,
            onDismiss = onDeleteDismiss,
            confirmText = "삭제",
            dismissText = "취소",
            modifier = Modifier.testTag("dialog-delete-cafe-source")
        )
    }
}

@Composable
private fun CafeSourceForm(
    state: CafeSourceSettingsUiState,
    onBoxNameChange: (String) -> Unit,
    onBoardUrlChange: (String) -> Unit,
    onTitleKeywordsTextChange: (String) -> Unit,
    onPreferMobileUrlChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onCancelEditClick: () -> Unit
) {
    WodLogCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cafe-source-form"),
        title = if (state.isEditing) "카페 소스 수정" else "카페 소스 추가",
        subtitle = "네이버 계정 정보는 저장하지 않습니다."
    ) {
        WodLogTextField(
            value = state.boxName,
            onValueChange = onBoxNameChange,
            label = "Box 이름",
            errorText = state.boxNameError,
            enabled = !state.isSaving,
            modifier = Modifier.testTag("input-cafe-source-box-name")
        )
        WodLogTextField(
            value = state.boardUrl,
            onValueChange = onBoardUrlChange,
            label = "게시판 URL",
            placeholder = "https://cafe.naver.com/...",
            errorText = state.boardUrlError,
            enabled = !state.isSaving,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.testTag("input-cafe-source-board-url")
        )
        WodLogTextField(
            value = state.titleKeywordsText,
            onValueChange = onTitleKeywordsTextChange,
            label = "제목 키워드",
            supportingText = "쉼표로 구분해 입력하세요.",
            enabled = !state.isSaving,
            modifier = Modifier.testTag("input-cafe-source-keywords")
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !state.isSaving) {
                    onPreferMobileUrlChange(!state.preferMobileUrl)
                }
                .testTag("toggle-cafe-source-prefer-mobile"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.preferMobileUrl,
                onCheckedChange = onPreferMobileUrlChange,
                enabled = !state.isSaving
            )
            Text(
                text = "모바일 URL 우선 사용",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        WodLogPrimaryButton(
            text = if (state.isEditing) "저장" else "추가",
            onClick = onSaveClick,
            loading = state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-save-cafe-source"),
            leadingIcon = {
                Icon(
                    imageVector = if (state.isEditing) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null
                )
            }
        )
        if (state.isEditing) {
            WodLogSecondaryButton(
                text = "취소",
                onClick = onCancelEditClick,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-cancel-cafe-source-edit"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun CafeSourceItem(
    cafeSource: CafeSource,
    isEditing: Boolean,
    isBusy: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WodLogCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cafe-source-item-${cafeSource.id}"),
        title = cafeSource.boxName,
        subtitle = cafeSource.boardUrl,
        outlined = !isEditing
    ) {
        Text(
            text = "제목 키워드: ${cafeSource.titleKeywords.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (cafeSource.preferMobileUrl) {
                "모바일 URL 우선 사용"
            } else {
                "입력한 URL 그대로 사용"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WodLogSecondaryButton(
                text = "수정",
                onClick = onEditClick,
                enabled = !isBusy,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-edit-cafe-source-${cafeSource.id}"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
            )
            WodLogDangerButton(
                text = "삭제",
                onClick = onDeleteClick,
                enabled = !isBusy,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action-delete-cafe-source-${cafeSource.id}"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            )
        }
    }
}
