package com.non.k4r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.non.k4r.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }

    }

}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    AppTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(modifier= Modifier.background(color = MaterialTheme.colorScheme.background)) {
                    Column(
                        modifier = Modifier
                            .safeDrawingPadding()
                            .fillMaxHeight()
                            .width(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Image(
                            painter = painterResource(R.mipmap.default_avatar),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(color = MaterialTheme.colorScheme.onSurface, text = "未登录")

                    }
                }

            }
        ) {
            Scaffold(
                topBar = {
                    Surface(
                        modifier = Modifier.safeDrawingPadding()
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "打开侧边栏")
                        }
                    }
                },
                content = { innerPadding ->
                    TimelineScreen(
                        modifier.padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding()
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun TimelineScreen(modifier: Modifier) {
    Surface(modifier = modifier) {
        val list = (1..100).toList()
        LazyColumn {
            items(list) { number ->
                Text("$number")
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
    AppTheme() {
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
