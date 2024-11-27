package com.non.k4r.module.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    datetime: LocalDateTime,
    cardImpl: @Composable () -> Unit
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