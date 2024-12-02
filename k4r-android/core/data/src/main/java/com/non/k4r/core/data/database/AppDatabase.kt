package com.non.k4r.core.data.database

import androidx.room.RoomDatabase
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.model.ExpenditureTagEntity
import com.non.k4r.core.data.database.model.RecordEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@androidx.room.Database(
    entities = [
        RecordEntity::class,
        ExpenditureTagEntity::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun expenditureTagDao(): ExpenditureTagDao
}

@Module
@InstallIn(ActivityComponent::class)
object DatabaseModule {
    @Provides
    fun expenditureTagDao(database: AppDatabase): ExpenditureTagDao {
        return database.expenditureTagDao()
    }
}