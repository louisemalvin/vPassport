package com.example.vpassport.data.states

import com.example.vpassport.data.model.History

data class HistoryState(
    val histories: List<History> = emptyList(),
    val site: String = "",
    val isAllowed: Boolean = false,
    val isAddingHistory: Boolean = false
)
