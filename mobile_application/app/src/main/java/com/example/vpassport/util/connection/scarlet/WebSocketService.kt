package com.example.vpassport.util.connection.scarlet

import com.example.vpassport.Passport
import com.example.vpassport.model.data.PassportAssertion
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow


interface MediatorService {
    @Send
    fun sendPassport(passport: Passport)

    @Receive
    fun observeConnection(): Flow<WebSocket.Event>

    @Receive
    fun observeMessage(): PassportAssertion
}