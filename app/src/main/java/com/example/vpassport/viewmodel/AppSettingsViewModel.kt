package com.example.vpassport.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.AppSettings
import com.example.vpassport.AppSettings.Language
import com.example.vpassport.model.repo.interfaces.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
): ViewModel() {

    init {
        getAppSettings()
    }

    private lateinit var _appSettings: LiveData<AppSettings>

    val appSettings: LiveData<AppSettings> = _appSettings

    private fun getAppSettings() = viewModelScope.launch {
        _appSettings = appSettingsRepository.getAppSettings()
    }
    fun setDarkMode(boolean: Boolean) = viewModelScope.launch {
        appSettingsRepository.setDarkMode(boolean)
    }
    fun setLanguage(language: Language) = viewModelScope.launch {
        appSettingsRepository.setLanguage(language)
    }
    fun resetDefault() = viewModelScope.launch {
        appSettingsRepository.resetSettings()
    }
}