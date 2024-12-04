package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.non.k4r.core.data.database.constant.RecordType

@Entity(tableName = "k4r_records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "remote_id") val remoteId: Long? = null,
    @ColumnInfo(name = "record_type") val type: RecordType,
)