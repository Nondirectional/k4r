package com.non.k4r.module.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.non.k4r.R
import com.non.k4r.module.expenditure.ExpenditureCard
import com.non.k4r.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@Composable
fun MainScreen(modifier: Modifier = Modifier, onNavigateToExpenditureSubmit: () -> Unit) {
    AppTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
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
                        Spacer(Modifier.height(16.dp))
                        DrawerItemButton(
                            isSelected = true,
                            onClick = {},
                            icon = Icons.Default.Home,
                            text = "首页"
                        )
                        DrawerItemButton(
                            isSelected = false,
                            onClick = {},
                            icon = Icons.Default.ShoppingCart,
                            text = "开支"
                        )

                    }
                }

            }
        ) {
            Scaffold(
                topBar = {
                    Surface(
                        modifier = Modifier.safeDrawingPadding()
                    ) {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
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
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            Log.d("MainActivity", "navigate to ExpenditureSubmitScreen")
                            onNavigateToExpenditureSubmit() },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "添加记录"
                            )
                        }
                    )
                })

        }
    }
}

@Composable
fun DrawerItemButton(
    icon: ImageVector?,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = ButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.12f
            ),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f
            ),
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) Icon(imageVector = icon, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text(text = text, Modifier.weight(1f))
        }
    }
}

@Composable
fun TimelineScreen(modifier: Modifier) {
    Surface(modifier = modifier) {
        val list = (1..100).toList()
        LazyColumn() {
            items(list) { number ->
                Column {
                    RecordCard(datetime = LocalDateTime.now()) {
                        ExpenditureCard(
                            title = "购物",
                            amount = -10.0,
                            tags = listOf("购物", "超市"),
                            detail = "购买超市购物券"
                        )
                    }
                }
            }
        }
    }
}