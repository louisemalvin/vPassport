package com.example.vpassport.data.model.history

data class HistoryState(
    val histories: List<History> = emptyList(),
    val site: String = "",
    val isAllowed: Boolean = false,
    val isAddingHistory: Boolean = false
)
