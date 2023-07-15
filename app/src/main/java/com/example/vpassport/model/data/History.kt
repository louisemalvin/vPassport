package com.example.vpassport.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "site_name")
    val site: String,
    @ColumnInfo(name = "is_allowed")
    val isAllowed: Boolean,
    @ColumnInfo(name = "timestamp")
    val date: LocalDateTime

)


class LocalDateTimeConverter {
    @TypeConverter
    fun timeToString(time: LocalDateTime): String {
        return time.toString()
    }

    @TypeConverter
    fun stringToTime(string: String): LocalDateTime {
        return LocalDateTime.parse(string)
    }
}

