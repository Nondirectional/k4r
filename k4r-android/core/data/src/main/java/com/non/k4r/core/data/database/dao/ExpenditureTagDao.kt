package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.non.k4r.core.data.database.model.ExpenditureTagEntity

@Dao
interface ExpenditureTagDao : BaseDao<ExpenditureTagEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: ExpenditureTagEntity)

    @Query("SELECT * FROM k4r_expenditure_tags WHERE id = :id")
    suspend fun get(id: Int): ExpenditureTagEntity?

    @Query("SELECT * FROM k4r_expenditure_tags")
    suspend fun getAllTags(): List<ExpenditureTagEntity>
}