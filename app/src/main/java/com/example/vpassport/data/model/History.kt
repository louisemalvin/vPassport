package com.example.vpassport.data.model

import android.icu.util.Calendar

data class History(
    val site: String,
    val date: Calendar,
    val isAllowed: Boolean
)