package com.non.k4r.module.common.db;

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.non.k4r.module.common.dao.RecordDao
import com.non.k4r.module.common.entity.RecordEntity
import com.non.k4r.module.expenditure.dao.ExpenditureTagDao
import com.non.k4r.module.expenditure.entity.ExpenditureTagEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Database(entities = [RecordEntity::class, ExpenditureTagEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun expenditureTagDao(): ExpenditureTagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "k4r"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
object DatabaseModule {
    @Provides
    fun expenditureTagDao(database: AppDatabase): ExpenditureTagDao {
        return database.expenditureTagDao()
    }
}