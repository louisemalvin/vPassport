package com.example.data.websocket

data class WebSocketMessage(
    val event: String,
    val data: String,
) {
    companion object {
        const val EVENT_ASSERTION_INIT = "assertion_request"
        const val EVENT_SIGNATURE_CHALLENGE = "signature_challenge"
        const val EVENT_CHALLENGE_RESULT = "challenge_result"
        const val EVENT_SIGNATURE_RESULT = "signature_result"
    }
}
