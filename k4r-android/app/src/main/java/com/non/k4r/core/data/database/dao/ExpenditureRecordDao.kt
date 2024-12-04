package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.ExpenditureRecordWithTags

@Dao
interface ExpenditureRecordDao : BaseDao<ExpenditureRecord> {
    @Query("SELECT * FROM k4r_expenditure_records WHERE id = :id")
    suspend fun get(id: Long): ExpenditureRecord?

    @Query("SELECT * FROM k4r_expenditure_records")
    suspend fun getAll(): List<ExpenditureRecord>

    @Query("SELECT * FROM k4r_expenditure_records WHERE id = :id")
    suspend fun getWithTags(id: Long): ExpenditureRecordWithTags?

    @Query("SELECT * FROM k4r_expenditure_records")
    suspend fun listAllWithTags(): List<ExpenditureRecordWithTags>
}