package com.example.vpassport.data.repo

import androidx.lifecycle.LiveData
import com.example.vpassport.AppSettings.Language

interface AppSettingsRepository {
    suspend fun getDarkMode(): LiveData<Boolean>
    suspend fun getLanguage(): LiveData<Language>
    suspend fun setDarkMode(boolean: Boolean)
    suspend fun setLanguage(language: Language)
    suspend fun resetSettings()

}