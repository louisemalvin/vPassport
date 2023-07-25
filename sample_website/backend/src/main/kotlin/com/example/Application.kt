package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.g0dkar.qrcode.QRCode
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun generateQRCode(wsId: String): String {
    var content = "ws:$wsId;url:192.168.86.23:8080/verify"
    val image = ByteArrayOutputStream()
    QRCode(content).render().writeImage(image)
    val imageBytes = image.toByteArray()
    return Base64.getEncoder().encodeToString(imageBytes)
}

fun verify() : Boolean {
    // TODO : Do some verification
    return true;
}

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val clients = HashMap<String, WebSocketServerSession>()

    routing {
        webSocket("/ws") { // websocketSession
            val wsId = UUID.randomUUID().toString()
            clients[wsId] = this
            // Send unique QR Code for the session
            val qrBase64 = generateQRCode(wsId)
            val qrEvent = "{\"event\":\"qr\",\"data\":\"$qrBase64\"}"
            outgoing.send(Frame.Text(qrEvent))

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        // Handle incoming text message
                        // ...
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                // Handle WebSocket channel closure
                // Remove the WebSocket session from the clients map
                clients.remove(wsId)
            } finally {
                // WebSocket connection closed, remove the WebSocket session from the clients map
                clients.remove(wsId)
            }
        }

        post("/verify") {
            val proof = call.parameters["proof"]
            val wsId = call.parameters["wsId"]
            var authenticated = false

            // Verify proof given
            authenticated = verify()

            // Find WebSocket session
            val session = clients[wsId]

            if (session != null) {
                // WebSocket session found, send authentication event
                val authenticationMessage = "{\"eventType\":\"authentication\",\"status\":\"$authenticated\"}"
                session.outgoing.send(Frame.Text(authenticationMessage))
                call.respond("User authenticated successfully.")
            } else {
                // WebSocket session not found
                call.respond("Authentication failed: Invalid WebSocket ID.")
            }
        }
    }
}