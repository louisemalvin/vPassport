package com.example.vpassport.util.connection.ktor

import com.example.vpassport.model.data.websocket.AuthData
import com.example.vpassport.model.data.websocket.WebSocketMessage
import com.example.vpassport.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthenticationService {
    fun getMessage() : Flow<WebSocketMessage>
    suspend fun connect(authData: AuthData, url: String) : Resource<Unit>
    suspend fun sendMessage(message: WebSocketMessage)
    suspend fun close()
}