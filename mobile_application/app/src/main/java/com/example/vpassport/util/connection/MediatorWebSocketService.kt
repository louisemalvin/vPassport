package com.example.vpassport.util.connection

import com.example.vpassport.Passport
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import kotlinx.coroutines.flow.observeOn
import okhttp3.OkHttpClient
import java.util.concurrent.Flow




class   MediatorWebSocketService {

    private val okHttpClient = OkHttpClient.Builder().build()
    private val scarletInstance = Scarlet.Builder()
        .webSocketFactory(okHttpClient.newWebSocketFactory("wss://192.168.1.127:8"))
        .addMessageAdapterFactory(MoshiMessageAdapter.Factory())
        .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
        .build()
    private val mediatorService = scarletInstance.create<MediatorService>()

    fun sendPassport(passport: Passport) {
        mediatorService.sendPassport(passport)
    }

    fun handleEvent() {
        mediatorService.observeConnection()
    }

    fun handleMessage() {
        mediatorService.observeMessage()
    }

}