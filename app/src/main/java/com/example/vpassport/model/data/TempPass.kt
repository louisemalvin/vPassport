package com.example.vpassport.model.data
data class TempPass(
    val docType: String,
    val issuer: String,
    val name: String,
    val docNum: String,
    val nationality: String,
    val birthDate: String,
    val sex: String,
    val issueDate: String,
    val expiryDate: String
)