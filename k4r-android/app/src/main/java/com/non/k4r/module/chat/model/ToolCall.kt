package com.non.k4r.module.chat.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * 工具定义
 * 基于阿里云通义千问Function Calling API
 * 参考：https://help.aliyun.com/zh/model-studio/qwen-function-calling
 */
@Serializable
data class Tool(
    val type: String = "function",
    val function: FunctionDefinition
)

/**
 * 自定义序列化器，用于处理 FunctionDefinition 的 parameters 字段
 */
object FunctionDefinitionSerializer : KSerializer<FunctionDefinition> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionDefinition") {
        element<String>("name")
        element<String>("description")
        element<JsonElement>("parameters")
    }

    override fun serialize(encoder: Encoder, value: FunctionDefinition) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeStringElement(descriptor, 0, value.name)
        compositeOutput.encodeStringElement(descriptor, 1, value.description)
        
        // 当 parameters 为 null 时，提供默认的空对象结构
        val parametersValue = value.parameters ?: buildJsonObject {
            put("type", "object")
            put("properties", buildJsonObject {})
        }
        compositeOutput.encodeSerializableElement(descriptor, 2, JsonElement.serializer(), parametersValue)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): FunctionDefinition {
        val compositeInput = decoder.beginStructure(descriptor)
        var name = ""
        var description = ""
        var parameters: JsonElement? = null
        
        while (true) {
            when (val index = compositeInput.decodeElementIndex(descriptor)) {
                0 -> name = compositeInput.decodeStringElement(descriptor, 0)
                1 -> description = compositeInput.decodeStringElement(descriptor, 1)
                2 -> parameters = compositeInput.decodeSerializableElement(descriptor, 2, JsonElement.serializer())
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        compositeInput.endStructure(descriptor)
        
        return FunctionDefinition(name, description, parameters)
    }
}

/**
 * 函数定义
 */
@Serializable(with = FunctionDefinitionSerializer::class)
data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: JsonElement? = null
) {
    // 手动序列化parameters字段，当为null时提供默认的空对象结构
    fun toJsonString(): String {
        val escapedName = name.replace("\\", "\\\\").replace("\"", "\\\"")
        val escapedDescription = description.replace("\\", "\\\\").replace("\"", "\\\"")
        val paramsStr = if (parameters != null) {
            ",\"parameters\":$parameters"
        } else {
            ",\"parameters\":{\"type\":\"object\",\"properties\":{}}"
        }
        return "{\"name\":\"$escapedName\",\"description\":\"$escapedDescription\"$paramsStr}"
    }
}

/**
 * 工具调用请求
 */
@Serializable
data class ToolCall(
    val id: String? = null,
    val type: String = "function",
    val function: FunctionCall
)

/**
 * 函数调用
 */
@Serializable
data class FunctionCall(
    val name: String,
    val arguments: String
)

/**
 * 工具调用结果
 */
@Serializable
data class ToolCallResult(
    val toolCallId: String,
    val result: String,
    val error: String? = null
)

/**
 * 扩展OpenAIMessage以支持工具调用
 */
@Serializable
data class OpenAIMessageWithTools(
    val role: String,
    val content: String? = null,
    val tool_calls: List<ToolCall>? = null,
    val tool_call_id: String? = null,
    val name: String? = null
)

/**
 * 扩展ChatRequest以支持工具调用
 */
@Serializable
data class ChatRequestWithTools(
    val model: String,
    val messages: List<OpenAIMessageWithTools>,
    val tools: List<Tool>? = null,
    val tool_choice: String? = null, // "auto", "none", 或具体工具名称
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    val max_tokens: Int? = null
)

/**
 * 扩展Choice以支持工具调用
 */
@Serializable
data class ChoiceWithTools(
    val index: Int,
    val message: OpenAIMessageWithTools,
    val finish_reason: String?
)

/**
 * 扩展ChatResponse以支持工具调用
 */
@Serializable
data class ChatResponseWithTools(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<ChoiceWithTools>,
    val usage: Usage? = null
)