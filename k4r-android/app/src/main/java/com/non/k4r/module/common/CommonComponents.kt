package com.non.k4r.module.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    datetime: LocalDateTime,
    cardImpl: @Composable () -> Unit,
) {
    Column(Modifier.padding(horizontal = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 0.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.618f),
            text = "${
                datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }"
        )
        cardImpl()
    }
}

@Composable
fun K4rTextField(
    value: String = "",
    placeholder: String = "",
    label: String = "",
    colors: TextFieldColors? = null,
    onValueChange: (String) -> Unit,
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

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = colors ?: defaultColors,
        placeholder = { Text(text = placeholder) },
        leadingIcon = null,
        trailingIcon = null,
        label = { Text(text = label) },
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}