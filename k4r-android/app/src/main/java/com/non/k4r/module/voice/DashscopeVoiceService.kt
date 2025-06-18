package com.non.k4r.module.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.UUID

class DashscopeVoiceService(private val context: Context) {

    companion object {
        private const val TAG = "DashscopeVoiceService"
    }

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _recognitionResult = MutableStateFlow<String?>(null)
    val recognitionResult: StateFlow<String?> = _recognitionResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _partialResult = MutableStateFlow<String?>(null)
    val partialResult: StateFlow<String?> = _partialResult

    private var isTaskStarted = false
    private var audioRecordingJob: Job? = null
    private var currentTaskId: String? = null

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket连接已建立")
            _isConnected.value = true
            _error.value = null
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "收到消息: $text")
            handleMessage(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "收到二进制消息")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket正在关闭: $code $reason")
            _isConnected.value = false
            _isListening.value = false
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket已关闭: $code $reason")
            _isConnected.value = false
            _isListening.value = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket连接失败", t)
            _error.value = "连接失败: ${t.message}"
            _isConnected.value = false
            _isListening.value = false
        }
    }

    /**
     * 初始化WebSocket连接，不开始语音任务
     */
    fun initializeConnection() {
        if (_isConnected.value) {
            Log.d(TAG, "WebSocket已连接，无需重复初始化")
            return
        }

        if (!DashscopeConfig.isApiKeyConfigured(context)) {
            _error.value = "请先在设置中配置阿里云Dashscope API Key"
            return
        }

        val apiKey = DashscopeConfig.getApiKey(context)
        val request = Request.Builder()
            .url(DashscopeConfig.WEBSOCKET_URL)
            .addHeader("Authorization", "bearer $apiKey")
            .addHeader("user-agent", "K4r-Android-App")
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)
        _error.value = null
    }

    /**
     * 开始语音识别任务（发送run-task指令）
     */
    fun startListening() {
        if (!_isConnected.value) {
            _error.value = "WebSocket未连接，请先初始化连接"
            return
        }

        if (isTaskStarted) {
            Log.d(TAG, "语音识别任务已在进行中")
            return
        }

        startTask()
        _recognitionResult.value = null
        _partialResult.value = null
    }

    private fun startTask() {
        currentTaskId = UUID.randomUUID().toString()
        val runTaskMessage = JSONObject().apply {
            put("header", JSONObject().apply {
                put("action", "run-task")
                put("task_id", currentTaskId)
                put("streaming", "duplex")
            })
            put("payload", JSONObject().apply {
                put("task_group", "audio")
                put("task", "asr")
                put("function", "recognition")
                put("model", DashscopeConfig.MODEL)
                put("input", JSONObject().apply {
                    put("format", DashscopeConfig.AUDIO_FORMAT)
                    put("sample_rate", DashscopeConfig.SAMPLE_RATE)
                    put("audio_encode", DashscopeConfig.AUDIO_ENCODE)
                })
                put("parameters", JSONObject().apply {
                    put("incremental_output", true)
                    put("enable_punctuation_prediction", true)
                    put("enable_inverse_text_normalization", true)
                    put("language_hints","zh")
                    put("disfluency_removal_enabled",true)
                })
            })
        }

        webSocket?.send(runTaskMessage.toString())
        Log.d(TAG, "发送run-task指令: $runTaskMessage")
    }

    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            val header = json.optJSONObject("header")
            val event = header?.optString("event")

            when (event) {
                "task-started" -> {
                    Log.d(TAG, "任务已开始")
                    isTaskStarted = true
                    _isListening.value = true
                    // 开始录音并发送音频数据
                    startAudioRecording()
                }

                "result-generated" -> {
                    val payload = json.optJSONObject("payload")
                    val output = payload?.optJSONObject("output")
                    val sentence = output?.optJSONObject("sentence")

                    if (sentence != null) {
                        val text = sentence.optString("text", "")
                        val isEnd = sentence.optBoolean("sentence_end", false)

                        if (isEnd && text.isNotEmpty()) {
                            _recognitionResult.value = text
                            Log.d(TAG, "识别结果: $text")
                        } else if (text.isNotEmpty()) {
                            _partialResult.value = text
                            Log.d(TAG, "部分结果: $text")
                        }
                    }
                }

                "task-finished" -> {
                    Log.d(TAG, "任务已完成")
                    _isListening.value = false
                    isTaskStarted = false
                    // 停止音频录制
                    audioRecordingJob?.cancel()
                    audioRecordingJob = null
                    // 清除部分识别结果
                    _partialResult.value = null
                }

                "task-failed" -> {
                    val payload = json.optJSONObject("payload")
                    val errorMessage = payload?.optString("message", "未知错误")
                    Log.e(TAG, "任务失败: $errorMessage")
                    _error.value = "识别失败: $errorMessage"
                    _isListening.value = false
                    isTaskStarted = false
                    // 停止音频录制
                    audioRecordingJob?.cancel()
                    audioRecordingJob = null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析消息失败", e)
            _error.value = "解析响应失败: ${e.message}"
        }
    }

    fun sendAudioData(audioData: ByteArray) {
        if (isTaskStarted && webSocket != null) {
            webSocket?.send(ByteString.of(*audioData))
        }
    }

    /**
     * 停止语音识别任务（发送finish-task指令），但保持WebSocket连接
     */
    fun stopListening() {
        if (isTaskStarted && currentTaskId != null) {
            val finishTaskMessage = JSONObject().apply {
                put("header", JSONObject().apply {
                    put("action", "finish-task")
                    put("task-id", currentTaskId)
                })
            }
            
            webSocket?.send(finishTaskMessage.toString())
            Log.d(TAG, "发送finish-task指令")
        }
        
        // 停止音频录制
        isTaskStarted = false
        audioRecordingJob?.cancel()
        audioRecordingJob = null
        _isListening.value = false
        // 清除部分识别结果
        _partialResult.value = null
    }

    /**
     * 关闭WebSocket连接并清理所有资源
     */
    fun destroy() {
        stopListening()
        webSocket?.close(1000, "正常关闭")
        webSocket = null
        currentTaskId = null
        isTaskStarted = false
        audioRecordingJob?.cancel()
        audioRecordingJob = null
        // 清除所有状态
        _isConnected.value = false
        _recognitionResult.value = null
        _partialResult.value = null
        _error.value = null
    }

    private fun startAudioRecording() {
        // 检查录音权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            _error.value = "录音权限未授予，请在设置中开启录音权限"
            return
        }
        
        audioRecordingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val sampleRate = DashscopeConfig.SAMPLE_RATE
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT
                
                val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )
                
                if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                    Log.e(TAG, "AudioRecord初始化失败")
                    _error.value = "音频录制初始化失败"
                    return@launch
                }
                
                audioRecord.startRecording()
                Log.d(TAG, "开始录音")
                
                val buffer = ByteArray(bufferSize)
                
                while (isTaskStarted && !Thread.currentThread().isInterrupted) {
                    val bytesRead = audioRecord.read(buffer, 0, buffer.size)
                    if (bytesRead > 0) {
                        // 发送音频数据到WebSocket
                        sendAudioData(buffer.copyOf(bytesRead))
                    }
                }
                
                audioRecord.stop()
                audioRecord.release()
                Log.d(TAG, "录音结束")
                
            } catch (e: Exception) {
                Log.e(TAG, "录音过程中发生错误", e)
                _error.value = "录音失败: ${e.message}"
            }
        }
    }
}