package com.non.k4r.module.chat.tool

import com.non.k4r.module.chat.model.Tool
import com.non.k4r.module.chat.model.ToolCallResult
import kotlinx.serialization.json.JsonElement

/**
 * 聊天工具接口
 * 开发者可以通过实现此接口来自定义工具
 */
interface ChatTool {
    /**
     * 工具名称，必须唯一
     */
    val name: String
    
    /**
     * 工具描述，用于大模型理解工具功能
     */
    val description: String
    
    /**
     * 工具参数定义（JSON Schema格式）
     */
    val parameters: JsonElement?
    
    /**
     * 执行工具调用
     * @param arguments 工具调用参数（JSON字符串）
     * @return 工具执行结果
     */
    suspend fun execute(arguments: String): ToolCallResult
    
    /**
     * 将工具转换为API格式
     */
    fun toTool(): Tool {
        return Tool(
            type = "function",
            function = com.non.k4r.module.chat.model.FunctionDefinition(
                name = name,
                description = description,
                parameters = parameters
            )
        )
    }
}