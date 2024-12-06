package com.non.k4r.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.non.k4r.core.data.database.model.TodoRecord

@Dao
interface TodoRecordDao :BaseDao<TodoRecord> {
    @Query("SELECT * FROM k4r_todo_records WHERE recordId = :recordId")
    fun getByRecordId(recordId: Long): TodoRecord?
}