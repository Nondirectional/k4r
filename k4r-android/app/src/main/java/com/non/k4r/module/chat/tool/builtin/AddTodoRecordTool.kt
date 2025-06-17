package com.non.k4r.module.chat.tool.builtin

import android.content.Context
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.core.data.database.model.Record
import com.non.k4r.core.data.database.model.TodoRecord
import com.non.k4r.module.chat.model.ToolCallResult
import com.non.k4r.module.chat.tool.ChatTool
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

/**
 * 添加待办记录工具
 */
class AddTodoRecordTool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordDao: RecordDao,
    private val todoRecordDao: TodoRecordDao
) : ChatTool {
    
    override val name = "add_todo_record"
    override val description = "添加待办事项记录。当用户说要添加任务、提醒、待办事项、需要做什么事情等时使用此工具"
    override val parameters: JsonElement = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            put("introduction", buildJsonObject {
                put("type", "string")
                put("description", "待办事项的描述，例如：买菜、开会、写报告等")
            })
            put("dueDate", buildJsonObject {
                put("type", "string")
                put("description", "截止日期，格式为YYYY-MM-DD，如果用户没有指定则为空")
            })
            put("remark", buildJsonObject {
                put("type", "string")
                put("description", "备注信息（可选）")
            })
        })
        put("required", buildJsonArray {
            add("introduction")
        })
    }
    
    override suspend fun execute(arguments: String): ToolCallResult {
        return try {
            val json = Json.parseToJsonElement(arguments).jsonObject
            
            // 解析参数
            val introduction = json["introduction"]?.jsonPrimitive?.content
                ?: return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "缺少introduction参数"
                )
            
            val dueDateStr = json["dueDate"]?.jsonPrimitive?.content
            val dueDate = if (dueDateStr != null && dueDateStr.isNotBlank()) {
                try {
                    LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e: Exception) {
                    null // 如果解析失败，设为null
                }
            } else {
                null
            }
            
            val remark = json["remark"]?.jsonPrimitive?.content ?: "通过AI助手添加"
            
            // 在IO线程中执行数据库操作
            withContext(Dispatchers.IO) {
                // 创建主记录
                val record = Record(
                    recordType = RecordType.Todo,
                    recordTime = LocalDateTime.now()
                )
                val recordId = recordDao.insert(record)
                
                // 创建待办记录
                val todoRecord = TodoRecord(
                    recordId = recordId,
                    introduction = introduction,
                    remark = remark,
                    dueDate = dueDate,
                    isCompleted = false
                )
                todoRecordDao.insert(todoRecord)
            }
            
            val dueDateDesc = if (dueDate != null) "\n截止日期：$dueDate" else ""
            
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "成功添加待办事项：\n" +
                        "内容：${introduction}\n" +
                        "备注：${remark}${dueDateDesc}"
            )
            
        } catch (e: Exception) {
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = "添加待办事项失败：${e.message}"
            )
        }
    }
} 