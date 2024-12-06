package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.ExpenditureRecordWithTags

@Dao
interface ExpenditureRecordDao : BaseDao<ExpenditureRecord> {
    @Query("SELECT * FROM k4r_expenditure_records WHERE expenditureRecordId = :id")
    suspend fun get(id: Long): ExpenditureRecord?

    @Query("SELECT * FROM k4r_expenditure_records")
    suspend fun getAll(): List<ExpenditureRecord>

    @Query("SELECT * FROM k4r_expenditure_records WHERE expenditureRecordId = :id")
    suspend fun getWithTags(id: Long): ExpenditureRecordWithTags?

    @Transaction
    @Query("SELECT * FROM k4r_expenditure_records WHERE recordId = :recordId")
    suspend fun getWithTagsByRecordId(recordId: Long): ExpenditureRecordWithTags?

    @Transaction
    @Query("SELECT * FROM k4r_expenditure_records ORDER BY date(expenditureDate) DESC,expenditureRecordId DESC")
    suspend fun listAllWithTags(): List<ExpenditureRecordWithTags>
}