package com.wodlog.app.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WodLogConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "삭제",
    dismissText: String = "취소",
    dangerous: Boolean = true
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            if (dangerous) {
                WodLogDangerButton(
                    text = confirmText,
                    onClick = onConfirm
                )
            } else {
                WodLogPrimaryButton(
                    text = confirmText,
                    onClick = onConfirm
                )
            }
        },
        dismissButton = {
            WodLogTextActionButton(
                text = dismissText,
                onClick = onDismiss
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
