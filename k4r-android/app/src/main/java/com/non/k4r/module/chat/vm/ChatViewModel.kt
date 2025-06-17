package com.non.k4r.module.chat.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.module.chat.ChatServiceManager
import com.non.k4r.module.chat.DashscopeChatService
import com.non.k4r.module.chat.model.ChatMessage
import com.non.k4r.module.chat.tool.ChatTool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 聊天界面ViewModel
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatServiceManager: ChatServiceManager
) : ViewModel() {
    
    private val chatService: DashscopeChatService by lazy { chatServiceManager.getChatService() }
    
    // 聊天消息列表
    val messages: StateFlow<List<ChatMessage>> = chatService.messages
    
    // 是否正在发送消息
    val isSending: StateFlow<Boolean> = chatService.isSending
    
    // 错误信息
    val error: StateFlow<String?> = chatService.error
    
    init {
        // 添加助手开始消息
        addAssistantMessage("你好，我是 K4R 智能助手，请问我可以帮你做些什么？")
    }
    
    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        viewModelScope.launch {
            chatService.sendMessage(content)
        }
    }
    
    /**
     * 清空聊天记录
     */
    fun clearMessages() {
        chatService.clearMessages()
        // 重新添加助手开始消息
        addAssistantMessage("你好，我是 K4R 智能助手，请问我可以帮你做些什么？")
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        chatService.clearError()
    }
    
    /**
     * 添加系统消息
     */
    private fun addSystemMessage(content: String) {
        chatService.addSystemMessage(content)
    }

    /**
     * 添加助手消息
     */
    private fun addAssistantMessage(content: String) {
        chatService.addAssistantMessage(content)
    }

    /**
     * 设置自定义系统提示
     */
    fun setSystemPrompt(prompt: String) {
        chatService.addSystemMessage(prompt)
    }
    
    // 工具管理功能
    
    /**
     * 注册工具
     */
    fun registerTool(tool: ChatTool) {
        chatService.registerTool(tool)
    }
    
    /**
     * 注销工具
     */
    fun unregisterTool(toolName: String) {
        chatService.unregisterTool(toolName)
    }
    
    /**
     * 获取已注册的工具列表
     */
    fun getRegisteredToolNames(): List<String> {
        return chatService.getRegisteredToolNames()
    }
    
    override fun onCleared() {
        super.onCleared()
        chatServiceManager.destroyChatService()
    }
}