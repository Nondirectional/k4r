package com.non.k4r.module.expenditure.vm;

import android.util.Log
import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.core.data.database.constant.ExpenditureType
import com.non.k4r.core.data.database.constant.RecordType
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.ExpenditureRecordTagDao
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.ExpenditureRecordTag
import com.non.k4r.core.data.database.model.ExpenditureTag
import com.non.k4r.core.data.database.model.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ExpenditureSubmitScreenViewModel @Inject constructor(
    private val expenditureTagDao: ExpenditureTagDao,
    private val recordDao: RecordDao,
    private val expenditureRecordDao: ExpenditureRecordDao,
    private val expenditureRecordTagDao: ExpenditureRecordTagDao
) : ViewModel() {

    val TAG: String = "ExpenditureSubmitScreenViewModel"

    private val _uiState =
        MutableStateFlow<ExpenditureSubmitScreenUiState>(ExpenditureSubmitScreenUiState())
    val uiState: StateFlow<ExpenditureSubmitScreenUiState> = _uiState

    init {
        loadTags()
    }

    fun loadTags() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tags = expenditureTagDao.getAll())
        }
    }

    fun onAmountChanged(amount: String) {
        try {
            _uiState.value = _uiState.value.copy(
                amount = if (amount.isBlank())
                    amount
                else
                    "-?(0|[1-9]?[0-9]+)+\\.?([0-9]+)?".toRegex()
                        .find(amount)?.value ?: amount
            )
        } catch (e: Exception) {
            Log.e(TAG, "ExpenditureSubmitScreen: fail", e)
        }
    }

    fun onAmountFocusEvent(focusState: FocusState) {
        if (!focusState.hasFocus) {
            if (!_uiState.value.amount.isBlank())
                _uiState.value.copy(
                    amount = try {
                        String.format(
                            locale = Locale.getDefault(),
                            format = "%.2f",
                            _uiState.value.amount.toFloat()
                        )
                    } catch (_: Exception) {
                        "0.00"
                    }
                )
        }
    }

    fun onIntroductionChanged(introduction: String) {
        _uiState.value = _uiState.value.copy(introduction = introduction)
    }

    fun onRemarkChanged(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun onExpenditureTypeChanged(expenditureType: ExpenditureType) {
        _uiState.value = _uiState.value.copy(expenditureType = expenditureType)
    }

    fun onTagSelected(tag: ExpenditureTag) {
        val selectedTags = _uiState.value.selectedTags.toMutableMap()
        selectedTags[tag.key] = tag
        _uiState.value = _uiState.value.copy(selectedTags = selectedTags)
    }

    fun onTagDeselected(tag: ExpenditureTag) {
        val selectedTags = _uiState.value.selectedTags.toMutableMap()
        selectedTags.remove(tag.key)
        _uiState.value = _uiState.value.copy(selectedTags = selectedTags)
    }

    fun displayDatePickerDialog(display: Boolean) {
        _uiState.value = _uiState.value.copy(datePickerDialogDisplayFlag = display)
    }

    fun onDateSuccessfulSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onSubmitClicked() {
        val selectedTags: List<ExpenditureTag> = _uiState.value.selectedTags.values.toList()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val recordId = recordDao.insert(Record(type = RecordType.Expenditure))

                val expenditureRecordId = expenditureRecordDao.insert(
                    ExpenditureRecord(
                        recordId = recordId,
                        recordDate = _uiState.value.date!!,
                        amount = (_uiState.value.amount.toFloat() * 100).toLong(),
                        introduction = _uiState.value.introduction,
                        remark = _uiState.value.remark,
                        expenditureType = _uiState.value.expenditureType
                    )
                )
                Log.d(
                    TAG,
                    "onSubmitClicked: expenditure record inserted, id: $expenditureRecordId"
                )
                if (selectedTags.isNotEmpty()) {
                    expenditureRecordTagDao.insertAll(selectedTags.map {
                        ExpenditureRecordTag(
                            recordId = expenditureRecordId, tagId = it.id,
                        )
                    })
                }
                Log.d(TAG, "onSubmitClicked: expenditure record and tags inserted")
                _uiState.value = _uiState.value.copy(isSubmissionSuccess = true)
            } catch (e: Exception) {
                Log.e(TAG, "onSubmitClicked: fail", e)
                _uiState.value = _uiState.value.copy(isSubmissionSuccess = false)
            }
        }
    }
}

data class ExpenditureSubmitScreenUiState(
    var datePickerDialogDisplayFlag: Boolean = false,
    var isSubmissionSuccess: Boolean? = null,
    var date: LocalDate? = LocalDate.now(),
    var tags: List<ExpenditureTag> = emptyList<ExpenditureTag>(),
    var amount: String = "",
    var introduction: String = "",
    var remark: String = "",
    var expenditureType: ExpenditureType = ExpenditureType.Expenditure,
    var selectedTags: Map<String, ExpenditureTag> = mutableMapOf<String, ExpenditureTag>(),
)