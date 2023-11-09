package com.example.vpassport.model.data.websocket

import com.example.data.websocket.PassportAssertion

data class ClientData(
    val sodFile: String,
    val dG14File: String,
    val publicKey: String,
    val challenge: String,
    val passportAssertion: PassportAssertion
)
