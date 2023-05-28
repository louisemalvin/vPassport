package com.example.vpassport

import android.icu.util.Calendar
import java.util.Date

data class History(
    val site: String,
    val date: Date,
    val isAllowed: Boolean
)
