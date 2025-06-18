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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.widthIn

import androidx.compose.material3.Button
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight

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
    
    // AI响应悬浮窗状态
    var showAiResponseOverlay by remember { mutableStateOf(false) }
    var aiResponseContent by remember { mutableStateOf("") }
    var aiResponseType by remember { mutableStateOf<com.non.k4r.module.expenditure.vm.VoiceProcessResult?>(null) }
    
    // 处理语音处理结果提醒 - 改为显示悬浮窗
    LaunchedEffect(voiceProcessResult) {
        voiceProcessResult?.let { result ->
            aiResponseType = result
            aiResponseContent = when (result) {
                is com.non.k4r.module.expenditure.vm.VoiceProcessResult.Success -> {
                    "✅ ${result.message}"
                }
                is com.non.k4r.module.expenditure.vm.VoiceProcessResult.Error -> {
                    "❌ ${result.message}"
                }
            }
            showAiResponseOverlay = true
            // 清除结果状态
            viewModel.clearVoiceProcessResult()
        }
    }
    
    // 自动关闭AI响应悬浮窗
    LaunchedEffect(showAiResponseOverlay) {
        if (showAiResponseOverlay) {
            kotlinx.coroutines.delay(5000) // 5秒后自动关闭
            showAiResponseOverlay = false
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
                            contentDescription = "点击登录",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    navController.navigate(LoginRoute)
                                }
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            color = MaterialTheme.colorScheme.onSurface, 
                            text = "点击头像登录",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
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
                            modifier.padding(innerPadding),
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
                            },
                            navController = navController
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
                
                // AI响应悬浮窗
                if (showAiResponseOverlay) {
                    AiResponseOverlay(
                        content = aiResponseContent,
                        isSuccess = aiResponseType is com.non.k4r.module.expenditure.vm.VoiceProcessResult.Success,
                        onDismiss = { showAiResponseOverlay = false },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp)
                    )
                }
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
    navController: NavController,
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
    var isComponentActive by remember { mutableStateOf(true) }
    
    // 检查是否还在MainScreen
    val currentRoute by remember {
        derivedStateOf {
            navController.currentBackStackEntry?.destination?.route
        }
    }
    val isInMainScreen by remember {
        derivedStateOf {
            currentRoute?.contains("MainRoute") == true
        }
    }
    
    val voiceService = remember { DashscopeVoiceService(context) }
    val isConnected by voiceService.isConnected.collectAsState()
    val isListening by voiceService.isListening.collectAsState()
    val recognitionResult by voiceService.recognitionResult.collectAsState()
    val partialResult by voiceService.partialResult.collectAsState()
    val error by voiceService.error.collectAsState()
    
    // 组件初始化时建立WebSocket连接
    LaunchedEffect(Unit) {
        Log.d("MultiFunctionFab", "初始化WebSocket连接")
        voiceService.initializeConnection()
    }
    
    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Log.d("MultiFunctionFab", "权限被拒绝")
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
    
    // 清理资源并标记组件为不活跃
    DisposableEffect(Unit) {
        onDispose {
            isComponentActive = false
            voiceService.destroy()
        }
    }
    
    Card(
        modifier = modifier
            .size(72.dp)
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
                        // 等待500ms判断是否为长按
                        val isLongPress = try {
                            delay(500) // 长按阈值
                            // 检查组件是否还在活跃状态且还在MainScreen
                            if (isComponentActive && currentCoroutineContext().isActive && isInMainScreen) {
                                true
                            } else {
                                Log.d("MultiFunctionFab", "组件已不在活跃状态或已离开MainScreen，取消长按操作")
                                false
                            }
                        } catch (e: Exception) {
                            Log.d("MultiFunctionFab", "长按检测被中断: ${e.message}")
                            false
                        }
                        
                        if (isLongPress && isComponentActive && isInMainScreen) {
                            // 长按：语音录入
                            isLongPressing = true
                            Log.d("MultiFunctionFab", "检测到长按，开始语音录入，权限状态: $hasPermission，连接状态: $isConnected")
                            
                            if (hasPermission) {
                                if (!isConnected) {
                                    Log.d("MultiFunctionFab", "WebSocket未连接，尝试重新连接")
                                    voiceService.initializeConnection()
                                    // 等待连接建立
                                    delay(1000)
                                    // 检查组件是否还在活跃状态且还在MainScreen
                                    if (!isComponentActive || !currentCoroutineContext().isActive || !isInMainScreen) {
                                        Log.d("MultiFunctionFab", "组件已不在活跃状态或已离开MainScreen，取消操作")
                                        return@detectTapGestures
                                    }
                                    if (!isConnected) {
                                        Log.e("MultiFunctionFab", "WebSocket连接失败")
                                        return@detectTapGestures
                                    }
                                }
                                
                                isPressed = true
                                isProcessing = true
                                showResult = false
                                Log.d("MultiFunctionFab", "开始语音识别任务")
                                voiceService.startListening()

                                
                                val released = tryAwaitRelease()
                                Log.d("MultiFunctionFab", "按钮释放状态: $released")
                                
                                if (released) {
                                    isPressed = false
                                    isWaitingForStop = true
                                    Log.d("MultiFunctionFab", "按钮正常释放，200毫秒后停止语音识别任务")
                                    delay(200)
                                    // 检查组件是否还在活跃状态且还在MainScreen
                                    if (isComponentActive && currentCoroutineContext().isActive && isInMainScreen) {
                                        voiceService.stopListening()
                                        isWaitingForStop = false
                                        Log.d("MultiFunctionFab", "延迟200毫秒后停止语音识别任务完成")
                                    } else {
                                        Log.d("MultiFunctionFab", "组件已不在活跃状态或已离开MainScreen，跳过停止操作")
                                    }
                                } else {
                                    isPressed = false
                                    isWaitingForStop = true
                                    Log.d("MultiFunctionFab", "按钮异常释放或取消，200毫秒后停止语音识别任务")
                                    delay(200)
                                    // 检查组件是否还在活跃状态且还在MainScreen
                                    if (isComponentActive && currentCoroutineContext().isActive && isInMainScreen) {
                                        voiceService.stopListening()
                                        isWaitingForStop = false
                                        Log.d("MultiFunctionFab", "延迟200毫秒后停止语音识别任务完成")
                                    } else {
                                        Log.d("MultiFunctionFab", "组件已不在活跃状态或已离开MainScreen，跳过停止操作")
                                    }
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

/**
 * AI响应悬浮窗组件 - 美化版本
 * 可点击其他区域关闭，包含进入退出动画
 */
@Composable
fun AiResponseOverlay(
    content: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 渐变背景遮罩
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.2f)
                    ),
                    radius = 800f
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // 悬浮窗主体卡片
        Card(
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }, // 阻止点击事件向上传播
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            // 内容区域
            Box {
                // 背景装饰
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = if (isSuccess) {
                                    listOf(
                                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                                        Color(0xFF81C784).copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFFF5722).copy(alpha = 0.1f),
                                        Color(0xFFFFAB91).copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                }
                            )
                        )
                )
                
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .widthIn(min = 280.dp, max = 360.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 顶部装饰线
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 图标容器
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (isSuccess) {
                                        listOf(
                                            Color(0xFF4CAF50).copy(alpha = 0.2f),
                                            Color(0xFF4CAF50).copy(alpha = 0.1f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFFF5722).copy(alpha = 0.2f),
                                            Color(0xFFFF5722).copy(alpha = 0.1f)
                                        )
                                    }
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 标题
                    Text(
                        text = if (isSuccess) "操作成功" else "操作失败",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF2E2E2E),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 响应内容
                    Text(
                        text = content.removePrefix("✅ ").removePrefix("❌ "),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 22.sp
                        ),
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // 关闭按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFFF5722),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "确定",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}