package com.non.k4r.module.expenditure.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.core.data.database.model.Record
import com.non.k4r.module.common.model.RecordMainScreenVO
import com.non.k4r.module.expenditure.model.ExpenditureRecordMainScreenVO
import com.non.k4r.module.todo.TodoRecordMainScreenVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
}

data class MainScreenUiState(
    var records: List<RecordMainScreenVO> = emptyList(),
    var map: Map<Long, RecordMainScreenVO> = emptyMap()
)