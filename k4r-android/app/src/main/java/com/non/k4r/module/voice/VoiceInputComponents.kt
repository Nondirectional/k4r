package com.non.k4r.module.voice

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun VoiceInputFab(
    onVoiceResult: (String) -> Unit,
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
    
    Log.d("VoiceInputFab", "初始权限状态: $hasPermission")
    
    val voiceService = remember { DashscopeVoiceService(context) }
    val isListening by voiceService.isListening.collectAsState()
    val recognitionResult by voiceService.recognitionResult.collectAsState()
    val partialResult by voiceService.partialResult.collectAsState()
    val error by voiceService.error.collectAsState()
    
    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("VoiceInputFab", "权限请求结果: $isGranted")
        hasPermission = isGranted
        if (isGranted) {
            Log.d("VoiceInputFab", "权限授予，开始录音")
            // 权限授予后自动开始录音
            voiceService.startListening()
        } else {
            Log.d("VoiceInputFab", "权限被拒绝")
        }
    }
    
    // 处理识别结果
    LaunchedEffect(recognitionResult) {
        recognitionResult?.let { result ->
            displayedResult = result
            showResult = true
            onVoiceResult(result)
            // 显示结果3秒后自动隐藏
            delay(3000)
            showResult = false
        }
    }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            voiceService.destroy()
        }
    }
    
    // 按住说话的按钮
    Box(
        modifier = modifier
            .size(56.dp)
            .background(
                color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        Log.d("VoiceInputFab", "按钮被按下，权限状态: $hasPermission")
                        if (hasPermission) {
                            isPressed = true
                            showResult = false // 开始录音时隐藏之前的结果
                            Log.d("VoiceInputFab", "开始录音")
                            voiceService.startListening()
                            
                            // 等待释放，使用awaitRelease()确保只有在真正释放时才执行
                            val released = tryAwaitRelease()
                            Log.d("VoiceInputFab", "按钮释放状态: $released")
                            
                            if (released) {
                                isPressed = false
                                Log.d("VoiceInputFab", "按钮正常释放，停止录音")
                                voiceService.stopListening()
                            } else {
                                isPressed = false
                                Log.d("VoiceInputFab", "按钮异常释放或取消，停止录音")
                                voiceService.stopListening()
                            }
                        } else {
                            Log.d("VoiceInputFab", "请求录音权限")
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = if (isListening) "正在录音" else "按住说话",
            tint = if (isListening) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        )
    }
    
    // 显示错误信息
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // 这里可以显示Toast或Snackbar
            // 暂时只在日志中显示
            Log.e("VoiceInput", "语音识别错误: $errorMessage")
        }
    }
}

@Composable
fun VoiceRecognitionOverlay(
    isListening: Boolean,
    partialResult: String?,
    finalResult: String?,
    showResult: Boolean,
    modifier: Modifier = Modifier
) {
    if (isListening || showResult) {
        Card(
            modifier = modifier
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (isListening && partialResult?.isNotEmpty() == true) {
                    Text(
                        text = "实时识别: $partialResult",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (showResult && finalResult?.isNotEmpty() == true) {
                    if (isListening && partialResult?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "识别结果: $finalResult",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isListening && partialResult?.isEmpty() != false && !showResult) {
                    Text(
                        text = "正在听取语音...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}