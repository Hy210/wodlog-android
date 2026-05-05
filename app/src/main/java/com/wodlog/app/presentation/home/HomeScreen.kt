package com.wodlog.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogEmptyState
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSectionHeader
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone

@Composable
fun HomeRoute(
    repository: WodlogRepository,
    onCreateWodClick: () -> Unit,
    onOpenCafeImport: (Long) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        state = state,
        onCreateWodClick = onCreateWodClick,
        onImportWodClick = {
            viewModel.onImportClick(onOpenCafeImport)
        },
        onCafeSourceSelected = { cafeSource ->
            viewModel.onCafeSourceSelected(cafeSource, onOpenCafeImport)
        },
        onDismissCafeSourcePicker = viewModel::dismissCafeSourcePicker
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState = HomeUiState(isLoadingCafeSources = false),
    onCreateWodClick: () -> Unit = {},
    onImportWodClick: () -> Unit = {},
    onCafeSourceSelected: (CafeSource) -> Unit = {},
    onDismissCafeSourcePicker: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-home")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "오늘 기록",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "운동 직후 바로 WOD를 남기고 최근 흐름을 확인하세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WodLogCard(
            title = "오늘의 WOD",
            subtitle = "아직 오늘 기록이 없습니다",
            outlined = false,
            actions = {
                WodLogStatusChip(
                    text = "미작성",
                    tone = WodLogStatusChipTone.Warning
                )
            }
        ) {
            WodLogEmptyState(
                title = "오늘의 WOD를 추가하세요",
                description = "운동 이름과 유형만 먼저 저장해도 됩니다.",
                action = {
                    WodLogPrimaryButton(
                        text = "WOD 추가",
                        onClick = onCreateWodClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action-create-wod")
                    )
                    if (state.cafeSources.isNotEmpty()) {
                        ImportWodButton(
                            onClick = onImportWodClick,
                            enabled = !state.isLoadingCafeSources,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("action-import-wod")
                        )
                        Text(
                            text = "설정한 네이버카페에서 오늘 WOD를 가져옵니다.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("home-import-wod-helper")
                        )
                    }
                }
            )
        }

        WodLogSectionHeader(
            title = "빠른 확인",
            description = "최근 기록과 비교 화면에서 이어서 정리할 수 있습니다."
        )

        WodLogCard(
            title = "기록 요약",
            subtitle = "최근 기록이 쌓이면 홈에서 흐름을 바로 확인할 수 있습니다."
        ) {
            WodLogMetricChip(
                label = "최근 기록",
                value = "0",
                unit = "개"
            )
            WodLogMetricChip(
                label = "오늘 상태",
                value = "대기"
            )
        }

        WodLogCard(
            title = "다음 단계",
            subtitle = "WOD를 저장하면 Calendar와 Compare에서 확인할 수 있습니다."
        ) {
            Text(
                text = "기록이 쌓이면 최근 3회 비교와 날짜별 기록 흐름이 더 선명하게 보입니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (state.isCafeSourcePickerVisible) {
        CafeSourcePickerDialog(
            cafeSources = state.cafeSources,
            onSelect = onCafeSourceSelected,
            onDismiss = onDismissCafeSourcePicker
        )
    }
}

@Composable
private fun ImportWodButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Text(text = "WOD 불러오기")
    }
}

@Composable
private fun CafeSourcePickerDialog(
    cafeSources: List<CafeSource>,
    onSelect: (CafeSource) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.testTag("dialog-cafe-source-picker"),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "가져올 카페 선택",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cafeSources.forEach { cafeSource ->
                    WodLogPrimaryButton(
                        text = cafeSource.boxName,
                        onClick = { onSelect(cafeSource) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action-select-cafe-source-${cafeSource.id}")
                    )
                    Text(
                        text = cafeSource.boardUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("action-dismiss-cafe-source-picker")
            ) {
                Text("취소")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}
