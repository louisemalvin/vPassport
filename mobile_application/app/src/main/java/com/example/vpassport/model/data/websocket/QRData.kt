package com.example.vpassport.model.data.websocket

import com.example.vpassport.model.data.websocket.AuthData

data class QRData(
    val wsId: String,
    val apiUrl: String,
    val attributes: List<String>
)
