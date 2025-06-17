package com.non.k4r.module.chat.tool

import android.util.Log
import com.non.k4r.module.chat.model.Tool
import com.non.k4r.module.chat.model.ToolCallResult
import java.util.UUID

/**
 * 工具管理器
 * 负责注册、管理和执行工具调用
 */
class ToolManager {
    
    companion object {
        private const val TAG = "ToolManager"
    }
    
    private val tools = mutableMapOf<String, ChatTool>()
    
    /**
     * 注册工具
     */
    fun registerTool(tool: ChatTool) {
        tools[tool.name] = tool
        Log.d(TAG, "Registered tool: ${tool.name}")
    }
    
    /**
     * 注册多个工具
     */
    fun registerTools(vararg tools: ChatTool) {
        tools.forEach { registerTool(it) }
    }
    
    /**
     * 取消注册工具
     */
    fun unregisterTool(toolName: String) {
        tools.remove(toolName)
        Log.d(TAG, "Unregistered tool: $toolName")
    }
    
    /**
     * 获取所有已注册的工具
     */
    fun getRegisteredTools(): List<Tool> {
        return tools.values.map { it.toTool() }
    }
    
    /**
     * 检查工具是否已注册
     */
    fun isToolRegistered(toolName: String): Boolean {
        return tools.containsKey(toolName)
    }
    
    /**
     * 执行工具调用
     */
    suspend fun executeTool(toolName: String, arguments: String): ToolCallResult {
        val tool = tools[toolName]
        
        if (tool == null) {
            val errorMessage = "Tool '$toolName' not found"
            Log.e(TAG, errorMessage)
            return ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = errorMessage
            )
        }
        
        return try {
            Log.d(TAG, "Executing tool: $toolName with arguments: $arguments")
            val result = tool.execute(arguments)
            Log.d(TAG, "Tool execution result: ${result.result}")
            result
        } catch (e: Exception) {
            val errorMessage = "Tool execution failed: ${e.message}"
            Log.e(TAG, errorMessage, e)
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = errorMessage
            )
        }
    }
    
    /**
     * 获取工具列表的字符串表示（用于调试）
     */
    fun getToolsInfo(): String {
        return tools.values.joinToString("\n") { 
            "- ${it.name}: ${it.description}"
        }
    }
    
    /**
     * 清空所有工具
     */
    fun clearAllTools() {
        tools.clear()
        Log.d(TAG, "Cleared all tools")
    }
}