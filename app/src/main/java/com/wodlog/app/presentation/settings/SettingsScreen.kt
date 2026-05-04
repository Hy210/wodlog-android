package com.wodlog.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogDangerButton
import com.wodlog.app.presentation.components.WodLogMetricChip
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone

@Composable
fun SettingsScreen(
    onProfileClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {},
    onLicenseClick: () -> Unit = {},
    onExportJsonClick: () -> Unit = {},
    onImportJsonClick: () -> Unit = {},
    onApplyImportClick: () -> Unit = {},
    exportState: SettingsExportState = SettingsExportState(),
    importState: SettingsImportState = SettingsImportState(),
    appVersion: String = "0.1.0-dev",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-settings")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "프로필, 생활습관, 백업과 앱 정보를 관리합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SettingsSection(
            title = "프로필과 생활습관",
            subtitle = "질문지와 분석에 사용할 기본 정보를 관리합니다.",
            modifier = Modifier.testTag("settings-section-profile")
        ) {
            WodLogPrimaryButton(
                text = "프로필",
                onClick = onProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-profile")
            )
            WodLogSecondaryButton(
                text = "생활습관",
                onClick = onLifestyleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-lifestyle")
            )
        }

        SettingsSection(
            title = "앱 정보",
            subtitle = "현재 설치된 와드로그 정보를 확인합니다.",
            modifier = Modifier.testTag("settings-section-app-info")
        ) {
            WodLogMetricChip(label = "앱 이름", value = "와드로그")
            WodLogMetricChip(
                label = "버전",
                value = appVersion,
                modifier = Modifier.testTag("settings-app-version")
            )
            Text(
                text = "GitHub 링크: 준비 중",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("settings-github-placeholder")
            )
        }

        SettingsSection(
            title = "데이터 백업",
            subtitle = "JSON 파일로 내보내거나 백업 파일을 미리 확인합니다.",
            modifier = Modifier.testTag("settings-section-data")
        ) {
            WodLogPrimaryButton(
                text = if (exportState.isExporting) "백업 준비 중..." else "백업하기",
                onClick = onExportJsonClick,
                enabled = !exportState.isExporting &&
                    !importState.isImporting &&
                    !importState.isApplying,
                loading = exportState.isExporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-export-json")
            )
            ExportStatus(exportState = exportState)

            WodLogSecondaryButton(
                text = if (importState.isImporting) "백업 검증 중..." else "복구 파일 미리보기",
                onClick = onImportJsonClick,
                enabled = !exportState.isExporting &&
                    !importState.isImporting &&
                    !importState.isApplying,
                loading = importState.isImporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-import-json")
            )
            ImportStatus(importState = importState)
            WodLogDangerButton(
                text = if (importState.isApplying) "복구 적용 중..." else "복구 적용",
                onClick = onApplyImportClick,
                enabled = importState.preview?.isValid == true &&
                    !importState.isImporting &&
                    !importState.isApplying,
                loading = importState.isApplying,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-apply-import")
            )
            Text(
                text = "기존 데이터는 삭제하지 않고 백업 데이터와 병합합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("settings-import-merge-note")
            )
        }

        SettingsSection(
            title = "라이선스",
            subtitle = "오픈소스 라이선스 정보를 확인합니다.",
            modifier = Modifier.testTag("settings-section-license")
        ) {
            WodLogSecondaryButton(
                text = "MIT License 보기",
                onClick = onLicenseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-license")
            )
        }

        SettingsSection(
            title = "위험 구역",
            subtitle = "되돌리기 어려운 작업은 별도 확인이 필요합니다.",
            modifier = Modifier.testTag("settings-section-danger")
        ) {
            WodLogDangerButton(
                text = "데이터 초기화 준비 중",
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-reset-data-placeholder")
            )
        }
    }
}

@Composable
private fun ExportStatus(exportState: SettingsExportState) {
    if (exportState.isExporting) {
        Text(
            text = "백업 JSON을 준비하고 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("settings-export-progress")
        )
    }
    exportState.message?.let { message ->
        WodLogStatusChip(
            text = message,
            tone = WodLogStatusChipTone.Success,
            modifier = Modifier.testTag("text-settings-export-message")
        )
    }
    exportState.errorMessage?.let { errorMessage ->
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-settings-export-error")
        )
    }
}

@Composable
private fun ImportStatus(importState: SettingsImportState) {
    if (importState.isImporting) {
        Text(
            text = "백업 JSON을 읽고 검증하고 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("settings-import-progress")
        )
    }
    importState.message?.let { message ->
        WodLogStatusChip(
            text = message,
            tone = WodLogStatusChipTone.Success,
            modifier = Modifier.testTag("text-settings-import-message")
        )
    }
    importState.errorMessage?.let { errorMessage ->
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("text-settings-import-error")
        )
    }
    if (importState.isApplying) {
        Text(
            text = "검증된 백업을 로컬 DB에 병합하고 있습니다.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("settings-import-apply-progress")
        )
    }
    importState.applyResult?.let { result ->
        WodLogCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings-import-apply-result"),
            title = "복구 적용 결과",
            outlined = false
        ) {
            WodLogStatusChip(
                text = if (result.isSuccess) "성공" else "실패",
                tone = if (result.isSuccess) WodLogStatusChipTone.Success else WodLogStatusChipTone.Error,
                modifier = Modifier.testTag("settings-import-apply-status")
            )
            ImportCount("Imported WOD", result.importedWodCount, "settings-import-applied-wod-count")
            ImportCount("Imported Movement", result.importedMovementCount, "settings-import-applied-movement-count")
            ImportCount("Imported Result", result.importedResultCount, "settings-import-applied-result-count")
            ImportCount("Imported Lifestyle", result.importedLifestyleLogCount, "settings-import-applied-lifestyle-count")
            ImportCount("Imported AI Report", result.importedAiReportCount, "settings-import-applied-report-count")
        }
    }
    importState.preview?.let { preview ->
        WodLogCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings-import-preview"),
            title = "복구 미리보기"
        ) {
            WodLogStatusChip(
                text = if (preview.isValid) "가져오기 가능" else "가져오기 불가",
                tone = if (preview.isValid) WodLogStatusChipTone.Success else WodLogStatusChipTone.Error,
                modifier = Modifier.testTag("settings-import-validity")
            )
            ImportCount("WOD", preview.wodCount, "settings-import-wod-count")
            ImportCount("Movement", preview.movementCount, "settings-import-movement-count")
            ImportCount("Result", preview.resultCount, "settings-import-result-count")
            ImportCount("Lifestyle", preview.lifestyleLogCount, "settings-import-lifestyle-count")
            ImportCount("AI Report", preview.aiReportCount, "settings-import-report-count")
            preview.errors.forEachIndexed { index, error ->
                Text(
                    text = "${error.type}: ${error.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("settings-import-error-$index")
                )
            }
        }
    }
}

@Composable
private fun ImportCount(
    label: String,
    count: Int,
    tag: String
) {
    WodLogMetricChip(
        label = label,
        value = count.toString(),
        modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    WodLogCard(
        modifier = modifier.fillMaxWidth(),
        title = title,
        subtitle = subtitle
    ) {
        content()
    }
}
