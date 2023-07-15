package com.example.vpassport.model.repo.interfaces

import com.example.vpassport.model.data.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun addHistory(history: History)
    suspend fun removeHistory(history: History)
    suspend fun removeAllHistories()
    suspend fun getHistories() : Flow<List<History>>
}