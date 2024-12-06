package com.non.k4r.module.todo.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.non.k4r.module.common.K4rDatePickerDialog
import com.non.k4r.module.common.K4rTextField
import com.non.k4r.module.common.MainRoute
import com.non.k4r.module.common.MainScreen
import com.non.k4r.module.expenditure.component.TAG
import com.non.k4r.module.todo.vm.TodoSubmitScreenViewModel
import com.non.k4r.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TodoCard(
    modifier: Modifier = Modifier,
    introduction: String,
    remark: String,
    finished: Boolean = false,
    dueDate: LocalDate?,
    onCheck: (Boolean) -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 120.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    introduction,
                    style = MaterialTheme.typography.titleLarge
                )
                if (dueDate != null) {
                    Text(dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                }
            }

            Checkbox(
                onCheckedChange = {
                    onCheck(it)
                },
                checked = finished,
                modifier = Modifier.weight(0.1f)
            )
        }

    }
}

@Composable
fun TodoSubmitScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TodoSubmitScreenViewModel = hiltViewModel<TodoSubmitScreenViewModel>()
) {
    AppTheme {
        val focusManager = LocalFocusManager.current
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.datePickerDialogDisplayFlag) {
            K4rDatePickerDialog(
                onDateSelected = { millis ->
                    if (millis != null) {
                        try {
                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onDateSuccessfulSelected(date)
                        } catch (_: Exception) {
                        }
                    }
                    viewModel.displayDatePickerDialog(false)
                    focusManager
                },
                onDismiss = {
                    viewModel.displayDatePickerDialog(false)
                    focusManager.clearFocus(true)
                })
        }
        Surface(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),

                contentAlignment = BiasAlignment(0f, -0.1f),

                ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "待办",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    K4rTextField(
                        placeholder = "请输入简介",
                        label = "简介",
                        onValueChange = viewModel::onIntroductionChanged,
                        value = uiState.introduction,
                        modifier = Modifier.fillMaxWidth()

                    )
                    K4rTextField(
                        placeholder = "请输入备注",
                        label = "备注",
                        onValueChange = viewModel::onRemarkChanged,
                        value = uiState.remark,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    K4rTextField(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                        },
                        value = uiState.date?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                            ?: "",
                        label = "截至日期",
                        placeholder = "请选择一个日期",
                        readOnly = true,
                        onValueChange = {},
                        onClick = { Log.d(TAG, "ExpenditureSubmitScreen: Click") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    viewModel.displayDatePickerDialog(true)
                                }
                            }
                    )
                    Spacer(Modifier.padding(vertical = 4.dp))
                    ElevatedButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(alignment = Alignment.End),
                        onClick = {
                            viewModel.onSubmitClicked(onSuccess = { navController.navigate(MainRoute) })
                        }) {
                        Text("确认")
                    }
                }
            }
        }
    }
}
