package com.non.k4r.module.todo.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.core.data.database.model.Record
import com.non.k4r.core.data.database.model.TodoRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


private const val TAG: String = "TodoSubmitScreenViewModel"

@HiltViewModel
class TodoSubmitScreenViewModel @Inject constructor(
    private val recordDao: RecordDao,
    private val todoRecordDao: TodoRecordDao,
) : ViewModel() {


    private val _uiState =
        MutableStateFlow<TodoSubmitScreenUiState>(TodoSubmitScreenUiState())
    val uiState: StateFlow<TodoSubmitScreenUiState> = _uiState


    fun onIntroductionChanged(introduction: String) {
        _uiState.value = _uiState.value.copy(introduction = introduction)
    }

    fun onRemarkChanged(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun displayDatePickerDialog(display: Boolean) {
        _uiState.value = _uiState.value.copy(datePickerDialogDisplayFlag = display)
    }

    fun onDateSuccessfulSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onSubmitClicked(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val recordId = recordDao.insert(
                    Record(
                        recordType = RecordType.Todo,
                        recordTime = LocalDateTime.now()
                    )
                )

                todoRecordDao.insert(
                    TodoRecord(
                        recordId = recordId,
                        dueDate = _uiState.value.date,
                        introduction = _uiState.value.introduction,
                        remark = _uiState.value.remark,
                        isCompleted = false
                    )
                )
                onSuccess()
                _uiState.value = _uiState.value.copy(isSubmissionSuccess = true)
            } catch (e: Exception) {
                Log.e(TAG, "onSubmitClicked: fail", e)
                _uiState.value = _uiState.value.copy(isSubmissionSuccess = false)
            }
        }
    }
}

data class TodoSubmitScreenUiState(
    var datePickerDialogDisplayFlag: Boolean = false,
    var isSubmissionSuccess: Boolean? = null,
    var date: LocalDate? = null,
    var introduction: String = "",
    var remark: String = "",
)