package com.non.k4r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.non.k4r.ui.theme.K4rTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            K4rTheme {
            }
        }
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    datetime: LocalDateTime,
    cardImpl: @Composable () -> Unit
) {
    Column {
        Text(datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        cardImpl()
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
    K4rTheme {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
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

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CardPreview() {
    Card(
        datetime = LocalDateTime.now(),
        cardImpl = {
            ExpenditureCard(
                amount = -1000.0,
                title = "购物",
                tags = listOf("食品", "饮品"),
                detail = "去楼下超市买了一箱牛奶"
            )
        }
    )
}


@Preview(widthDp = 360)
@Composable
private fun ExpenditureCardPreview() {
    Column {
        ExpenditureCard(
            amount = -1000.0,
            title = "购物",
            tags = listOf("食品", "饮品"),
            detail = "去楼下超市买了一箱牛奶"
        )

        ExpenditureCard(
            amount = 1000.0,
            title = "意外收入",
            tags = listOf("意外之财"),
            detail = "路上捡到钱"
        )
    }

}