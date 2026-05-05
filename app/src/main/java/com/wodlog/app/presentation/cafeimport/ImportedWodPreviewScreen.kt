package com.wodlog.app.presentation.cafeimport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogSecondaryButton

@Composable
fun ImportedWodPreviewScreen(
    importedWodText: ImportedWodText?,
    onBackClick: () -> Unit,
    onApplyToWodEdit: (ImportedWodText) -> Boolean = { false },
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var applyErrorMessage by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-imported-wod-preview")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "가져온 WOD 미리보기",
            style = MaterialTheme.typography.headlineMedium
        )

        if (importedWodText == null) {
            Text(
                text = "가져온 본문을 찾지 못했습니다. 다시 가져오기를 시도해 주세요.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-imported-wod-preview-missing")
            )
            WodLogSecondaryButton(
                text = "뒤로",
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action-back-imported-wod-preview")
            )
            return@Column
        }

        applyErrorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-imported-wod-apply-error")
            )
        }

        WodLogCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("imported-wod-preview-source"),
            title = importedWodText.title.ifBlank { "게시글 제목 없음" },
            subtitle = importedWodText.sourceUrl
        ) {
            Text(
                text = importedWodText.sourceUrl,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("text-imported-wod-source-url")
            )
        }

        WodLogCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("imported-wod-preview-body"),
            title = "가져온 본문"
        ) {
            Text(
                text = importedWodText.importedText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-imported-wod-body")
            )
            Text(
                text = "복사해서 WOD 추가에 직접 붙여넣을 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WodLogPrimaryButton(
            text = "WOD 입력에 적용",
            onClick = {
                val applied = onApplyToWodEdit(importedWodText)
                if (!applied) {
                    applyErrorMessage = "가져온 내용을 입력 화면에 전달하지 못했습니다. 다시 시도해 주세요."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-apply-imported-wod-to-edit")
        )
        Text(
            text = "입력 화면에서 내용을 확인하고 수정한 뒤 저장해 주세요.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-imported-wod-apply-guide")
        )
        WodLogPrimaryButton(
            text = "복사",
            onClick = {
                clipboardManager.setText(AnnotatedString(importedWodText.importedText))
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-copy-imported-wod")
        )
        WodLogSecondaryButton(
            text = "뒤로",
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-back-imported-wod-preview")
        )
        Text(
            text = "저장은 WOD 입력 화면의 저장 버튼을 누를 때만 진행됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-imported-wod-next-phase")
        )
    }
}
