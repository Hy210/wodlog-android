package com.wodlog.app.presentation.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun WodLogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        isError = errorText != null,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = (errorText ?: supportingText)?.let { message ->
            {
                Text(message)
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.42f),
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.18f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WodLogDateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "yyyy-MM-dd",
    supportingText: String? = "직접 입력하거나 달력에서 선택하세요.",
    errorText: String? = null,
    enabled: Boolean = true,
    initialDate: LocalDate = LocalDate.now(),
    selectButtonText: String = "달력에서 선택"
) {
    var showPicker by remember { mutableStateOf(false) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    WodLogTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        supportingText = supportingText,
        errorText = errorText,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default,
        trailingIcon = {
            TextButton(
                onClick = { showPicker = true },
                enabled = enabled
            ) {
                Text(selectButtonText)
            }
        }
    )

    if (showPicker) {
        val selectedDate = value.toLocalDateOrNull() ?: initialDate
        val datePickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis
                            ?.toLocalDate()
                            ?.let { currentOnValueChange(it.toString()) }
                        showPicker = false
                    }
                ) {
                    Text("선택")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WodLogDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "선택하세요",
    enabled: Boolean = true,
    supportingText: String? = null,
    errorText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        }
    ) {
        WodLogTextField(
            modifier = Modifier.menuAnchor(
                type = MenuAnchorType.PrimaryNotEditable,
                enabled = enabled
            ),
            value = selectedOption.orEmpty(),
            onValueChange = {},
            label = label,
            placeholder = placeholder,
            supportingText = supportingText,
            errorText = errorText,
            enabled = enabled,
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun String.toLocalDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(trim()) }.getOrNull()

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
