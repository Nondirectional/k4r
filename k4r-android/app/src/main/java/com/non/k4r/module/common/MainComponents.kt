package com.non.k4r.module.common

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
import com.non.k4r.module.voice.VoiceRecognitionOverlay
import com.non.k4r.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val voiceProcessResult by viewModel.voiceProcessResult.collectAsState()
    val backStackEntry = navController.currentBackStackEntry
    val context = LocalContext.current
    
    // 语音识别状态
    var isListening by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var partialResult by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var displayedResult by remember { mutableStateOf("") }
    
    // 处理语音处理结果提醒
    LaunchedEffect(voiceProcessResult) {
        voiceProcessResult?.let { result ->
            when (result) {
                is com.non.k4r.module.expenditure.vm.VoiceProcessResult.Success -> {
                    // 显示成功提醒
                    android.widget.Toast.makeText(context, "✅ ${result.message}", android.widget.Toast.LENGTH_LONG).show()
                }
                is com.non.k4r.module.expenditure.vm.VoiceProcessResult.Error -> {
                    // 显示错误提醒
                    android.widget.Toast.makeText(context, "❌ ${result.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
            // 清除结果状态
            viewModel.clearVoiceProcessResult()
        }
    }

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
                        DrawerItemButton(
                            isSelected = false,
                            onClick = { navController.navigate(SettingsRoute) },
                            icon = Icons.Default.Settings,
                            text = "设置"
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
                        MultiFunctionFab(
                            onSingleClick = { navController.navigate(FeatureCatalogRoute) },
                            onDoubleClick = { navController.navigate(ChatRoute) },
                            onVoiceResult = { voiceText ->
                                viewModel.processVoiceCommand(voiceText)
                            },
                            onStateChange = { listening, pressed, partial, showRes, displayed ->
                                isListening = listening
                                isPressed = pressed
                                partialResult = partial
                                showResult = showRes
                                displayedResult = displayed
                            }
                        )
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
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (records.isEmpty()) {
            // 空状态显示
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无记录",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "点击右下角按钮开始添加",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(records) { record ->
                    RecordCard(
                        dateTime = when (record!!.type) {
                            RecordType.Expenditure -> {
                                val expenditureRecord = (record as ExpenditureRecordMainScreenVO).expenditureWithTags!!.expenditureRecord
                                expenditureRecord.expenditureDate.atTime(
                                    record.recordTime?.hour ?: 0,
                                    record.recordTime?.minute ?: 0,
                                    record.recordTime?.second ?: 0
                                )
                            }
                            else -> record.recordTime!!
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
                                    expenditureType = recordImpl.expenditureWithTags!!.expenditureRecord.expenditureType,
                                    tags = recordImpl.expenditureWithTags!!.tags.map { it.name },
                                    remark = recordImpl.expenditureWithTags!!.expenditureRecord.remark
                                )
                            }

                            RecordType.Todo -> {
                                val recordImpl = record as TodoRecordMainScreenVO

                                TodoCard(
                                    introduction = recordImpl.todoRecord!!.introduction,
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

/**
 * 多功能FloatingActionButton
 * - 单击：跳转到功能分类界面
 * - 双击：跳转到智能对话界面
 * - 长按：语音录入
 */
@Composable
fun MultiFunctionFab(
    onSingleClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onVoiceResult: (String) -> Unit,
    onStateChange: (isListening: Boolean, isPressed: Boolean, partialResult: String?, showResult: Boolean, displayedResult: String) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isPressed by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var displayedResult by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var isWaitingForStop by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    
    val voiceService = remember { DashscopeVoiceService(context) }
    val isListening by voiceService.isListening.collectAsState()
    val recognitionResult by voiceService.recognitionResult.collectAsState()
    val partialResult by voiceService.partialResult.collectAsState()
    val error by voiceService.error.collectAsState()
    
    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            voiceService.startListening()
        }
    }
    
    // 处理识别结果
    LaunchedEffect(recognitionResult) {
        recognitionResult?.let { result ->
            displayedResult = result
            showResult = true
            onVoiceResult(result)
            onStateChange(isListening, isPressed || isWaitingForStop, partialResult, showResult, displayedResult)
            delay(3000)
            showResult = false
            onStateChange(isListening, isPressed || isWaitingForStop, partialResult, showResult, displayedResult)
        }
    }
    
    // 监听停止录音状态
    LaunchedEffect(isListening) {
        if (!isListening && isProcessing) {
            delay(500)
            isProcessing = false
        }
        onStateChange(isListening, isPressed || isWaitingForStop, partialResult, showResult, displayedResult)
    }
    
    // 监听其他状态变化  
    LaunchedEffect(isPressed, isWaitingForStop, partialResult, showResult, displayedResult) {
        onStateChange(isListening, isPressed || isWaitingForStop, partialResult, showResult, displayedResult)
    }
    
    // 在录音完成后重置长按状态
    LaunchedEffect(isListening) {
        if (!isListening && !isWaitingForStop) {
            // 延迟一段时间后重置长按状态，避免立即触发单击
            delay(100)
            isLongPressing = false
        }
    }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            voiceService.destroy()
        }
    }
    
    Card(
        modifier = modifier
            .size(56.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // 只有不是长按状态时才处理单击
                        if (!isLongPressing) {
                            Log.d("MultiFunctionFab", "单击：跳转到功能分类界面")
                            onSingleClick()
                        } else {
                            Log.d("MultiFunctionFab", "长按后的点击事件被忽略")
                            isLongPressing = false
                        }
                    },
                    onDoubleTap = {
                        // 双击：跳转到智能对话界面
                        Log.d("MultiFunctionFab", "双击：跳转到智能对话界面")
                        onDoubleClick()
                    },
                    onPress = {
                        Log.d("MultiFunctionFab", "按钮被按下")
                        val pressStartTime = System.currentTimeMillis()
                        
                        // 等待500ms判断是否为长按
                        val isLongPress = try {
                            delay(500) // 长按阈值
                            true
                        } catch (e: Exception) {
                            false
                        }
                        
                        if (isLongPress) {
                            // 长按：语音录入
                            isLongPressing = true
                            Log.d("MultiFunctionFab", "检测到长按，开始语音录入，权限状态: $hasPermission")
                            
                            if (hasPermission) {
                                isPressed = true
                                isProcessing = true
                                showResult = false
                                Log.d("MultiFunctionFab", "开始录音")
                                voiceService.startListening()
                                
                                val released = tryAwaitRelease()
                                Log.d("MultiFunctionFab", "按钮释放状态: $released")
                                
                                if (released) {
                                    isPressed = false
                                    isWaitingForStop = true
                                    Log.d("MultiFunctionFab", "按钮正常释放，1秒后停止录音")
                                    delay(1000)
                                    voiceService.stopListening()
                                    isWaitingForStop = false
                                    Log.d("MultiFunctionFab", "延迟1秒后停止录音完成")
                                } else {
                                    isPressed = false
                                    isWaitingForStop = true
                                    Log.d("MultiFunctionFab", "按钮异常释放或取消，1秒后停止录音")
                                    delay(1000)
                                    voiceService.stopListening()
                                    isWaitingForStop = false
                                    Log.d("MultiFunctionFab", "延迟1秒后停止录音完成")
                                }
                            } else {
                                Log.d("MultiFunctionFab", "请求录音权限")
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        } else {
                            // 短按，等待释放
                            Log.d("MultiFunctionFab", "检测到短按，等待释放")
                            tryAwaitRelease()
                        }
                    }
                )
            },
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "多功能按钮：单击-功能分类，双击-智能对话，长按-语音录入",
                tint = if (isListening) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }
    }
    
    // 显示错误信息
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Log.e("MultiFunctionFab", "语音识别错误: $errorMessage")
        }
    }
}