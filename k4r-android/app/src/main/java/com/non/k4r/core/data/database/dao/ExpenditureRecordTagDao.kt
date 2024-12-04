package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.ExpenditureRecordTag

@Dao
interface ExpenditureRecordTagDao: BaseDao<ExpenditureRecordTag> {
    @Query("SELECT * FROM k4r_expenditure_record_tag_rel WHERE id = :id")
    suspend fun get(id: Int): ExpenditureRecordTag?

}