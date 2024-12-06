package com.non.k4r.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.non.k4r.core.data.database.converter.K4rDateTimeTypeConverters
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.ExpenditureRecordTagDao
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.dao.FeatureUsageHistoryDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import com.non.k4r.core.data.database.model.ExpenditureRecord
import com.non.k4r.core.data.database.model.ExpenditureRecordTag
import com.non.k4r.core.data.database.model.ExpenditureTag
import com.non.k4r.core.data.database.model.FeatureUsageHistory
import com.non.k4r.core.data.database.model.Record
import com.non.k4r.core.data.database.model.TodoRecord

@Database(
    entities = [
        Record::class,
        ExpenditureTag::class,
        ExpenditureRecord::class,
        ExpenditureRecordTag::class,
        FeatureUsageHistory::class,
        TodoRecord::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(K4rDateTimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun expenditureTagDao(): ExpenditureTagDao
    abstract fun expenditureRecordDao(): ExpenditureRecordDao
    abstract fun expenditureRecordTagDao(): ExpenditureRecordTagDao
    abstract fun featureUsageHistoryDao(): FeatureUsageHistoryDao
    abstract fun todoRecordDao(): TodoRecordDao
}
