package com.wodlog.app.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wodlog.app.domain.backup.BackupExportUseCase
import com.wodlog.app.domain.backup.BackupImportPreview
import com.wodlog.app.domain.backup.BackupImportPreviewUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsExportState(
    val isExporting: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
)

data class SettingsImportState(
    val isImporting: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
    val preview: BackupImportPreview? = null,
)

@Composable
fun SettingsRoute(
    backupExportUseCase: BackupExportUseCase,
    backupImportPreviewUseCase: BackupImportPreviewUseCase,
    onProfileClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {},
    onLicenseClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportState by remember { mutableStateOf(SettingsExportState()) }
    var importState by remember { mutableStateOf(SettingsImportState()) }
    var pendingJson by remember { mutableStateOf<String?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingJson
        if (uri == null) {
            pendingJson = null
            exportState = SettingsExportState(message = "JSON 내보내기를 취소했습니다.")
            return@rememberLauncherForActivityResult
        }
        if (json.isNullOrBlank()) {
            pendingJson = null
            exportState = SettingsExportState(errorMessage = "내보낼 JSON 데이터가 없습니다.")
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            exportState = SettingsExportState(isExporting = true)
            runCatching {
                writeTextToUri(context, uri, json)
            }.onSuccess {
                exportState = SettingsExportState(message = "JSON 내보내기를 완료했습니다.")
            }.onFailure {
                exportState = SettingsExportState(errorMessage = "JSON 내보내기에 실패했습니다.")
            }
            pendingJson = null
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            importState = SettingsImportState(message = "JSON 가져오기를 취소했습니다.")
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            importState = SettingsImportState(isImporting = true)
            runCatching {
                val json = readTextFromUri(context, uri)
                backupImportPreviewUseCase.preview(json)
            }.onSuccess { preview ->
                importState = SettingsImportState(
                    message = if (preview.isValid) {
                        "가져오기 파일을 확인했습니다. 아직 DB에는 반영하지 않았습니다."
                    } else {
                        "가져올 수 없는 백업 파일입니다."
                    },
                    preview = preview
                )
            }.onFailure {
                importState = SettingsImportState(errorMessage = "JSON 파일을 읽거나 검증하지 못했습니다.")
            }
        }
    }

    SettingsScreen(
        onProfileClick = onProfileClick,
        onLifestyleClick = onLifestyleClick,
        onLicenseClick = onLicenseClick,
        onExportJsonClick = {
            scope.launch {
                exportState = SettingsExportState(isExporting = true)
                runCatching {
                    backupExportUseCase.exportJson()
                }.onSuccess { json ->
                    pendingJson = json
                    createDocumentLauncher.launch("wodlog-backup.json")
                }.onFailure {
                    pendingJson = null
                    exportState = SettingsExportState(errorMessage = "JSON 생성에 실패했습니다.")
                }
            }
        },
        onImportJsonClick = {
            importState = SettingsImportState(isImporting = true)
            openDocumentLauncher.launch(arrayOf("application/json", "text/*"))
        },
        exportState = exportState,
        importState = importState
    )
}

internal suspend fun writeTextToUri(
    context: Context,
    uri: Uri,
    text: String,
) {
    withContext(Dispatchers.IO) {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val outputStream = context.contentResolver.openOutputStream(uri)
            ?: error("Unable to open output stream.")
        outputStream.use { stream ->
            stream.write(bytes)
        }
    }
}

internal suspend fun readTextFromUri(
    context: Context,
    uri: Uri,
): String {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Unable to open input stream.")
        inputStream.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).readText()
        }
    }
}
