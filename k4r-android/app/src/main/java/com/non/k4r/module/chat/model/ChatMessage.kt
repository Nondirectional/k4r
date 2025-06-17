package com.non.k4r.module.chat.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 聊天消息数据模型
 */
@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val error: String? = null,
    // 工具调用相关字段
    val toolCalls: List<com.non.k4r.module.chat.model.ToolCall>? = null,
    val toolCallId: String? = null,
    val toolName: String? = null
)

/**
 * 消息角色枚举
 */
@Serializable
enum class MessageRole(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool");
    
    override fun toString(): String = value
}

/**
 * OpenAI兼容的消息格式
 */
@Serializable
data class OpenAIMessage(
    val role: String,
    val content: String
)

/**
 * 聊天请求数据模型
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    val max_tokens: Int? = null
)

/**
 * 聊天响应数据模型
 */
@Serializable
data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val index: Int,
    val message: OpenAIMessage,
    val finish_reason: String?
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

/**
 * 流式响应数据模型
 */
@Serializable
data class ChatStreamResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<StreamChoice>
)

@Serializable
data class StreamChoice(
    val index: Int,
    val delta: Delta,
    val finish_reason: String?
)

@Serializable
data class Delta(
    val role: String? = null,
    val content: String? = null
)