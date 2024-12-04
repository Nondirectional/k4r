package com.non.k4r.module.expenditure.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.ExpenditureRecordWithTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(
    expenditureRecordDao: ExpenditureRecordDao
) : ViewModel() {
    val TAG: String = "MainScreenViewModel"

    private val _uiState =
        MutableStateFlow<MainScreenUiState>(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            val expenditureList: List<ExpenditureRecordWithTags> =
                expenditureRecordDao.listAllWithTags()
            _uiState.value = _uiState.value.copy(expenditureList = expenditureList)
        }
    }

}

data class MainScreenUiState(
    var expenditureList: List<ExpenditureRecordWithTags> = emptyList(),
)