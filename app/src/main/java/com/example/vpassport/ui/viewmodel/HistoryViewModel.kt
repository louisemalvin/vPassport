package com.example.vpassport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.data.model.history.History
import com.example.vpassport.data.model.history.HistoryDao
import com.example.vpassport.data.model.history.HistoryEvent
import com.example.vpassport.data.model.history.HistoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HistoryViewModel(
    private val dao: HistoryDao
): ViewModel() {
    private val _histories = dao.getHistories()
    private val _state = MutableStateFlow(HistoryState())
    val state = combine(_state, _histories) {
        state, histories ->
        state.copy(
            histories = histories
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryState())

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.DeleteHistory -> {
                viewModelScope.launch {
                    dao.deleteHistory(event.history)
                }
            }

            is HistoryEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingHistory = false
                    )
                }
            }

            is HistoryEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingHistory = true
                    )
                }
            }

            is HistoryEvent.SetIsAllowed -> {
                _state.update {
                    it.copy(
                        isAllowed = event.isAllowed
                    )
                }
            }

            is HistoryEvent.SetSite -> {
                _state.update {
                    it.copy(
                        site = event.site
                    )
                }
            }

            HistoryEvent.AddHistory -> {
                val site = _state.value.site
                val isAllowed = _state.value.isAllowed
                if (site.isBlank()) {
                    return
                }
                val history = History(
                    site = site,
                    isAllowed = isAllowed,
                    date = LocalDateTime.now(),
                )
                viewModelScope.launch {
                    dao.insertHistory(history)
                }
            }
        }
    }
}