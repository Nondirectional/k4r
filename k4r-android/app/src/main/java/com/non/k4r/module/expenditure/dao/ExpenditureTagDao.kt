package com.non.k4r.module.expenditure.dao;

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity

@Dao
interface ExpenditureTagDao {
    @Insert
    suspend fun insert(tag: ExpenditureTagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: ExpenditureTagEntity)

    @Query("SELECT * FROM k4r_expenditure_tags WHERE id = :id")
    suspend fun get(id: Int): ExpenditureTagEntity?

    @Query("SELECT * FROM k4r_expenditure_tags")
    suspend fun getAllTags(): List<ExpenditureTagEntity>

}