package com.non.k4r.module.expenditure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.non.k4r.R
import com.non.k4r.ui.theme.AppTheme
import java.util.Locale
import kotlin.math.abs

@Composable
fun ExpenditureSubmitScreen(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf("")) }
    var amount by remember { mutableFloatStateOf(0.00f) }

    AppTheme {
        Box(
            modifier = modifier
                .safeDrawingPadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                TextField(
                    placeholder = { Text("Enter amount") },
                    label = { Text("Amount") },
                    onValueChange = {},
                    value = "",
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    placeholder = { Text("Enter title") },
                    label = { Text("Title") },
                    onValueChange = {},
                    value = "",
                    modifier = Modifier.fillMaxWidth()

                )
                TextField(
                    placeholder = { Text("Enter Detail") },
                    label = { Text("Remark") },
                    onValueChange = {},
                    value = "",
                    modifier = Modifier.fillMaxWidth()

                )
                Row {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .heightIn(min = 50.dp)
                            .background(color = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(fontSize = 24.sp, text = "支出")
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .heightIn(min = 50.dp)
                            .background(color = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(fontSize = 24.sp, text = "支出")
                    }
                }
            }
            ElevatedButton(
                modifier = Modifier.align(alignment = Alignment.BottomEnd),
                onClick = {}) {
                Text("Submit")
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
    detail: String
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