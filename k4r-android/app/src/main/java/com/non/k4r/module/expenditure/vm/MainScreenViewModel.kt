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
            var vos: MutableList<RecordMainScreenVO?> = mutableListOf()

            if (records.isNotEmpty()) {
                records.forEach { item ->
                    var vo: RecordMainScreenVO? = null
                    when (item.recordType) {
                        RecordType.Expenditure -> {
                            expenditureRecordDao.getWithTagsByRecordId(item.id)?.let {
                                var voImpl = ExpenditureRecordMainScreenVO()
                                voImpl.id = item.id
                                voImpl.type = item.recordType
                                voImpl.recordTime = item.recordTime
                                voImpl.expenditureWithTags = it
                                vo = voImpl
                            }
                        }

                        RecordType.Todo -> {
                            var voImpl = TodoRecordMainScreenVO()
                            voImpl.id = item.id
                            voImpl.type = item.recordType
                            voImpl.recordTime = item.recordTime
                            voImpl.todoRecord = todoRecordDao.getByRecordId(item.id)
                            vo = voImpl
                        }
                    }
                    vos.add(vo)
                }
            }
            _uiState.value =
                _uiState.value.copy(records = vos)
        }
    }

}

data class MainScreenUiState(
    var records: List<RecordMainScreenVO?> = emptyList(),
)