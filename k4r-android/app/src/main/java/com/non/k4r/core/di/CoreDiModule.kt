package com.non.k4r.core.di

import android.content.Context
import com.non.k4r.core.holder.InitiatedFlagHolder
import com.non.k4r.module.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreDiModule {
    @Provides
    @Singleton
    fun provideInitiatedFlagHolder(
        @ApplicationContext context: Context
    ): InitiatedFlagHolder =
        InitiatedFlagHolder(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository =
        SettingsRepository(context)
}