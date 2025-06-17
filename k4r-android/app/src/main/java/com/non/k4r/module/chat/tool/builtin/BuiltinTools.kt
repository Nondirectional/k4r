package com.non.k4r.module.chat.tool.builtin

import com.non.k4r.module.chat.model.ToolCallResult
import com.non.k4r.module.chat.tool.ChatTool
import kotlinx.serialization.json.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

/**
 * 获取当前时间工具
 */
class GetCurrentTimeTool : ChatTool {
    override val name = "get_current_time"
    override val description = "获取当前的日期和时间，当用户询问现在几点或今天是几号时非常有用"
    override val parameters: JsonElement? = null
    
    override suspend fun execute(arguments: String): ToolCallResult {
        val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA)
        val currentTime = formatter.format(Date())
        
        return ToolCallResult(
            toolCallId = UUID.randomUUID().toString(),
            result = "当前时间是：$currentTime"
        )
    }
}

/**
 * 简单计算器工具
 */
class CalculatorTool : ChatTool {
    override val name = "calculator"
    override val description = "执行基本的数学计算，支持加减乘除、幂运算、开方等操作"
    override val parameters: JsonElement = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            put("expression", buildJsonObject {
                put("type", "string")
                put("description", "要计算的数学表达式，例如：2+3*4, sqrt(16), pow(2,3)")
            })
        })
        put("required", buildJsonArray {
            add("expression")
        })
    }
    
    override suspend fun execute(arguments: String): ToolCallResult {
        return try {
            val json = Json.parseToJsonElement(arguments).jsonObject
            val expression = json["expression"]?.jsonPrimitive?.content
                ?: return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "缺少expression参数"
                )
            
            val result = evaluateExpression(expression)
            
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "计算结果：$expression = $result"
            )
        } catch (e: Exception) {
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = "计算失败：${e.message}"
            )
        }
    }
    
    private fun evaluateExpression(expression: String): Double {
        // 简单的表达式计算器实现
        val cleanExpression = expression.replace(" ", "")
        
        // 处理特殊函数
        when {
            cleanExpression.startsWith("sqrt(") && cleanExpression.endsWith(")") -> {
                val number = cleanExpression.substring(5, cleanExpression.length - 1).toDouble()
                return sqrt(number)
            }
            cleanExpression.startsWith("pow(") && cleanExpression.endsWith(")") -> {
                val params = cleanExpression.substring(4, cleanExpression.length - 1).split(",")
                if (params.size == 2) {
                    val base = params[0].toDouble()
                    val exponent = params[1].toDouble()
                    return base.pow(exponent)
                }
            }
            cleanExpression.startsWith("sin(") && cleanExpression.endsWith(")") -> {
                val number = cleanExpression.substring(4, cleanExpression.length - 1).toDouble()
                return sin(number)
            }
            cleanExpression.startsWith("cos(") && cleanExpression.endsWith(")") -> {
                val number = cleanExpression.substring(4, cleanExpression.length - 1).toDouble()
                return cos(number)
            }
        }
        
        // 基本四则运算
        return evaluateBasicExpression(cleanExpression)
    }
    
    private fun evaluateBasicExpression(expression: String): Double {
        // 简单的四则运算解析器
        // 支持 +, -, *, /, 括号
        
        // 这里使用一个简化的实现，实际项目中可以使用更完善的表达式解析库
        val tokens = tokenize(expression)
        return parseExpression(tokens)
    }
    
    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        
        while (i < expression.length) {
            when (val char = expression[i]) {
                '+', '-', '*', '/', '(', ')' -> {
                    tokens.add(char.toString())
                    i++
                }
                in '0'..'9', '.' -> {
                    var number = ""
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        number += expression[i]
                        i++
                    }
                    tokens.add(number)
                }
                else -> i++
            }
        }
        
        return tokens
    }
    
    private fun parseExpression(tokens: List<String>): Double {
        // 简化的表达式解析，只处理基本的加减乘除
        if (tokens.isEmpty()) return 0.0
        
        var result = tokens[0].toDoubleOrNull() ?: 0.0
        var i = 1
        
        while (i < tokens.size - 1) {
            val operator = tokens[i]
            val operand = tokens[i + 1].toDoubleOrNull() ?: 0.0
            
            when (operator) {
                "+" -> result += operand
                "-" -> result -= operand
                "*" -> result *= operand
                "/" -> {
                    if (operand != 0.0) {
                        result /= operand
                    } else {
                        throw ArithmeticException("除零错误")
                    }
                }
            }
            
            i += 2
        }
        
        return result
    }
}

/**
 * 随机数生成工具
 */
class RandomNumberTool : ChatTool {
    override val name = "random_number"
    override val description = "生成指定范围内的随机数"
    override val parameters: JsonElement = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            put("min", buildJsonObject {
                put("type", "integer")
                put("description", "最小值（包含）")
            })
            put("max", buildJsonObject {
                put("type", "integer")
                put("description", "最大值（包含）")
            })
        })
        put("required", buildJsonArray {
            add("min")
            add("max")
        })
    }
    
    override suspend fun execute(arguments: String): ToolCallResult {
        return try {
            val json = Json.parseToJsonElement(arguments).jsonObject
            val min = json["min"]?.jsonPrimitive?.int ?: 1
            val max = json["max"]?.jsonPrimitive?.int ?: 100
            
            if (min > max) {
                return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "最小值不能大于最大值"
                )
            }
            
            val randomNumber = Random().nextInt(max - min + 1) + min
            
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "生成的随机数是：$randomNumber（范围：$min-$max）"
            )
        } catch (e: Exception) {
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = "生成随机数失败：${e.message}"
            )
        }
    }
}