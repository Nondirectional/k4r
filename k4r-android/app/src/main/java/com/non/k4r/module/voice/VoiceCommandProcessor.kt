package com.non.k4r.module.voice

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

data class VoiceCommand(
    val action: VoiceAction,
    val parameters: Map<String, Any>
)

enum class VoiceAction {
    ADD_EXPENDITURE,
    ADD_TODO,
    UNKNOWN
}

class VoiceCommandProcessor {
    
    // 开支相关的关键词
    private val expenditureKeywords = listOf(
        "花了", "花费", "买了", "购买", "支付", "付了", "消费", "开支", "用了", "花掉"
    )
    
    // 待办相关的关键词
    private val todoKeywords = listOf(
        "提醒", "记住", "待办", "任务", "要做", "别忘了", "安排", "计划"
    )
    
    // 金额匹配正则
    private val amountPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*[元块钱]")
    
    // 时间匹配正则
    private val timePatterns = mapOf(
        "今天" to 0,
        "明天" to 1,
        "后天" to 2,
        "大后天" to 3
    )
    
    fun processVoiceInput(text: String): VoiceCommand {
        val cleanText = text.trim()
        
        return when {
            containsExpenditureKeywords(cleanText) -> parseExpenditureCommand(cleanText)
            containsTodoKeywords(cleanText) -> parseTodoCommand(cleanText)
            else -> VoiceCommand(VoiceAction.UNKNOWN, emptyMap())
        }
    }
    
    private fun containsExpenditureKeywords(text: String): Boolean {
        return expenditureKeywords.any { text.contains(it) }
    }
    
    private fun containsTodoKeywords(text: String): Boolean {
        return todoKeywords.any { text.contains(it) }
    }
    
    private fun parseExpenditureCommand(text: String): VoiceCommand {
        val parameters = mutableMapOf<String, Any>()
        
        // 提取金额
        val amountMatcher = amountPattern.matcher(text)
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)
            try {
                val amount = amountStr.toDouble()
                parameters["amount"] = (amount * 100).toInt() // 转换为分
            } catch (e: NumberFormatException) {
                // 忽略无效金额
            }
        }
        
        // 提取描述（移除金额和关键词后的内容）
        var description = text
        if (amountMatcher.find()) {
            description = description.replace(amountMatcher.group(), "")
        }
        
        // 移除开支关键词
        expenditureKeywords.forEach { keyword ->
            description = description.replace(keyword, "")
        }
        
        // 清理描述
        description = description.trim().replace(Regex("\\s+"), " ")
        if (description.isNotEmpty()) {
            parameters["introduction"] = description
        }
        
        // 设置当前日期
        parameters["date"] = LocalDate.now()
        
        return VoiceCommand(VoiceAction.ADD_EXPENDITURE, parameters)
    }
    
    private fun parseTodoCommand(text: String): VoiceCommand {
        val parameters = mutableMapOf<String, Any>()
        
        // 提取时间
        var dueDate: LocalDate? = null
        timePatterns.forEach { (keyword, daysOffset) ->
            if (text.contains(keyword)) {
                dueDate = LocalDate.now().plusDays(daysOffset.toLong())
            }
        }
        
        if (dueDate != null) {
            parameters["dueDate"] = dueDate!!
        }
        
        // 提取描述（移除时间和关键词后的内容）
        var description = text
        
        // 移除时间关键词
        timePatterns.keys.forEach { keyword ->
            description = description.replace(keyword, "")
        }
        
        // 移除待办关键词
        todoKeywords.forEach { keyword ->
            description = description.replace(keyword, "")
        }
        
        // 清理描述
        description = description.trim().replace(Regex("\\s+"), " ")
        if (description.isNotEmpty()) {
            parameters["introduction"] = description
        }
        
        return VoiceCommand(VoiceAction.ADD_TODO, parameters)
    }
}