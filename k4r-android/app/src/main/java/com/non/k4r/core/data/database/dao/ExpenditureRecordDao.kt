package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.ExpenditureRecord

@Dao
interface ExpenditureRecordDao : BaseDao<ExpenditureRecord> {
    @Query("SELECT * FROM k4r_expenditure_records WHERE id = :id")
    suspend fun get(id: Int): ExpenditureRecord?

    @Query("SELECT * FROM k4r_expenditure_records")
    suspend fun getAll(): List<ExpenditureRecord>
}