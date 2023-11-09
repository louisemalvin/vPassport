package com.example.vpassport.model.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class CAData(
    val dg14Base64: String
)