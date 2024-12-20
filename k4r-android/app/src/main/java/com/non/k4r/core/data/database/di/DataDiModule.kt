package com.non.k4r.core.data.database.di

import android.content.Context
import androidx.room.Room
import com.non.k4r.core.data.database.AppDatabase
import com.non.k4r.core.data.database.dao.ExpenditureRecordDao
import com.non.k4r.core.data.database.dao.ExpenditureRecordTagDao
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
import com.non.k4r.core.data.database.dao.FeatureUsageHistoryDao
import com.non.k4r.core.data.database.dao.RecordDao
import com.non.k4r.core.data.database.dao.TodoRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "k4r_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideRecordDao(
        database: AppDatabase
    ): RecordDao = database.recordDao()

    @Provides
    @Singleton
    fun provideExpenditureTagDao(
        database: AppDatabase
    ): ExpenditureTagDao = database.expenditureTagDao()

    @Provides
    @Singleton
    fun provideExpenditureRecordDao(
        database: AppDatabase
    ): ExpenditureRecordDao = database.expenditureRecordDao()

    @Provides
    @Singleton
    fun provideExpenditureRecordTagDao(
        database: AppDatabase
    ): ExpenditureRecordTagDao = database.expenditureRecordTagDao()

    @Provides
    @Singleton
    fun provideFeatureUsageHistoryDao(
        database: AppDatabase
    ): FeatureUsageHistoryDao = database.featureUsageHistoryDao()

    @Provides
    @Singleton
    fun provideTodoRecordDao(
        database: AppDatabase
    ): TodoRecordDao = database.todoRecordDao()
}