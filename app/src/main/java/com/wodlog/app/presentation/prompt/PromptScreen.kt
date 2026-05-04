package com.wodlog.app.presentation.prompt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Prompt",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Copy this prompt and paste it into ChatGPT yourself.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "This app does not call OpenAI API or send this prompt to a server.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("text-prompt-local-only-note")
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("prompt-loading")
            )
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("prompt-error")
            )
        }

        state.wodTitle?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("text-prompt-wod-title")
            )
        }

        Surface(
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state.promptText.ifBlank { "Prompt is not ready yet." },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("text-prompt-content")
                    .padding(16.dp)
            )
        }

        Button(
            onClick = {
                if (state.promptText.isNotBlank()) {
                    clipboardManager.setText(AnnotatedString(state.promptText))
                    onCopyClick()
                }
            },
            enabled = state.promptText.isNotBlank() && !state.isLoading,
            modifier = Modifier.testTag("action-copy-prompt")
        ) {
            Text("Copy prompt")
        }

        state.copyMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("text-prompt-copy-message")
            )
        }
    }
}
