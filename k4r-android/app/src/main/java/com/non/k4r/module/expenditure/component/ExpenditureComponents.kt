package com.non.k4r.module.expenditure.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.non.k4r.R
import com.non.k4r.module.common.K4rTextField
import com.non.k4r.module.expenditure.ExpenditureType
import com.non.k4r.module.expenditure.dao.ExpenditureTagDao
import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity
import com.non.k4r.ui.theme.AppTheme
import java.util.Locale
import kotlin.math.abs

const val TAG: String = "Expenditure"

@Composable
fun ExpenditureSubmitScreen(
    modifier: Modifier = Modifier,
    expenditureTagDao: ExpenditureTagDao,
) {
//    val tagListViewModel: ExpenditureTagListViewModel = viewModel()
    val displayTags by tagListViewModel.tags.collectAsState()
    LaunchedEffect(true) {
        tagListViewModel.loadTags(dao = expenditureTagDao)
    }

    var introduction by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenditureType by remember { mutableStateOf(ExpenditureType.Expenditure) }
    var selectedTas = remember { mutableStateMapOf<String, ExpenditureTagEntity>() }

    AppTheme {
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
                        onValueChange = {
                            try {
                                amount = if (it.isBlank())
                                    it
                                else
                                    "-?(0|[1-9]?[0-9]+)+\\.?([0-9]+)?".toRegex()
                                        .find(it)?.value ?: amount
                            } catch (e: Exception) {
                                Log.e(TAG, "ExpenditureSubmitScreen: fail", e)
                            }
                        },
                        value = amount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusEvent {
                                if (!it.hasFocus) {
                                    if (!amount.isBlank())
                                        amount = try {
                                            String.format(
                                                locale = Locale.getDefault(),
                                                format = "%.2f",
                                                amount.toFloat()
                                            )
                                        } catch (_: Exception) {
                                            "0.00"
                                        }
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = {
                            amount =
                                String.format(locale = Locale.getDefault(), format = "%.2f", amount)
                        })
                    )
                    K4rTextField(
                        placeholder = "请输入简介",
                        label = "简介",
                        onValueChange = { introduction = it },
                        value = introduction,
                        modifier = Modifier.fillMaxWidth()

                    )
                    K4rTextField(
                        placeholder = "请输入备注",
                        label = "备注",
                        onValueChange = { remark = it },
                        value = remark,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.padding(vertical = 4.dp))
                    Row(modifier = modifier.clip(shape = MaterialTheme.shapes.small)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .heightIn(min = 50.dp)
                                .clickable(onClick = {
                                    expenditureType = ExpenditureType.Expenditure
                                })
                                .background(color = if (expenditureType == ExpenditureType.Expenditure) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(fontSize = 24.sp, text = "支出")
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .heightIn(min = 50.dp)
                                .clickable(onClick = {
                                    expenditureType = ExpenditureType.Income
                                })
                                .background(color = if (expenditureType == ExpenditureType.Income) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(fontSize = 24.sp, text = "支出")
                        }
                    }

                    Spacer(modifier.padding(top = 4.dp))
                    ExpenditureTagSelector(expenditureTags = displayTags, selectedTas = selectedTas)
                }
                ElevatedButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(alignment = BiasAlignment(0.95f, 1.0f)),
                    onClick = {
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
    title: String,
    amount: Double,
    tags: List<String>,
    detail: String,
) {
    AppTheme {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 3.dp,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_currency_yuan_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .weight(0.2f),
                )

                Column(Modifier.weight(0.5f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        tags.forEach {
                            Text(
                                text = "#$it",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Text(
                    text = "${if (amount < 0) "-" else "+"} ${
                        String.format(
                            locale = Locale.getDefault(),
                            format = "%.2f",
                            abs(amount)
                        )
                    }",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (amount > 0) Color.Red else Color(0xFF008000),
                    modifier = Modifier.weight(0.3f)
                )
            }

        }
    }
}

@Composable
fun ExpenditureTagSelector(
    modifier: Modifier = Modifier,
    expenditureTags: List<ExpenditureTagEntity>,
    selectedTas: MutableMap<String, ExpenditureTagEntity>,
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
                    selected = selectedTas.contains(expenditureTag.key),
                    onClick = {
                        if (selectedTas.contains(expenditureTag.key)) {
                            selectedTas.remove(expenditureTag.key)
                        } else {
                            selectedTas[expenditureTag.key] = expenditureTag
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
    expenditureTag: ExpenditureTagEntity,
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

@Preview
@Composable
fun TagSelectorPreview() {
    val expenditureTags = listOf<ExpenditureTagEntity>(
        ExpenditureTagEntity(key = "1", name = "Tag1"),
        ExpenditureTagEntity(key = "2", name = "Tag2"),
        ExpenditureTagEntity(key = "3", name = "Tag3"),
        ExpenditureTagEntity(key = "4", name = "Tag4"),
        ExpenditureTagEntity(key = "5", name = "Tag5"),
        ExpenditureTagEntity(key = "6", name = "Tag6"),
    )
    var selectedTas = remember { mutableStateMapOf<String, ExpenditureTagEntity>() }

    ExpenditureTagSelector(expenditureTags = expenditureTags, selectedTas = selectedTas)
}