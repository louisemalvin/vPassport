package com.example.vpassport.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val darkMode: Boolean = false,
    val language: Language = Language.ENGLISH
)

enum class Language {
    ENGLISH, GERMAN
}