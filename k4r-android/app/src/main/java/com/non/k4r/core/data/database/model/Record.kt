package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.non.k4r.core.data.database.constant.RecordType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "k4r_records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "remoteId") val remoteId: Long? = null,
    @ColumnInfo(name = "recordType") val recordType: RecordType,
    @ColumnInfo(name = "recordTime") val recordTime: LocalDateTime,
)