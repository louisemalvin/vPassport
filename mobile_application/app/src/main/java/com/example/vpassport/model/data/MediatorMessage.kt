package com.example.vpassport.model.data

data class MediatorMessage(
    val event: Event,
    val data: String
)

enum class Event {
    INIT, RESULT
}