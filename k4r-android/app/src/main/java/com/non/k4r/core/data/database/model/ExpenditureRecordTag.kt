package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "k4r_expenditure_record_tag_rel")
data class ExpenditureRecordTag(
    @PrimaryKey(autoGenerate = true)
    var relId: Long = 0L,

    @ColumnInfo(name = "expenditureRecordId")
    var expenditureRecordId: Long,

    @ColumnInfo(name = "expenditureRecordTagId")
    var expenditureRecordTagId: Long
)
