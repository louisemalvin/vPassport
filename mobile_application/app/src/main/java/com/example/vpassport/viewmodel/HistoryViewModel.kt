package com.example.vpassport.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vpassport.model.data.History
import com.example.vpassport.model.repo.interfaces.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository : HistoryRepository
): ViewModel() {
    init {
        getHistories()
    }

    private lateinit var _histories : LiveData<List<History>>
    private lateinit var _isAdding : MutableStateFlow<Boolean>
    val histories = _histories
    val isAdding = _isAdding.asStateFlow()
    private fun getHistories() {
        viewModelScope.launch {
            _histories = historyRepository.getHistories().asLiveData()
            _isAdding = MutableStateFlow(false)
        }
    }
    fun addHistory(history: History) { 
        viewModelScope.launch {
            historyRepository.addHistory(history)
        }
    }
    fun deleteHistory(history: History) {
        viewModelScope.launch {
            historyRepository.removeHistory(history)
        }
    }
    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.removeAllHistories()
        }
    }
    fun setIsAdding(boolean: Boolean) {
        _isAdding.value = boolean
    }
}