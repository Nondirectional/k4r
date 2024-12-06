package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "k4r_todo_records")
data class TodoRecord(
    @PrimaryKey(autoGenerate = true)
    var todoRecordId: Long = 0L,
    @ColumnInfo(name = "remoteId")
    var remoteId: Long? = null,
    @ColumnInfo(name = "recordId")
    var recordId: Long,
    @ColumnInfo(name = "introduction")
    var introduction: String,
    @ColumnInfo(name = "remark")
    var remark: String,
    @ColumnInfo(name = "dueDate")
    var dueDate: LocalDate? = null,
    @ColumnInfo(name = "finishedDate")
    var finishedDate: LocalDate? = null,
    @ColumnInfo(name = "isCompleted")
    var isCompleted: Boolean = false
)
