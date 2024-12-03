package com.non.k4r.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.model.ExpenditureTagEntity
import com.non.k4r.core.data.database.model.RecordEntity

@Database(
    entities = [
        RecordEntity::class,
        ExpenditureTagEntity::class], version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun expenditureTagDao(): ExpenditureTagDao
}
