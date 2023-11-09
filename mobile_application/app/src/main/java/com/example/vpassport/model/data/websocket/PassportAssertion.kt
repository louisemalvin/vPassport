package com.example.vpassport.model.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class PassportAssertion(
    var documentNumber: String,
    var documentType: String,
    var issuer: String,
    var name: String,
    var nationality: String,
    var birthDate: String,
    var sex: String,
    var issueDate: String,
    var expiryDate: String
)
