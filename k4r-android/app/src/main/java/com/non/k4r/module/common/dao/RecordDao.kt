package com.non.k4r.module.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.non.k4r.module.common.entity.RecordEntity

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: RecordEntity)

    @Query("SELECT * FROM k4r_record WHERE id = :id")
    suspend fun getRecord(id: Int): RecordEntity?

    @Query("SELECT * FROM k4r_record")
    suspend fun getAllRecords(): List<RecordEntity>

    @Query("DELETE FROM k4r_record WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}