package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "k4r_expenditure_record_tag_rel")
data class ExpenditureRecordTag(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "record_id")
    var recordId: Long,

    @ColumnInfo(name = "tag_id")
    var tagId: Long
)
