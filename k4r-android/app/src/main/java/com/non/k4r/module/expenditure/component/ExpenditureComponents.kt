package com.non.k4r.module.expenditure.component

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.non.k4r.R
import com.non.k4r.core.data.database.constant.ExpenditureType
import com.non.k4r.core.data.database.model.ExpenditureTag
import com.non.k4r.module.common.K4rDatePickerDialog
import com.non.k4r.module.common.K4rTextField
import com.non.k4r.module.expenditure.vm.ExpenditureSubmitScreenViewModel
import com.non.k4r.ui.theme.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

const val TAG: String = "Expenditure"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenditureSubmitScreen(
    modifier: Modifier = Modifier,
    onSubmitSuccess: () -> Unit,
    viewModel: ExpenditureSubmitScreenViewModel = hiltViewModel<ExpenditureSubmitScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    AppTheme {
        val focusManager = LocalFocusManager.current

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
                        "开支/收入",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    K4rTextField(
                        placeholder = "请输入金额",
                        label = "金额",
                        onValueChange = viewModel::onAmountChanged,
                        value = uiState.amount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent(onFocusEvent = viewModel::onAmountFocusEvent),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        label = "日期",
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
                    Row(modifier = modifier.clip(shape = MaterialTheme.shapes.small)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .heightIn(min = 50.dp)
                                .clickable(onClick = {
                                    viewModel.onExpenditureTypeChanged(ExpenditureType.Expenditure)
                                })
                                .background(color = if (uiState.expenditureType == ExpenditureType.Expenditure) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(fontSize = 24.sp, text = "支出")
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .heightIn(min = 50.dp)
                                .clickable(onClick = {
                                    viewModel.onExpenditureTypeChanged(ExpenditureType.Income)
                                })
                                .background(color = if (uiState.expenditureType == ExpenditureType.Income) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(fontSize = 24.sp, text = "收入")
                        }
                    }

                    Spacer(modifier.padding(top = 4.dp))
                    ExpenditureTagSelector(
                        expenditureTags = uiState.tags,
                        selectedTags = uiState.selectedTags,
                        onTagClick = { expenditureTag, selected ->
                            if (selected) {
                                viewModel.onTagSelected(expenditureTag)
                            } else {
                                viewModel.onTagDeselected(expenditureTag)
                            }
                        },
                    )
                }
                ElevatedButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(alignment = BiasAlignment(0.95f, 1.0f)),
                    onClick = {
                        viewModel.onSubmitClicked(onSubmitSuccess)
                    }) {
                    Text("确认")
                }
            }
        }
    }
}


@Composable
fun ExpenditureCard(
    modifier: Modifier = Modifier,
    introduction: String,
    amount: Double,
    expenditureType: ExpenditureType,
    tags: List<String>,
    remark: String,
) {
    AppTheme {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            shape = MaterialTheme.shapes.medium,
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
                    // 图标背景圆形
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (expenditureType == ExpenditureType.Income) 
                                    Color(0xFFE8F5E8) 
                                else 
                                    Color(0xFFFFF0F0),
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_payments_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (expenditureType == ExpenditureType.Income) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = introduction,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
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
                        
                        if (tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(tags) { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 金额显示
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (expenditureType == ExpenditureType.Income) "+" else "-",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (expenditureType == ExpenditureType.Income) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                        Text(
                            text = String.format(
                                locale = Locale.getDefault(),
                                format = "%.2f",
                                abs(amount)
                            ),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (expenditureType == ExpenditureType.Income) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenditureTagSelector(
    modifier: Modifier = Modifier,
    expenditureTags: List<ExpenditureTag>,
    selectedTags: Map<String, ExpenditureTag>,
    onTagClick: (ExpenditureTag, Boolean) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            items(expenditureTags) { expenditureTag ->
                ExpenditureTagSelectorItem(
                    expenditureTag = expenditureTag,
                    selected = selectedTags.contains(expenditureTag.key),
                    onClick = {
                        if (selectedTags.contains(expenditureTag.key)) {
                            onTagClick(expenditureTag, false)
                        } else {
                            onTagClick(expenditureTag, true)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenditureTagSelectorItem(
    modifier: Modifier = Modifier,
    expenditureTag: ExpenditureTag,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(4.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(color = if (!selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.onPrimary)
    ) {
        Text(
            text = expenditureTag.name,
            style = MaterialTheme.typography.titleMedium
        )
    }

}