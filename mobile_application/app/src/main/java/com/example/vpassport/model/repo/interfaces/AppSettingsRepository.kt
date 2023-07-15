package com.example.vpassport.model.repo.interfaces

import com.example.vpassport.AppSettings
import com.example.vpassport.AppSettings.Language
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
//    suspend fun getDarkMode(): LiveData<Boolean>
//    suspend fun getLanguage(): LiveData<Language>
    suspend fun getAppSettings(): Flow<AppSettings>

    suspend fun setDarkMode(boolean: Boolean)
    suspend fun setLanguage(language: Language)
    suspend fun resetSettings()

}