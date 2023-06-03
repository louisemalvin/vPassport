package com.example.vpassport.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.vpassport.AppSettings
import kotlinx.coroutines.flow.map

class DefaultAppSettingsRepository(
    private val context: Context,
    private val appSettingsDataStore: DataStore<AppSettings>
) : AppSettingsRepository {
    override suspend fun getDarkMode(): LiveData<Boolean> {
        return appSettingsDataStore.data.map { currentSettings ->
            currentSettings.darkMode
        }.asLiveData()
    }

    override suspend fun getLanguage(): LiveData<AppSettings.Language> {
        return appSettingsDataStore.data.map { currentSettings ->
            currentSettings.language
        }.asLiveData()
    }

    override suspend fun setDarkMode(boolean: Boolean) {
        appSettingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setDarkMode(boolean)
                .build()
        }
    }

    override suspend fun setLanguage(language: AppSettings.Language) {
        appSettingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setLanguage(language)
                .build()
        }
    }

    override suspend fun resetSettings() {
        appSettingsDataStore.updateData {
            it.toBuilder()
                .clear()
                .build()
        }
    }
}