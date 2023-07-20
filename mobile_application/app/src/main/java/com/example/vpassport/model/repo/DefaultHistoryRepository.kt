package com.example.vpassport.model.repo

import com.example.vpassport.model.data.History
import com.example.vpassport.model.database.dao.HistoryDao
import com.example.vpassport.model.repo.interfaces.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultHistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override suspend fun addHistory(history: History) {
        historyDao.insertHistory(history)
    }

    override suspend fun removeHistory(history: History) {
        historyDao.deleteHistory(history)
    }

    override suspend fun removeAllHistories() {
        historyDao.deleteAllHistories()
    }

    override suspend fun getHistories(): Flow<List<History>> {
        return historyDao.getHistories()
    }


}