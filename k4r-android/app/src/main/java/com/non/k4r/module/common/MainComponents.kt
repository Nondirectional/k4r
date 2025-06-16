package com.non.k4r.module.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.non.k4r.R
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.module.common.model.RecordMainScreenVO
import com.non.k4r.module.expenditure.component.ExpenditureCard
import com.non.k4r.module.expenditure.model.ExpenditureRecordMainScreenVO
import com.non.k4r.module.expenditure.vm.MainScreenViewModel
import com.non.k4r.module.todo.TodoRecordMainScreenVO
import com.non.k4r.module.todo.component.TodoCard
import com.non.k4r.module.voice.DashscopeVoiceService
import com.non.k4r.module.voice.VoiceInputFab
import com.non.k4r.module.voice.VoiceRecognitionOverlay
import com.non.k4r.ui.theme.AppTheme
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry = navController.currentBackStackEntry
    val context = LocalContext.current
    
    // 语音识别状态
    var isListening by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var partialResult by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var displayedResult by remember { mutableStateOf("") }

    LaunchedEffect(backStackEntry) {
        // 每次进入 MainScreen 时调用 loadData
        viewModel.reloadRecords()
    }
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
            Box(modifier = modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                            ),
                            viewModel = viewModel,
                            records = uiState.records
                        )
                    },
                    floatingActionButton = {
                        Column {
                            VoiceInputFab(
                                onVoiceResult = { voiceText ->
                                    viewModel.processVoiceCommand(voiceText)
                                },
                                onStateChange = { listening, pressed, partial, showRes, displayed ->
                                    isListening = listening
                                    isPressed = pressed
                                    partialResult = partial
                                    showResult = showRes
                                    displayedResult = displayed
                                },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            FloatingActionButton(
                                onClick = { navController.navigate(FeatureCatalogRoute) },
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
                        }
                    })
                
                // 语音识别结果覆盖层
                VoiceRecognitionOverlay(
                    isListening = isListening,
                    partialResult = partialResult,
                    finalResult = displayedResult,
                    showResult = showResult,
                    isPressed = isPressed,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp) // 避免与TopBar重叠
                )
            }

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
fun TimelineScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    records: List<RecordMainScreenVO?> = emptyList()
) {
    Surface(modifier = modifier) {
        LazyColumn {
            items(records) { record ->
                Column {
                    RecordCard(
                        date =
                        when (record!!.type) {
                            RecordType.Expenditure -> (record as ExpenditureRecordMainScreenVO).expenditureWithTags!!.expenditureRecord.expenditureDate
                            else -> record.recordTime!!.toLocalDate()
                        },
                        onDelete = {
                            viewModel.deleteRecord(record.id!!)
                        }
                    ) {
                        when (record.type) {
                            RecordType.Expenditure -> {
                                val recordImpl = record as ExpenditureRecordMainScreenVO
                                ExpenditureCard(
                                    introduction = recordImpl.expenditureWithTags!!.expenditureRecord.introduction,
                                    amount = recordImpl.expenditureWithTags!!.expenditureRecord.amount / 100.0,
                                    tags = recordImpl.expenditureWithTags!!.tags.map { it.name },
                                    remark = recordImpl.expenditureWithTags!!.expenditureRecord.remark
                                )
                            }

                            RecordType.Todo -> {
                                val recordImpl = record as TodoRecordMainScreenVO

                                TodoCard(
                                    introduction = recordImpl.todoRecord!!.introduction,
                                    modifier = Modifier.padding(16.dp),
                                    remark = recordImpl.todoRecord!!.remark,
                                    finished = recordImpl.todoRecord!!.isCompleted,
                                    dueDate = recordImpl.todoRecord!!.dueDate,
                                    onCheck = {
                                        viewModel.toggleTodoRecord(recordImpl.todoRecord!!.recordId)
                                    },
                                )
                            }
                            else -> throw IllegalArgumentException("Unknown record type")
                        }

                    }
                }
            }
        }
    }
}