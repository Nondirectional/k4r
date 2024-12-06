package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.Record
import javax.inject.Singleton

@Dao
@Singleton
interface RecordDao : BaseDao<Record> {
    @Query(
        """
      SELECT * 
      FROM k4r_records 
      WHERE CASE WHEN :id IS NULL THEN 1 ELSE id = :id END
      LIMIT :pageSize OFFSET :offset
    """
    )
    fun pageRecords(pageSize: Int, offset: Int, id: Long?): List<Record>
}