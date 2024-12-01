package com.non.k4r.module.expenditure.vm;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.non.k4r.module.expenditure.ExpenditureType
import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenditureSubmitScreenViewModel : ViewModel() {
    private val _data = MutableStateFlow<ExpenditureSubmitScreenData>(ExpenditureSubmitScreenData())
    val data: StateFlow<ExpenditureSubmitScreenData> = _data

    fun loadTags() {
        viewModelScope.launch {
            _data.value.tags = da
        }
    }
}

data class ExpenditureSubmitScreenData(
    var tags: List<ExpenditureTagEntity> = emptyList<ExpenditureTagEntity>(),
    var amount: String = "",
    var introduction: String = "",
    var remark: String = "",
    var expenditureType: ExpenditureType = ExpenditureType.Expenditure,
    var selectedTas: Map<String, ExpenditureTagEntity> = mutableMapOf<String, ExpenditureTagEntity>(),
)