package com.example.vpassport.util.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.vpassport.AppSettings
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.DefaultAppSettingsRepository
import com.example.vpassport.model.repo.DefaultPassportRepository
import com.example.vpassport.model.repo.interfaces.AppSettingsRepository
import com.example.vpassport.model.repo.interfaces.PassportRepository
import com.example.vpassport.util.serializer.AppSettingsSerializer
import com.example.vpassport.util.serializer.PassportSerializer
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    private val Context.appSettingsDataStore: DataStore<AppSettings> by dataStore(
        fileName = "app_settings.pb",
        serializer = AppSettingsSerializer
    )
    private val Context.passportDataStore: DataStore<Passport> by dataStore(
        fileName = "passport.pb",
        serializer = PassportSerializer
    )

    @Provides
    @Reusable
    fun provideAppSettingsDataStore(@ApplicationContext context: Context) =
        context.appSettingsDataStore

    @Provides
    @Reusable
    internal fun providesAppSettingsRepository(
        @ApplicationContext context: Context,
        appSettingsDataStore: DataStore<AppSettings>
    ): AppSettingsRepository {
        return DefaultAppSettingsRepository(
            context,
            appSettingsDataStore
        )
    }

    @Provides
    @Reusable
    fun providePassportDataStore(@ApplicationContext context: Context) =
        context.passportDataStore

    @Provides
    @Reusable
    internal fun providePassportRepository(
        @ApplicationContext context: Context,
        passportDataStore: DataStore<Passport>
    ): PassportRepository {
        return DefaultPassportRepository(
            context,
            passportDataStore
        )
    }
}