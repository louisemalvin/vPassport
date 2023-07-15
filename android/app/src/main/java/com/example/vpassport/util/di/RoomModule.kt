package com.example.vpassport.util.di

import android.content.Context
import androidx.room.Room
import com.example.vpassport.model.database.HistoryDatabase
import com.example.vpassport.model.database.dao.HistoryDao
import com.example.vpassport.model.repo.DefaultHistoryRepository
import com.example.vpassport.model.repo.interfaces.HistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideHistoryDatabase(@ApplicationContext appContext: Context): HistoryDatabase {
        return Room.databaseBuilder(
            appContext,
            HistoryDatabase::class.java,
            "history.db"
        ).build()
    }
    @Singleton
    @Provides
    fun provideHistoryDao(historyDatabase: HistoryDatabase) : HistoryDao {
        return historyDatabase.dao
    }

    @Provides
    fun provideHistoryRepository(historyDao: HistoryDao) : HistoryRepository {
        return DefaultHistoryRepository(historyDao)
    }
}