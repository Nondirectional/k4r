package com.non.k4r.module.chat.tool.builtin

import android.content.Context
import com.non.k4r.core.data.database.constant.ExpenditureType
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.Record
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
 * 添加支出/收入记录工具
 */
class AddExpenditureRecordTool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordDao: RecordDao,
    private val expenditureRecordDao: ExpenditureRecordDao
) : ChatTool {
    
    override val name = "add_expenditure_record"
    override val description = "添加支出或收入记录。当用户说要记录花费、开支、收入、购买了什么东西等时使用此工具"
    override val parameters: JsonElement = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            put("amount", buildJsonObject {
                put("type", "number")
                put("description", "金额（元），例如：10.5元应该输入10.5")
            })
            put("introduction", buildJsonObject {
                put("type", "string")
                put("description", "支出/收入的描述，例如：午餐、工资、买书等")
            })
            put("type", buildJsonObject {
                put("type", "string")
                put("enum", buildJsonArray {
                    add("expenditure")
                    add("income")
                })
                put("description", "记录类型：expenditure（支出）或income（收入）")
            })
            put("date", buildJsonObject {
                put("type", "string")
                put("description", "日期，格式为YYYY-MM-DD，如果用户没有指定则使用今天")
            })
            put("remark", buildJsonObject {
                put("type", "string")
                put("description", "备注信息（可选）")
            })
        })
        put("required", buildJsonArray {
            add("amount")
            add("introduction")
            add("type")
        })
    }
    
    override suspend fun execute(arguments: String): ToolCallResult {
        return try {
            val json = Json.parseToJsonElement(arguments).jsonObject
            
            // 解析参数
            val amountDouble = json["amount"]?.jsonPrimitive?.double 
                ?: return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "缺少amount参数"
                )
            
            val amount = (amountDouble * 100).toLong() // 转换为分
            
            val introduction = json["introduction"]?.jsonPrimitive?.content
                ?: return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "缺少introduction参数"
                )
            
            val typeStr = json["type"]?.jsonPrimitive?.content
                ?: return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "缺少type参数"
                )
            
            val expenditureType = when (typeStr.lowercase()) {
                "expenditure" -> ExpenditureType.Expenditure
                "income" -> ExpenditureType.Income
                else -> return ToolCallResult(
                    toolCallId = UUID.randomUUID().toString(),
                    result = "",
                    error = "type参数必须是expenditure或income"
                )
            }
            
            val dateStr = json["date"]?.jsonPrimitive?.content
            val expenditureDate = if (dateStr != null) {
                try {
                    LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e: Exception) {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
            
            val remark = json["remark"]?.jsonPrimitive?.content ?: "通过AI助手添加"
            
            // 在IO线程中执行数据库操作
            withContext(Dispatchers.IO) {
                // 创建主记录
                val record = Record(
                    recordType = RecordType.Expenditure,
                    recordTime = LocalDateTime.now()
                )
                val recordId = recordDao.insert(record)
                
                // 创建支出记录
                val expenditureRecord = ExpenditureRecord(
                    recordId = recordId,
                    amount = amount,
                    introduction = introduction,
                    remark = remark,
                    expenditureDate = expenditureDate,
                    expenditureType = expenditureType
                )
                expenditureRecordDao.insert(expenditureRecord)
            }
            
            val typeDesc = if (expenditureType == ExpenditureType.Expenditure) "支出" else "收入"
            val amountDesc = String.format("%.2f", amountDouble)
            
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "成功添加${typeDesc}记录：\n" +
                        "金额：¥${amountDesc}\n" +
                        "描述：${introduction}\n" +
                        "日期：${expenditureDate}\n" +
                        "备注：${remark}"
            )
            
        } catch (e: Exception) {
            ToolCallResult(
                toolCallId = UUID.randomUUID().toString(),
                result = "",
                error = "添加${if (arguments.contains("expenditure")) "支出" else "收入"}记录失败：${e.message}"
            )
        }
    }
} 