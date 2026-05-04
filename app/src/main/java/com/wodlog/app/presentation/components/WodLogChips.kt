package com.wodlog.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wodlog.app.presentation.theme.WodlogStatusColor

enum class WodLogStatusChipTone {
    Neutral,
    Primary,
    Success,
    Warning,
    Error
}

@Composable
fun WodLogMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val chipLabel: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = if (unit.isNullOrBlank()) value else "$value $unit",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    if (onClick == null) {
        AssistChip(
            modifier = modifier.defaultMinSize(minHeight = 48.dp),
            onClick = {},
            label = chipLabel,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    } else {
        FilterChip(
            modifier = modifier.defaultMinSize(minHeight = 48.dp),
            selected = selected,
            onClick = onClick,
            label = chipLabel
        )
    }
}

@Composable
fun WodLogStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    tone: WodLogStatusChipTone = WodLogStatusChipTone.Neutral,
    onClick: (() -> Unit)? = null
) {
    val containerColor = statusContainerColor(tone, selected)
    val contentColor = statusContentColor(tone, selected)

    if (onClick == null) {
        AssistChip(
            modifier = modifier.defaultMinSize(minHeight = 40.dp),
            onClick = {},
            label = { Text(text) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = containerColor,
                labelColor = contentColor
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = if (selected) contentColor else MaterialTheme.colorScheme.outlineVariant
            )
        )
    } else {
        FilterChip(
            modifier = modifier.defaultMinSize(minHeight = 40.dp),
            selected = selected,
            onClick = onClick,
            label = { Text(text) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = containerColor,
                selectedLabelColor = contentColor
            )
        )
    }
}

@Composable
private fun statusContainerColor(
    tone: WodLogStatusChipTone,
    selected: Boolean
): Color = when (tone) {
    WodLogStatusChipTone.Neutral -> if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    WodLogStatusChipTone.Primary -> MaterialTheme.colorScheme.primaryContainer
    WodLogStatusChipTone.Success -> WodlogStatusColor.SuccessContainer
    WodLogStatusChipTone.Warning -> WodlogStatusColor.WarningContainer
    WodLogStatusChipTone.Error -> MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun statusContentColor(
    tone: WodLogStatusChipTone,
    selected: Boolean
): Color = when (tone) {
    WodLogStatusChipTone.Neutral -> if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    WodLogStatusChipTone.Primary -> MaterialTheme.colorScheme.onPrimaryContainer
    WodLogStatusChipTone.Success -> WodlogStatusColor.Success
    WodLogStatusChipTone.Warning -> WodlogStatusColor.Warning
    WodLogStatusChipTone.Error -> MaterialTheme.colorScheme.onErrorContainer
}
