package com.non.k4r.module.expenditure.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.core.data.database.constant.ExpenditureType
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.core.data.database.model.Record
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.TodoRecord
import com.non.k4r.module.chat.ChatServiceManager
import com.non.k4r.module.chat.DashscopeChatService
import com.non.k4r.module.common.model.RecordMainScreenVO
import com.non.k4r.module.expenditure.model.ExpenditureRecordMainScreenVO
import com.non.k4r.module.todo.TodoRecordMainScreenVO
import com.non.k4r.module.voice.VoiceAction
import com.non.k4r.module.voice.VoiceCommand
import com.non.k4r.module.voice.VoiceCommandProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val recordDao: RecordDao,
    private val expenditureRecordDao: ExpenditureRecordDao,
    private val todoRecordDao: TodoRecordDao,
    private val chatServiceManager: ChatServiceManager
) : ViewModel() {
    private val TAG: String = "MainScreenViewModel"

    private val _uiState =
        MutableStateFlow<MainScreenUiState>(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState
    
    // 语音处理结果
    private val _voiceProcessResult = MutableStateFlow<VoiceProcessResult?>(null)
    val voiceProcessResult: StateFlow<VoiceProcessResult?> = _voiceProcessResult.asStateFlow()
    
    private val voiceCommandProcessor = VoiceCommandProcessor()

    init {
        reloadRecords()
    }

    fun reloadRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            val records: List<Record> = recordDao.pageRecords(20, 0, null)
            var vos: MutableList<RecordMainScreenVO> = mutableListOf()

            if (records.isNotEmpty()) {
                records.forEach { item ->
                    var vo: RecordMainScreenVO? = null
                    when (item.recordType) {
                        RecordType.Expenditure -> {
                            var withTagsByRecordId =
                                expenditureRecordDao.getWithTagsByRecordId(item.id)
                            withTagsByRecordId?.let {
                                var voImpl = ExpenditureRecordMainScreenVO()
                                voImpl.id = item.id
                                voImpl.type = item.recordType
                                voImpl.recordTime = item.recordTime
                                voImpl.expenditureWithTags = it
                                vo = voImpl
                            }

                        }

                        RecordType.Todo -> {
                            var todoRecord = todoRecordDao.getByRecordId(item.id)
                            if (todoRecord != null) {
                                var voImpl = TodoRecordMainScreenVO()
                                voImpl.id = item.id
                                voImpl.type = item.recordType
                                voImpl.recordTime = item.recordTime
                                voImpl.todoRecord = todoRecord
                                vo = voImpl
                            }

                        }
                    }
                    if (vo != null) {
                        vos.add(vo!!)
                    }
                }
            }
            var map = vos.map { it.id!! to it }.toMap()
            _uiState.value =
                _uiState.value.copy(records = vos, map = map)

        }
    }

    fun toggleTodoRecord(recordId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            var targetRecord: RecordMainScreenVO? = _uiState.value.map[recordId]
            if (targetRecord != null) {
                var vO = targetRecord as TodoRecordMainScreenVO
                
                // 创建新的TodoRecord实例以确保状态变化被检测到
                val updatedTodoRecord = vO.todoRecord!!.copy(
                    isCompleted = !vO.todoRecord!!.isCompleted
                )
                
                // 更新数据库
                todoRecordDao.update(updatedTodoRecord)
                
                // 在主线程更新UI状态
                viewModelScope.launch(Dispatchers.Main) {
                    // 创建新的TodoRecordMainScreenVO实例
                    val newVO = TodoRecordMainScreenVO().apply {
                        id = vO.id
                        type = vO.type
                        recordTime = vO.recordTime
                        recomposeFlag = !vO.recomposeFlag
                        todoRecord = updatedTodoRecord
                    }
                    
                    val updatedRecords = _uiState.value.records.map { record ->
                        if (record.id == recordId) newVO else record
                    }
                    val updatedMap = updatedRecords.associateBy { it.id!! }
                    _uiState.value = _uiState.value.copy(records = updatedRecords, map = updatedMap)
                }
            }
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val targetRecord: RecordMainScreenVO? = _uiState.value.map[recordId]
            if (targetRecord != null) {
                when (targetRecord.type) {
                    RecordType.Expenditure -> {
                        val expenditureVO = targetRecord as ExpenditureRecordMainScreenVO
                        expenditureVO.expenditureWithTags?.expenditureRecord?.let { expenditureRecord ->
                            expenditureRecordDao.delete(expenditureRecord)
                        }
                    }
                    RecordType.Todo -> {
                        val todoVO = targetRecord as TodoRecordMainScreenVO
                        todoVO.todoRecord?.let { todoRecord ->
                            todoRecordDao.delete(todoRecord)
                        }
                    }
                    else -> return@launch
                }
                
                // 删除主记录
                val record = Record(
                    id = targetRecord.id!!,
                    recordType = targetRecord.type!!,
                    recordTime = targetRecord.recordTime!!
                )
                recordDao.delete(record)
                
                // 在主线程更新UI状态
                viewModelScope.launch(Dispatchers.Main) {
                    val updatedRecords = _uiState.value.records.filter { it.id != recordId }
                    val updatedMap = updatedRecords.associateBy { it.id!! }
                    _uiState.value = _uiState.value.copy(records = updatedRecords, map = updatedMap)
                }
            }
        }
    }
    
    fun processVoiceCommand(voiceText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 使用AI大模型来处理语音输入
                val chatService = chatServiceManager.getChatService()
                
                // 添加系统提示，指导AI理解用户意图并调用相应工具
                val systemPrompt = """
                你是一个智能助手，专门帮助用户管理记录。
                用户会通过语音输入来添加支出、收入或待办事项。
                请根据用户的输入，调用合适的工具来完成操作。
                
                **当前日期信息：**
                今天是：${LocalDate.now()}
                
                **可用工具说明：**
                1. add_expenditure_record：添加支出或收入记录
                2. add_todo_record：添加待办事项记录
                
                工具执行成功后，请用简洁的中文总结操作结果。
                """.trimIndent()
                
                chatService.addSystemMessage(systemPrompt)
                chatService.sendMessage(voiceText)
                
                // 监听聊天服务状态，当处理完成后刷新数据
                monitorChatServiceAndRefresh(chatService)
                
            } catch (e: Exception) {
                android.util.Log.e(TAG, "处理语音指令失败", e)
                // 显示错误提示
                viewModelScope.launch(Dispatchers.Main) {
                    _voiceProcessResult.value = VoiceProcessResult.Error("语音处理失败，尝试使用简单模式处理")
                }
                
                // 如果AI处理失败，回退到原来的简单处理器
                val command = voiceCommandProcessor.processVoiceInput(voiceText)
                
                when (command.action) {
                    VoiceAction.ADD_EXPENDITURE -> {
                        addExpenditureFromVoice(command)
                        // 显示成功提示
                        viewModelScope.launch(Dispatchers.Main) {
                            _voiceProcessResult.value = VoiceProcessResult.Success("已通过简单模式添加支出记录")
                        }
                    }
                    VoiceAction.ADD_TODO -> {
                        addTodoFromVoice(command)
                        // 显示成功提示
                        viewModelScope.launch(Dispatchers.Main) {
                            _voiceProcessResult.value = VoiceProcessResult.Success("已通过简单模式添加待办事项")
                        }
                    }
                    VoiceAction.UNKNOWN -> {
                        // 显示无法识别的提示
                        viewModelScope.launch(Dispatchers.Main) {
                            _voiceProcessResult.value = VoiceProcessResult.Error("无法识别语音指令，请尝试更清晰的表达")
                        }
                    }
                }
            }
        }
    }
    
    private fun monitorChatServiceAndRefresh(chatService: DashscopeChatService) {
        // 启动协程监听聊天服务状态
        viewModelScope.launch {
            chatService.isSending.collect { isSending ->
                if (!isSending) {
                    // 当AI处理完成后，延迟一下再刷新数据确保数据库操作完成
                    kotlinx.coroutines.delay(500)
                    reloadRecords()
                    
                    // 获取最新的消息，查看是否有成功提示
                    val messages = chatService.messages.value
                    val lastMessage = messages.lastOrNull()
                    if (lastMessage != null && lastMessage.role == com.non.k4r.module.chat.model.MessageRole.ASSISTANT) {
                        // 在主线程显示结果提示
                        viewModelScope.launch(Dispatchers.Main) {
                            if (lastMessage.error != null) {
                                _voiceProcessResult.value = VoiceProcessResult.Error(lastMessage.error)
                            } else {
                                _voiceProcessResult.value = VoiceProcessResult.Success(lastMessage.content)
                            }
                        }
                    }
                    
                    // 停止监听这个特定的收集
                    return@collect
                }
            }
        }
        
        // 设置超时，避免无限等待
        viewModelScope.launch {
            kotlinx.coroutines.delay(30000) // 30秒超时
            // 超时后显示提示
            _voiceProcessResult.value = VoiceProcessResult.Error("语音处理超时，请重试")
        }
    }
    
    private suspend fun addExpenditureFromVoice(command: VoiceCommand) {
        val amount = command.parameters["amount"] as? Long ?: return
        val introduction = command.parameters["introduction"] as? String ?: "语音添加的开支"
        val date = command.parameters["date"] as? LocalDate ?: LocalDate.now()
        
        // 创建主记录
        val record = Record(
            recordType = RecordType.Expenditure,
            recordTime = LocalDateTime.now()
        )
        val recordId = recordDao.insert(record)
        
        // 创建开支记录
        val expenditureRecord = ExpenditureRecord(
            recordId = recordId,
            introduction = introduction,
            amount = amount,
            expenditureDate = date,
            remark = "通过语音添加",
            expenditureType = ExpenditureType.Expenditure
        )
        expenditureRecordDao.insert(expenditureRecord)
        
        // 刷新数据
        reloadRecords()
    }
    
    private suspend fun addTodoFromVoice(command: VoiceCommand) {
        val introduction = command.parameters["introduction"] as? String ?: "语音添加的待办"
        val dueDate = command.parameters["dueDate"] as? LocalDate
        
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
            remark = "通过语音添加",
            isCompleted = false,
            dueDate = dueDate
        )
        todoRecordDao.insert(todoRecord)
        
        // 刷新数据
        reloadRecords()
    }
    
    /**
     * 清除语音处理结果
     */
    fun clearVoiceProcessResult() {
        _voiceProcessResult.value = null
    }
}

data class MainScreenUiState(
    var records: List<RecordMainScreenVO> = emptyList(),
    var map: Map<Long, RecordMainScreenVO> = emptyMap()
)

/**
 * 语音处理结果
 */
sealed class VoiceProcessResult {
    data class Success(val message: String) : VoiceProcessResult()
    data class Error(val message: String) : VoiceProcessResult()
}