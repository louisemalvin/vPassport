package com.example.vpassport.util.events

import com.example.vpassport.model.`1`.History

sealed interface HistoryEvent {
    object AddHistory: HistoryEvent
    data class DeleteHistory(val history: History): HistoryEvent

    object HideDialog: HistoryEvent
    object ShowDialog: HistoryEvent
    data class SetSite(val site: String): HistoryEvent
    data class SetIsAllowed(val isAllowed: Boolean): HistoryEvent

    }