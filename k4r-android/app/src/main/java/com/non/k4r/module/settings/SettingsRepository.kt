package com.non.k4r.module.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "k4r_settings"
        private const val CHAT_API_KEY = "chat_api_key"
        private const val CHAT_MODEL = "chat_model"
        private const val VOICE_API_KEY = "voice_api_key"
        private const val VOICE_MODEL = "voice_model"
        private const val BACKEND_HOST = "backend_host"
        private const val BACKEND_PORT = "backend_port"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // 大模型对话配置
    fun saveChatApiKey(apiKey: String) {
        sharedPreferences.edit().putString(CHAT_API_KEY, apiKey).apply()
    }
    
    fun getChatApiKey(): String {
        return sharedPreferences.getString(CHAT_API_KEY, "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") ?: "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    }
    
    fun saveChatModel(model: String) {
        sharedPreferences.edit().putString(CHAT_MODEL, model).apply()
    }
    
    fun getChatModel(): String {
        return sharedPreferences.getString(CHAT_MODEL, "qwen-turbo") ?: "qwen-turbo"
    }
    
    // 语音识别配置
    fun saveVoiceApiKey(apiKey: String) {
        sharedPreferences.edit().putString(VOICE_API_KEY, apiKey).apply()
    }
    
    fun getVoiceApiKey(): String {
        return sharedPreferences.getString(VOICE_API_KEY, "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") ?: "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    }
    
    fun saveVoiceModel(model: String) {
        sharedPreferences.edit().putString(VOICE_MODEL, model).apply()
    }
    
    fun getVoiceModel(): String {
        return sharedPreferences.getString(VOICE_MODEL, "paraformer-realtime-v2") ?: "paraformer-realtime-v2"
    }

    // 后端服务配置
    fun saveBackendHost(host: String) {
        sharedPreferences.edit().putString(BACKEND_HOST, host).apply()
    }

    fun getBackendHost(): String {
        return sharedPreferences.getString(BACKEND_HOST, "10.0.2.2") ?: "10.0.2.2"
    }

    fun saveBackendPort(port: String) {
        sharedPreferences.edit().putString(BACKEND_PORT, port).apply()
    }

    fun getBackendPort(): String {
        return sharedPreferences.getString(BACKEND_PORT, "8000") ?: "8000"
    }
} 