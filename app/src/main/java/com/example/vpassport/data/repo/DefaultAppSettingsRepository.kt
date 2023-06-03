package com.example.vpassport.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import com.example.vpassport.AppSettings

class DefaultAppSettingsRepository(
    private val context: Context,
    private val appSettingsDataStore: DataStore<AppSettings>
) : AppSettingsRepository {
    override suspend fun getDarkMode(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getLanguage(): LiveData<AppSettings.Language> {
        TODO("Not yet implemented")
    }

    override suspend fun setDarkMode(boolean: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage(language: AppSettings.Language) {
        TODO("Not yet implemented")
    }

    override suspend fun resetSettings() {
        TODO("Not yet implemented")
    }
}