package com.non.k4r.module.chat

import android.content.Context
import android.util.Log
import com.non.k4r.module.chat.model.*
import com.non.k4r.module.chat.tool.ToolManager
import com.non.k4r.module.chat.tool.builtin.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.UUID

/**
 * 阿里云百炼大模型对话服务
 *
 * 基于OpenAI兼容接口实现
 * 参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope
 */
class DashscopeChatService(private val context: Context) {

    companion object {
        private const val TAG = "DashscopeChatService"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    // 工具管理器
    private val toolManager = ToolManager()

    init {
        // 注册内置工具
        toolManager.registerTools(
            GetCurrentTimeTool(),
            CalculatorTool(),
            RandomNumberTool()
        )
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(DashscopeChatConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(DashscopeChatConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(DashscopeChatConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // 聊天消息列表
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // 当前是否正在发送消息
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    // 错误信息
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 注册自定义工具
     */
    fun registerTool(tool: com.non.k4r.module.chat.tool.ChatTool) {
        toolManager.registerTool(tool)
    }

    /**
     * 取消注册工具
     */
    fun unregisterTool(toolName: String) {
        toolManager.unregisterTool(toolName)
    }

    fun getRegisteredToolNames(): List<String> {
        return toolManager.getRegisteredTools().map { it.function.name }
    }

    /**
     * 获取已注册的工具信息
     */
    fun getRegisteredToolsInfo(): String {
        return toolManager.getToolsInfo()
    }

    /**
     * 发送消息
     */
    suspend fun sendMessage(content: String, model: String = DashscopeChatConfig.DEFAULT_MODEL) {
        if (_isSending.value) {
            Log.w(TAG, "Already sending a message, ignoring new request")
            return
        }

        if (!DashscopeChatConfig.isApiKeyConfigured(context)) {
            _error.value = "API Key未配置，请在设置中配置您的API Key"
            return
        }

        try {
            _isSending.value = true
            _error.value = null

            // 添加用户消息
            val userMessage = ChatMessage(
                role = MessageRole.USER,
                content = content
            )
            _messages.value = _messages.value + userMessage

            // 添加加载中的助手消息
            val loadingMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "",
                isLoading = true
            )
            _messages.value = _messages.value + loadingMessage

            // 准备请求数据
            val openAIMessages = _messages.value
                .filter { !it.isLoading && it.error == null }
                .map { message ->
                    when (message.role) {
                        MessageRole.TOOL -> OpenAIMessageWithTools(
                            role = "tool",
                            content = message.content,
                            tool_call_id = message.toolCallId,
                            name = message.toolName
                        )

                        else -> OpenAIMessageWithTools(
                            role = message.role.value,
                            content = message.content,
                            tool_calls = message.toolCalls
                        )
                    }
                }

            val registeredTools = toolManager.getRegisteredTools()

            // 手动构建请求JSON以避免序列化问题
            val requestJson = buildString {
                append("{")
                append("\"model\":\"$model\",")
                append("\"messages\":")
                append(
                    json.encodeToString(
                        kotlinx.serialization.builtins.ListSerializer(
                            OpenAIMessageWithTools.serializer()
                        ), openAIMessages
                    )
                )
                if (registeredTools.isNotEmpty()) {
                    append(",\"tools\":[")
                    registeredTools.forEachIndexed { index, tool ->
                        if (index > 0) append(",")
                        append("{\"type\":\"function\",\"function\":")
                        append(tool.function.toJsonString())
                        append("}")
                    }
                    append("],\"tool_choice\":\"auto\"")
                }
                append(",\"stream\":false,\"temperature\":0.7}")
            }

            val requestBody = requestJson.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url(DashscopeChatConfig.CHAT_COMPLETIONS_URL)
                .addHeader("Authorization", DashscopeChatConfig.getAuthorizationHeader(context))
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            Log.d(TAG, "Sending request to: ${DashscopeChatConfig.CHAT_COMPLETIONS_URL}")
            Log.d(TAG, "Request body: $requestJson")

            // 发送请求
            client.newCall(httpRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Request failed", e)
                    handleError("网络请求失败: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            val errorBody = response.body?.string() ?: "Unknown error"
                            Log.e(
                                TAG,
                                "Request failed with code: ${response.code}, body: $errorBody"
                            )
                            handleError("请求失败 (${response.code}): $errorBody")
                            return
                        }

                        val responseBody = response.body?.string()
                        if (responseBody == null) {
                            handleError("响应体为空")
                            return
                        }

                        Log.d(TAG, "Response: $responseBody")

                        try {
                            val chatResponse = json.decodeFromString(
                                ChatResponseWithTools.serializer(),
                                responseBody
                            )
                            val choice = chatResponse.choices.firstOrNull()

                            if (choice != null) {
                                val message = choice.message

                                // 检查是否需要调用工具
                                if (message.tool_calls != null && message.tool_calls.isNotEmpty()) {
                                    // 处理工具调用
                                    CoroutineScope(Dispatchers.IO).launch {
                                        handleToolCalls(message, model)
                                    }
                                } else {
                                    // 普通响应
                                    val assistantContent = message.content ?: ""

                                    // 更新消息列表，移除加载中的消息，添加实际响应
                                    val updatedMessages = _messages.value.dropLast(1) + ChatMessage(
                                        role = MessageRole.ASSISTANT,
                                        content = assistantContent,
                                        toolCalls = message.tool_calls
                                    )
                                    _messages.value = updatedMessages
                                }
                            } else {
                                handleError("响应中没有有效的选择")
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse response", e)
                            handleError("解析响应失败: ${e.message}")
                        }
                    }
                    _isSending.value = false
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            handleError("发送消息失败: ${e.message}")
            _isSending.value = false
        }
    }

    /**
     * 处理工具调用
     */
    private suspend fun handleToolCalls(message: OpenAIMessageWithTools, model: String) {
        try {
            // 移除加载中的消息，添加助手的工具调用消息
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = message.content ?: "正在调用工具...",
                toolCalls = message.tool_calls
            )
            val messagesWithoutLoading = _messages.value.dropLast(1) + assistantMessage
            _messages.value = messagesWithoutLoading

            // 执行所有工具调用
            val toolResults = mutableListOf<ChatMessage>()

            message.tool_calls?.forEach { toolCall ->
                Log.d(
                    TAG,
                    "Executing tool: ${toolCall.function.name} with arguments: ${toolCall.function.arguments}"
                )

                val result = toolManager.executeTool(
                    toolCall.function.name,
                    toolCall.function.arguments
                )

                // 添加工具结果消息
                val toolMessage = ChatMessage(
                    role = MessageRole.TOOL,
                    content = if (result.error != null) {
                        "工具执行失败: ${result.error}"
                    } else {
                        result.result
                    },
                    toolCallId = toolCall.id ?: UUID.randomUUID().toString(),
                    toolName = toolCall.function.name
                )
                toolResults.add(toolMessage)
            }

            // 添加工具结果到消息列表
            _messages.value = _messages.value + toolResults

            // 添加新的加载消息，准备获取最终响应
            val finalLoadingMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "",
                isLoading = true
            )
            _messages.value = _messages.value + finalLoadingMessage

            // 重新调用API获取最终响应
            sendMessageInternal(model = model)

        } catch (e: Exception) {
            Log.e(TAG, "Error handling tool calls", e)
            handleError("工具调用失败: ${e.message}")
        }
    }

    /**
     * 内部发送消息方法（用于工具调用后的后续请求）
     */
    private suspend fun sendMessageInternal(model: String = DashscopeChatConfig.DEFAULT_MODEL) {
        try {
            // 准备请求数据
            val openAIMessages = _messages.value
                .filter { !it.isLoading && it.error == null }
                .map { message ->
                    when (message.role) {
                        MessageRole.TOOL -> OpenAIMessageWithTools(
                            role = "tool",
                            content = message.content,
                            tool_call_id = message.toolCallId,
                            name = message.toolName
                        )

                        else -> OpenAIMessageWithTools(
                            role = message.role.value,
                            content = message.content,
                            tool_calls = message.toolCalls
                        )
                    }
                }

            val registeredTools = toolManager.getRegisteredTools()

            // 手动构建请求JSON以确保工具格式正确
            val requestJson = buildString {
                append("{")
                append("\"model\":\"$model\",")
                append("\"messages\":")
                append(
                    json.encodeToString(
                        kotlinx.serialization.builtins.ListSerializer(
                            OpenAIMessageWithTools.serializer()
                        ), openAIMessages
                    )
                )
                if (registeredTools.isNotEmpty()) {
                    append(",\"tools\":[")
                    registeredTools.forEachIndexed { index, tool ->
                        if (index > 0) append(",")
                        append("{\"type\":\"function\",\"function\":")
                        append(tool.function.toJsonString())
                        append("}")
                    }
                    append("],\"tool_choice\":\"none\"")
                } else {
                    append(",\"tool_choice\":\"none\"")
                }
                append(",\"stream\":false,\"temperature\":0.7}")
            }

            val requestBody = requestJson.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url(DashscopeChatConfig.CHAT_COMPLETIONS_URL)
                .addHeader("Authorization", DashscopeChatConfig.getAuthorizationHeader(context))
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            Log.d(TAG, "API key is: ${DashscopeChatConfig.getAuthorizationHeader(context)}")
            Log.d(TAG, "Sending internal request to: ${DashscopeChatConfig.CHAT_COMPLETIONS_URL}")
            Log.d(TAG, "Internal request body: $requestJson")

            // 发送请求
            client.newCall(httpRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Request failed", e)
                    handleError("网络请求失败: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            response.request.body.toString()
                            val errorBody = response.body?.string() ?: "Unknown error"
                            Log.e(
                                TAG,
                                "Request failed with code: ${response.code}, body: $errorBody"
                            )
                            handleError("请求失败 (${response.code}): $errorBody")
                            return
                        }

                        val responseBody = response.body?.string()
                        if (responseBody == null) {
                            handleError("响应体为空")
                            return
                        }

                        Log.d(TAG, "Response: $responseBody")

                        try {
                            val chatResponse = json.decodeFromString(
                                ChatResponseWithTools.serializer(),
                                responseBody
                            )
                            val choice = chatResponse.choices.firstOrNull()

                            if (choice != null) {
                                val message = choice.message
                                val assistantContent = message.content ?: ""

                                // 更新消息列表，移除加载中的消息，添加最终响应
                                val updatedMessages = _messages.value.dropLast(1) + ChatMessage(
                                    role = MessageRole.ASSISTANT,
                                    content = assistantContent
                                )
                                _messages.value = updatedMessages
                            } else {
                                handleError("响应中没有有效的选择")
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse response", e)
                            handleError("解析响应失败: ${e.message}")
                        }
                    }
                    _isSending.value = false
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Error sending internal message", e)
            handleError("发送消息失败: ${e.message}")
            _isSending.value = false
        }
    }

    /**
     * 处理错误
     */
    private fun handleError(errorMessage: String) {
        _error.value = errorMessage

        // 移除加载中的消息，添加错误消息
        val updatedMessages = _messages.value.dropLast(1) + ChatMessage(
            role = MessageRole.ASSISTANT,
            content = "抱歉，发生了错误",
            error = errorMessage
        )
        _messages.value = updatedMessages
        _isSending.value = false
    }

    /**
     * 清空聊天记录
     */
    fun clearMessages() {
        _messages.value = emptyList()
        _error.value = null
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * 添加系统消息
     */
    fun addSystemMessage(content: String) {
        val systemMessage = ChatMessage(
            role = MessageRole.SYSTEM,
            content = content
        )
        _messages.value =
            listOf(systemMessage) + _messages.value.filter { it.role != MessageRole.SYSTEM }
    }

    /**
     * 添加助手消息
     */
    fun addAssistantMessage(content: String) {
        val assistantMessage = ChatMessage(
            role = MessageRole.ASSISTANT,
            content = content
        )
        _messages.value = listOf(assistantMessage) + _messages.value
    }

    /**
     * 销毁服务，释放资源
     */
    fun destroy() {
        // 取消所有进行中的请求
        client.dispatcher.cancelAll()
        clearMessages()
    }
}