package com.non.k4r.module.chat

import android.content.Context
import com.non.k4r.module.settings.SettingsRepository
import kotlinx.coroutines.runBlocking

/**
 * 阿里云百炼大模型对话服务配置
 * 
 * 参考文档：https://help.aliyun.com/zh/model-studio/getting-started/first-api-call-to-qwen
 * OpenAI兼容接口：https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope
 */
object DashscopeChatConfig {
    
    /**
     * 默认阿里云百炼API Key（后备配置）
     */
    private const val DEFAULT_API_KEY = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    
    /**
     * 默认模型（后备配置）
     */
    const val DEFAULT_MODEL = "qwen-plus"
    
    /**
     * OpenAI兼容接口基础URL
     */
    const val BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/"
    
    /**
     * 聊天完成接口URL
     */
    const val CHAT_COMPLETIONS_URL = "${BASE_URL}chat/completions"
    
    /**
     * 请求超时时间（秒）
     */
    const val TIMEOUT_SECONDS = 30L
    
    /**
     * 最大重试次数
     */
    const val MAX_RETRY_COUNT = 3
    
    /**
     * 从设置获取API Key，如果未配置则使用默认值
     */
    fun getApiKey(context: Context): String {
        return runBlocking {
            try {
                val settingsRepository = SettingsRepository(context)
                val apiKey = settingsRepository.getChatApiKey()
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
                val model = settingsRepository.getChatModel()
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
    
    /**
     * 获取Authorization头
     */
    fun getAuthorizationHeader(context: Context): String {
        return "Bearer ${getApiKey(context)}"
    }
    
    // 保留原有方法以兼容现有代码
    @Deprecated("请使用 getApiKey(context: Context) 方法")
    fun getApiKey(): String = DEFAULT_API_KEY
    
    @Deprecated("请使用 isApiKeyConfigured(context: Context) 方法") 
    fun isApiKeyConfigured(): Boolean = DEFAULT_API_KEY.isNotBlank()
    
    @Deprecated("请使用 getAuthorizationHeader(context: Context) 方法")
    fun getAuthorizationHeader(): String = "Bearer $DEFAULT_API_KEY"
}