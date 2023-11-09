package com.example

data class WebSocketMessage(
    val event: String,
    val data: String,
) {
    companion object {
        const val EVENT_ASSERTION_INIT = "assertion_request"
        const val EVENT_RESULT = "assertion_response"
        const val EVENT_SIGNATURE_CHALLENGE = "signature_challenge"
    }
}
