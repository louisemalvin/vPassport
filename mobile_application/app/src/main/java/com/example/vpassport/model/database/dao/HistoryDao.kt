package com.example.vpassport.model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.vpassport.model.data.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Upsert
    suspend fun insertHistory(history: History)

    @Delete
    suspend fun deleteHistory(history: History)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getHistories(): Flow<List<History>>

    @Query("DELETE FROM history")
    suspend fun deleteAllHistories()

}