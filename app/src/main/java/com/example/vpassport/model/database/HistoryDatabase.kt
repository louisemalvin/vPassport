package com.example.vpassport.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vpassport.model.database.dao.HistoryDao
import com.example.vpassport.model.data.History
import com.example.vpassport.model.data.LocalDateTimeConverter

@Database(
    entities = [History::class],
    version = 1
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract val dao: HistoryDao
}