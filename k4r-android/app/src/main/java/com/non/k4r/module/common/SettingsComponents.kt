package com.non.k4r.module.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.non.k4r.module.auth.LoginViewModel
import com.non.k4r.module.settings.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    
    var chatApiKey by remember { mutableStateOf("") }
    var chatModel by remember { mutableStateOf("") }
    var voiceApiKey by remember { mutableStateOf("") }
    var voiceModel by remember { mutableStateOf("") }
    var backendHost by remember { mutableStateOf("") }
    var backendPort by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    // 加载当前设置
    LaunchedEffect(Unit) {
        chatApiKey = settingsRepository.getChatApiKey()
        chatModel = settingsRepository.getChatModel()
        voiceApiKey = settingsRepository.getVoiceApiKey()
        voiceModel = settingsRepository.getVoiceModel()
        backendHost = settingsRepository.getBackendHost()
        backendPort = settingsRepository.getBackendPort()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = "保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 后端服务配置
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "后端服务配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = backendHost,
                        onValueChange = { backendHost = it },
                        label = { Text("后端服务Host") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例如: 10.0.2.2") }
                    )

                    OutlinedTextField(
                        value = backendPort,
                        onValueChange = { backendPort = it },
                        label = { Text("后端服务端口") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例如: 8000") }
                    )
                }
            }
            
            // 大模型对话配置
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "大模型对话配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = chatApiKey,
                        onValueChange = { chatApiKey = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("请输入Dashscope API Key") }
                    )
                    
                    OutlinedTextField(
                        value = chatModel,
                        onValueChange = { chatModel = it },
                        label = { Text("模型ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例如: qwen-plus") }
                    )
                    
                    Text(
                        text = "可选模型: qwen-plus, qwen-max, qwen-turbo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 语音识别配置
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "语音识别配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = voiceApiKey,
                        onValueChange = { voiceApiKey = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("请输入Dashscope API Key") }
                    )
                    
                    OutlinedTextField(
                        value = voiceModel,
                        onValueChange = { voiceModel = it },
                        label = { Text("模型ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例如: paraformer-realtime-v2") }
                    )
                    
                    Text(
                        text = "可选模型: paraformer-realtime-v2",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // 保存确认对话框
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("保存设置") },
            text = { Text("确定要保存当前配置吗？保存后需要重启应用以使配置生效。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsRepository.saveChatApiKey(chatApiKey)
                        settingsRepository.saveChatModel(chatModel)
                        settingsRepository.saveVoiceApiKey(voiceApiKey)
                        settingsRepository.saveVoiceModel(voiceModel)
                        settingsRepository.saveBackendHost(backendHost)
                        settingsRepository.saveBackendPort(backendPort)
                        showSaveDialog = false
                        navController.navigateUp()
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
} 