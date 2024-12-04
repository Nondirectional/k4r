package com.non.k4r.module.common

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    datetime: LocalDate,
    cardImpl: @Composable () -> Unit,
) {
    Column(Modifier.padding(horizontal = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 0.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.618f),
            text = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )
        cardImpl()
    }
}

@Composable
fun K4rTextField(
    value: String = "",
    placeholder: String = "",
    label: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    colors: TextFieldColors? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = 1,
) {
    val defaultColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
    )

    Box(Modifier.clickable(onClick = onClick)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            colors = colors ?: defaultColors,
            placeholder = { Text(text = placeholder) },
            leadingIcon = leadingIcon,
            trailingIcon = null,
            label = { Text(text = label) },
            minLines = minLines,
            readOnly = readOnly,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun K4rDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}
