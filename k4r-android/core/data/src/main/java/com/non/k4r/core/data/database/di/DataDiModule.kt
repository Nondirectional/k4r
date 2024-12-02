package com.non.k4r.core.data.database.di

import android.content.Context
import androidx.room.Room
import com.non.k4r.core.data.database.AppDatabase
import com.non.k4r.core.data.database.dao.ExpenditureTagDao
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
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCategoriesDao(
        database: AppDatabase
    ): ExpenditureTagDao = database.expenditureTagDao()
}