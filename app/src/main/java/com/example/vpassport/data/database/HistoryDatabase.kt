package com.example.vpassport.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vpassport.data.dao.HistoryDao
import com.example.vpassport.data.model.History
import com.example.vpassport.data.model.LocalDateTimeConverter

@Database(
    entities = [History::class],
    version = 1
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract val dao: HistoryDao
}