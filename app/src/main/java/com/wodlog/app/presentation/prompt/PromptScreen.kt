package com.wodlog.app.presentation.prompt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.wodlog.app.presentation.components.WodLogCard
import com.wodlog.app.presentation.components.WodLogPrimaryButton
import com.wodlog.app.presentation.components.WodLogStatusChip
import com.wodlog.app.presentation.components.WodLogStatusChipTone

@Composable
fun PromptRoute(
    viewModel: PromptViewModel,
    wodId: Long
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(wodId) {
        viewModel.loadPrompt(wodId)
    }

    PromptScreen(
        state = state,
        onCopyClick = viewModel::onCopied
    )
}

@Composable
fun PromptScreen(
    state: PromptUiState,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen-prompt")
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ChatGPT 질문지",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "질문지를 복사한 뒤 ChatGPT에 직접 붙여넣으세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "이 앱은 OpenAI API를 호출하거나 질문지를 서버로 보내지 않습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("text-prompt-local-only-note")
        )

        if (state.isLoading) {
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.testTag("prompt-loading"))
                Text(
                    text = "질문지를 준비하는 중입니다",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        state.errorMessage?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("prompt-error")
                )
            }
        }

        state.wodTitle?.let { title ->
            WodLogCard(
                modifier = Modifier.fillMaxWidth(),
                title = "대상 WOD",
                actions = {
                    WodLogStatusChip(
                        text = "로컬 복사",
                        tone = WodLogStatusChipTone.Neutral
                    )
                }
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("text-prompt-wod-title")
                )
            }
        }

        WodLogCard(
            title = "질문지 내용",
            subtitle = "전체 내용을 훑어본 뒤 복사할 수 있습니다.",
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state.promptText.ifBlank { "질문지가 아직 준비되지 않았습니다." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("text-prompt-content")
            )
        }

        WodLogPrimaryButton(
            text = "질문지 복사",
            onClick = {
                if (state.promptText.isNotBlank()) {
                    clipboardManager.setText(AnnotatedString(state.promptText))
                    onCopyClick()
                }
            },
            enabled = state.promptText.isNotBlank() && !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action-copy-prompt")
        )

        state.copyMessage?.let { message ->
            WodLogCard(outlined = false, modifier = Modifier.fillMaxWidth()) {
                WodLogStatusChip(
                    text = message,
                    tone = WodLogStatusChipTone.Success,
                    modifier = Modifier.testTag("text-prompt-copy-message")
                )
            }
        }
    }
}
