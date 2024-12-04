package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.non.k4r.core.data.database.constant.ExpenditureType
import java.time.LocalDate

@Entity(tableName = "k4r_expenditure_records")
data class ExpenditureRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "record_id")
    var recordId: Long? = null,

    @ColumnInfo(name = "remote_id")
    var remoteId: Long? = null,

    @ColumnInfo
    var amount: Long,

    @ColumnInfo(name = "introduction")
    var introduction: String,

    @ColumnInfo(name = "remark")
    var remark: String = "",

    @ColumnInfo(name = "record_date")
    var recordDate: LocalDate,

    @ColumnInfo(name = "expenditure_type")
    var expenditureType: ExpenditureType,
)