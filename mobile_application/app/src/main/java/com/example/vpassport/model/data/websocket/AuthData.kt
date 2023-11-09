package com.example.vpassport.model.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    var wsId: String,
    var documentNumber: String? = null,
    var documentType: String? = null,
    var issuer: String? = null,
    var name: String? = null,
    var nationality: String? = null,
    var birthDate: String? = null,
    var sex: String? = null,
    var issueDate: String? = null,
    var expiryDate: String? = null,

    var documentNumberSignature: String? = null,
    var documentTypeSignature: String? = null,
    var issuerSignature: String? = null,
    var nameSignature: String? = null,
    var nationalitySignature: String? = null,
    var birthDateSignature: String? = null,
    var sexSignature: String? = null,
    var issueDateSignature: String? = null,
    var expiryDateSignature: String? = null
)