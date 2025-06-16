package com.non.k4r.module.voice

/**
 * 阿里云Dashscope语音识别服务配置
 * 
 * 使用说明：
 * 1. 请在阿里云控制台获取您的API Key
 * 2. 将API_KEY常量替换为您的实际API Key
 * 3. 或者通过环境变量、配置文件等方式动态配置
 */
object DashscopeConfig {
    
    /**
     * 阿里云Dashscope API Key
     * 请替换为您的实际API Key
     * 
     * 获取方式：
     * 1. 登录阿里云控制台
     * 2. 进入模型服务灵积（DashScope）
     * 3. 在API-KEY管理页面创建或查看您的API Key
     */
    const val API_KEY = "sk-c216704d43124ab7b369dac6ed438262"
    
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
     * 检查API Key是否已配置
     */
    fun isApiKeyConfigured(): Boolean {
        return API_KEY != "YOUR_DASHSCOPE_API_KEY" && API_KEY.isNotBlank()
    }
    
    /**
     * 获取配置的API Key
     * 可以在这里添加从环境变量或其他配置源读取的逻辑
     */
    fun getApiKey(): String {
        // 优先从环境变量读取（如果需要）
        // val envApiKey = System.getenv("DASHSCOPE_API_KEY")
        // if (!envApiKey.isNullOrBlank()) {
        //     return envApiKey
        // }
        
        return API_KEY
    }
}