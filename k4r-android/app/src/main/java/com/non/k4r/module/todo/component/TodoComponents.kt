package com.non.k4r.module.todo.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 任务状态图标背景
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (finished) 
                                Color(0xFFE8F5E8) 
                            else 
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (finished) 
                            Icons.Default.Done 
                        else 
                            Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (finished) 
                            Color(0xFF2E7D32) 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = introduction,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (finished) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (finished) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (remark.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remark,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                    
                    if (dueDate != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val isOverdue = dueDate.isBefore(LocalDate.now()) && !finished
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isOverdue) 
                                        Color(0xFFFFF0F0) 
                                    else 
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) 
                                    Color(0xFFD32F2F) 
                                else 
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // 自定义复选框
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (finished) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        .clickable { onCheck(!finished) },
                    contentAlignment = Alignment.Center
                ) {
                    if (finished) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "已完成",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
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
