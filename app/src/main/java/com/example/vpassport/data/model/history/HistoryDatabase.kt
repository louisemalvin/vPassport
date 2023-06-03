package com.example.vpassport.data.model.history

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [History::class],
    version = 1
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract val dao: HistoryDao
}