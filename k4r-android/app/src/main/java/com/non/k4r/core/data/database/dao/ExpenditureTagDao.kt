package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.ExpenditureTag

@Dao
interface ExpenditureTagDao : BaseDao<ExpenditureTag> {
    @Query("SELECT * FROM k4r_expenditure_tags WHERE expenditureRecordTagId = :id")
    suspend fun get(id: Int): ExpenditureTag?

    @Query("SELECT * FROM k4r_expenditure_tags")
    suspend fun getAll(): List<ExpenditureTag>
}