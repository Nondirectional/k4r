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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val recordDao: RecordDao,
    private val expenditureRecordDao: ExpenditureRecordDao,
    private val todoRecordDao: TodoRecordDao
) : ViewModel() {
    private val TAG: String = "MainScreenViewModel"

    private val _uiState =
        MutableStateFlow<MainScreenUiState>(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState
    
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
            val command = voiceCommandProcessor.processVoiceInput(voiceText)
            
            when (command.action) {
                VoiceAction.ADD_EXPENDITURE -> {
                    addExpenditureFromVoice(command)
                }
                VoiceAction.ADD_TODO -> {
                    addTodoFromVoice(command)
                }
                VoiceAction.UNKNOWN -> {
                    // 可以在这里添加错误处理或提示用户
                }
            }
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
}

data class MainScreenUiState(
    var records: List<RecordMainScreenVO> = emptyList(),
    var map: Map<Long, RecordMainScreenVO> = emptyMap()
)