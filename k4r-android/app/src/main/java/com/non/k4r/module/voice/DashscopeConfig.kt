package com.non.k4r.module.voice

import android.content.Context
import com.non.k4r.module.settings.SettingsRepository
import kotlinx.coroutines.runBlocking

/**
 * 阿里云Dashscope语音识别服务配置
 * 
 * 使用说明：
 * 1. 请在阿里云控制台获取您的API Key
 * 2. 在应用设置页面配置API Key和模型ID
 * 3. 或者通过环境变量、配置文件等方式动态配置
 */
object DashscopeConfig {
    
    /**
     * 默认阿里云Dashscope API Key（后备配置）
     */
    private const val DEFAULT_API_KEY = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    
    /**
     * 默认语音识别模型（后备配置）
     */
    private const val DEFAULT_MODEL = "paraformer-realtime-v2"
    
    /**
     * WebSocket连接URL
     */
    const val WEBSOCKET_URL = "wss://dashscope.aliyuncs.com/api-ws/v1/inference"
    
    /**
     * 语音识别模型
     */
    const val MODEL = "paraformer-realtime-v2"
    
    /**
     * 音频格式配置
     */
    const val AUDIO_FORMAT = "pcm"
    const val SAMPLE_RATE = 16000
    const val AUDIO_ENCODE = "LINEAR16"
    
    /**
     * 从设置获取API Key，如果未配置则使用默认值
     */
    fun getApiKey(context: Context): String {
        return runBlocking {
            try {
                val settingsRepository = SettingsRepository(context)
                val apiKey = settingsRepository.getVoiceApiKey()
                if (apiKey.isNotBlank()) apiKey else DEFAULT_API_KEY
            } catch (e: Exception) {
                DEFAULT_API_KEY
            }
        }
    }
    
    /**
     * 从设置获取模型ID，如果未配置则使用默认值
     */
    fun getModel(context: Context): String {
        return runBlocking {
            try {
                val settingsRepository = SettingsRepository(context)
                val model = settingsRepository.getVoiceModel()
                if (model.isNotBlank()) model else DEFAULT_MODEL
            } catch (e: Exception) {
                DEFAULT_MODEL
            }
        }
    }
    
    /**
     * 检查API Key是否已配置
     */
    fun isApiKeyConfigured(context: Context): Boolean {
        val apiKey = getApiKey(context)
        return apiKey != "YOUR_DASHSCOPE_API_KEY" && apiKey.isNotBlank()
    }
    
    // 保留原有方法以兼容现有代码
    @Deprecated("请使用 getApiKey(context: Context) 方法")
    fun getApiKey(): String = DEFAULT_API_KEY
    
    @Deprecated("请使用 isApiKeyConfigured(context: Context) 方法")
    fun isApiKeyConfigured(): Boolean = DEFAULT_API_KEY.isNotBlank()
}