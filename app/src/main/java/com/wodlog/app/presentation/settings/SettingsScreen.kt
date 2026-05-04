package com.wodlog.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onProfileClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {},
    onLicenseClick: () -> Unit = {},
    onExportJsonClick: () -> Unit = {},
    onImportJsonClick: () -> Unit = {},
    exportState: SettingsExportState = SettingsExportState(),
    importState: SettingsImportState = SettingsImportState(),
    appVersion: String = "0.1.0-dev",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-settings")
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineMedium
        )

        SettingsSection(
            title = "프로필과 생활습관",
            modifier = Modifier.testTag("settings-section-profile")
        ) {
            Button(
                onClick = onProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-profile")
            ) {
                Text("프로필")
            }
            OutlinedButton(
                onClick = onLifestyleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-lifestyle")
            ) {
                Text("생활습관")
            }
        }

        SettingsSection(
            title = "앱 정보",
            modifier = Modifier.testTag("settings-section-app-info")
        ) {
            Text("앱 이름: 와드로그")
            Text(
                text = "버전: $appVersion",
                modifier = Modifier.testTag("settings-app-version")
            )
            Text(
                text = "GitHub 링크: 준비 중",
                modifier = Modifier.testTag("settings-github-placeholder")
            )
        }

        SettingsSection(
            title = "데이터",
            modifier = Modifier.testTag("settings-section-data")
        ) {
            Button(
                onClick = onExportJsonClick,
                enabled = !exportState.isExporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-export-json")
            ) {
                Text(if (exportState.isExporting) "JSON 내보내는 중" else "JSON 내보내기")
            }
            ExportStatus(exportState = exportState)

            OutlinedButton(
                onClick = onImportJsonClick,
                enabled = !importState.isImporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-import-json")
            ) {
                Text(if (importState.isImporting) "JSON 검증 중" else "JSON 가져오기 미리보기")
            }
            ImportStatus(importState = importState)
        }

        SettingsSection(
            title = "라이선스",
            modifier = Modifier.testTag("settings-section-license")
        ) {
            Button(
                onClick = onLicenseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-open-license")
            ) {
                Text("MIT License 보기")
            }
        }

        SettingsSection(
            title = "위험 구역",
            modifier = Modifier.testTag("settings-section-danger")
        ) {
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-reset-data-placeholder")
            ) {
                Text("앱 데이터 초기화 준비 중")
            }
        }
    }
}

@Composable
private fun ExportStatus(exportState: SettingsExportState) {
    if (exportState.isExporting) {
        Text(
            text = "백업 JSON을 준비하고 있습니다.",
            modifier = Modifier.testTag("settings-export-progress")
        )
    }
    exportState.message?.let { message ->
        Text(
            text = message,
            modifier = Modifier.testTag("text-settings-export-message")
        )
    }
    exportState.errorMessage?.let { errorMessage ->
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("text-settings-export-error")
        )
    }
}

@Composable
private fun ImportStatus(importState: SettingsImportState) {
    if (importState.isImporting) {
        Text(
            text = "백업 JSON을 읽고 검증하고 있습니다.",
            modifier = Modifier.testTag("settings-import-progress")
        )
    }
    importState.message?.let { message ->
        Text(
            text = message,
            modifier = Modifier.testTag("text-settings-import-message")
        )
    }
    importState.errorMessage?.let { errorMessage ->
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("text-settings-import-error")
        )
    }
    importState.preview?.let { preview ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings-import-preview"),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (preview.isValid) "검증 결과: 가져오기 가능" else "검증 결과: 가져오기 불가",
                modifier = Modifier.testTag("settings-import-validity")
            )
            Text(
                text = "WOD: ${preview.wodCount}",
                modifier = Modifier.testTag("settings-import-wod-count")
            )
            Text(
                text = "Movement: ${preview.movementCount}",
                modifier = Modifier.testTag("settings-import-movement-count")
            )
            Text(
                text = "Result: ${preview.resultCount}",
                modifier = Modifier.testTag("settings-import-result-count")
            )
            Text(
                text = "Lifestyle: ${preview.lifestyleLogCount}",
                modifier = Modifier.testTag("settings-import-lifestyle-count")
            )
            Text(
                text = "AI Report: ${preview.aiReportCount}",
                modifier = Modifier.testTag("settings-import-report-count")
            )
            preview.errors.forEachIndexed { index, error ->
                Text(
                    text = "${error.type}: ${error.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("settings-import-error-$index")
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        content()
    }
}
