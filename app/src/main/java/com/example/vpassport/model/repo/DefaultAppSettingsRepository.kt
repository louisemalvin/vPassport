package com.example.vpassport.model.repo

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.vpassport.AppSettings
import com.example.vpassport.model.repo.interfaces.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultAppSettingsRepository @Inject constructor(
    private val context: Context,
    private val appSettingsDataStore: DataStore<AppSettings>
) : AppSettingsRepository {

    override suspend fun getAppSettings(): Flow<AppSettings> {
        return appSettingsDataStore.data
    }
//    override suspend fun getDarkMode(): LiveData<Boolean> {
//        return appSettingsDataStore.data.map { currentSettings ->
//            currentSettings.darkMode
//        }.asLiveData()
//    }
//
//    override suspend fun getLanguage(): LiveData<AppSettings.Language> {
//        return appSettingsDataStore.data.map { currentSettings ->
//            currentSettings.language
//        }.asLiveData()
//    }

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