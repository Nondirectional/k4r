package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "k4r_expenditure_tags")
data class ExpenditureTagEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "remote_id")
    var remoteId: Long? = null,

    @ColumnInfo(name = "key")
    var key: String,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "icon_key")
    var iconKey: String? = null,

    @ColumnInfo(name = "is_custom")
    var isCustom: Boolean = false
)