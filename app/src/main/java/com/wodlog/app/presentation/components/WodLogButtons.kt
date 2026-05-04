package com.wodlog.app.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun WodLogPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled && !loading,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        WodLogButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun WodLogSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled && !loading,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        WodLogButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun WodLogDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.38f),
            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        WodLogButtonContent(
            text = text,
            loading = loading,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun WodLogTextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text)
    }
}

@Composable
private fun WodLogButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: (@Composable () -> Unit)?
) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
        )
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            Text(text)
        }
    }
}
