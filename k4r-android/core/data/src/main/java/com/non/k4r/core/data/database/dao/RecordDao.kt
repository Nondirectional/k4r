package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import com.non.k4r.core.data.database.model.RecordEntity
import javax.inject.Singleton

@Dao
@Singleton
interface RecordDao : BaseDao<RecordEntity> {
}