package com.example.vpassport.util.connection.ktor

import com.example.vpassport.model.data.MediatorMessage
import com.example.vpassport.model.data.websocket.CAData
import com.example.vpassport.model.data.websocket.ClientData
import com.example.vpassport.model.data.websocket.WebSocketMessage
import com.example.vpassport.util.Resource
import kotlinx.coroutines.flow.Flow

interface MediatorService {
    fun getMessage() : Flow<WebSocketMessage>
    suspend fun connect(clientCAData: CAData) : Resource<Unit>
    suspend fun sendMessage(message: WebSocketMessage)
    suspend fun close()
}