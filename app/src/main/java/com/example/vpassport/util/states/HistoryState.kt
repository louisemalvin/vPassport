package com.example.vpassport.util.states

import com.example.vpassport.model.data.History

data class HistoryState(
    val histories: List<History> = emptyList(),
    val site: String = "",
    val isAllowed: Boolean = false,
    val isAddingHistory: Boolean = false
)
