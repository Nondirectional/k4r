package com.non.k4r.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "k4r_feature_usage_histories")
data class FeatureUsageHistory(
    @PrimaryKey(autoGenerate = false)
    var featureKey: String,
    @ColumnInfo(name = "last_used_time")
    var lastUsedTime: LocalDateTime,
    @ColumnInfo(name = "used_count")
    var usedCount: Long
)