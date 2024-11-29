package com.non.k4r.module.expenditure

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.non.k4r.module.common.ExpenditureSubmitRoute
import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity
import com.non.k4r.ui.theme.AppTheme
import java.util.Locale
import kotlin.math.abs

const val TAG: String = "Expenditure"

@Composable
fun ExpenditureSubmitScreen(
    modifier: Modifier = Modifier,
    route: ExpenditureSubmitRoute,
) {
    var introduction by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenditureType by remember { mutableStateOf(ExpenditureType.Expenditure) }
    var selectedTas = remember { mutableStateMapOf<String, ExpenditureTagEntity>() }
    AppTheme {
        Box(
            modifier = modifier
                .safeDrawingPadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "开支/收入",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    placeholder = { Text("请输入金额") },
                    label = {
                        Text("金额")
                    },
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
                                amount = try {
                                    String.format(
                                        locale = Locale.getDefault(),
                                        format = "%.2f",
                                        amount.toFloat()
                                    )
                                } catch (e: Exception) {
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
                TextField(
                    placeholder = { Text("请输入简介") },
                    label = { Text("简介") },
                    onValueChange = { introduction = it },
                    value = "",
                    modifier = Modifier.fillMaxWidth()

                )
                TextField(
                    placeholder = { Text("请输入备注") },
                    label = { Text("备注") },
                    onValueChange = { remark = it },
                    value = "",
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()

                )

                Row {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .heightIn(min = 50.dp)
                            .clickable(onClick = {
                                expenditureType = ExpenditureType.Expenditure
                            })
                            .background(color = if (expenditureType == ExpenditureType.Expenditure) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
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
                            .background(color = if (expenditureType == ExpenditureType.Income) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(fontSize = 24.sp, text = "支出")
                    }
                }

                ExpenditureTagSelector(expenditureTags = expenditureTags, selectedTas = selectedTas)
            }
            ElevatedButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = BiasAlignment(0.9f, 0.9f)),
                onClick = {}) {
                Text("确认")
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
            color = MaterialTheme.colorScheme.primaryContainer,
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
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        tags.forEach {
                            Text(
                                text = "#$it",
                                color = Color(0xFF21BECE),
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
    selectedTas: MutableMap<String, ExpenditureTagEntity>
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(8.dp)
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
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(4.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(color = if (!selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
    ) {
        Text(text = expenditureTag.name, style = MaterialTheme.typography.titleMedium)
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
        ExpenditureTagEntity(key = "4", name = "Tag4"),
        ExpenditureTagEntity(key = "4", name = "Tag4"),
        ExpenditureTagEntity(key = "4", name = "Tag4"),
        ExpenditureTagEntity(key = "4", name = "Tag4"),
        ExpenditureTagEntity(key = "4", name = "Tag4")
    )
    var selectedTas = remember { mutableStateMapOf<String, ExpenditureTagEntity>() }

    ExpenditureTagSelector(expenditureTags = expenditureTags, selectedTas = selectedTas)
}