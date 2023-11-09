package com.example

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.g0dkar.qrcode.QRCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap

data class QRData(
    val wsId: String,
    val apiUrl: String,
    val attributes: List<String>
)

fun buildJSONMessage(event: String, data: Map<String, Any>): String {
    val jsonMap = mutableMapOf<String, Any>()
    jsonMap["event"] = event
    jsonMap.putAll(data)

    val gson = Gson()
    return gson.toJson(jsonMap)
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun generateQRCode(wsId: String, apiUrl: String): String {
    val gson = Gson()
    val content = gson.toJson(QRData(wsId, apiUrl, listOf("name")))
    val image = ByteArrayOutputStream()
    QRCode(content).render().writeImage(image)
    val imageBytes = image.toByteArray()
    return Base64.getEncoder().encodeToString(imageBytes)
}

fun verify(): Boolean {
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
            val apiUrl = "ws://192.168.86.23:8080/verify"
            val qrBase64 = generateQRCode(wsId, apiUrl)
            val qrEvent = buildJSONMessage("qr", mapOf("data" to qrBase64))
            outgoing.send(Frame.Text(qrEvent))

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                clients.remove(wsId)
            } finally {
                clients.remove(wsId)
            }
        }

        webSocket("/verify") { // websocketSession
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        // Parse the raw data to a UserData object using Gson
                        LoggerFactory.getLogger( "Received data: $text")
                        val message = Gson().fromJson(text, WebSocketMessage::class.java)
                        val userData = Gson().fromJson(message.data, AuthData::class.java)
                        // Access the properties from the parsed object
                        val wsId = userData.wsId
                        var authenticated = false

                        // TODO: Verify proof given
                        authenticated = verify()

                        // Find WebSocket session
                        val session = clients[wsId]
                        if (session != null) {

                            // WebSocket session found, send authentication event
                            val authenticationMessage =
                                buildJSONMessage("authentication", mapOf("status" to authenticated))
                            session.outgoing.send(Frame.Text(authenticationMessage))
                            session.outgoing.close()
                            close(CloseReason(CloseReason.Codes.NORMAL, "Authentication complete"))

                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {

            }
        }
    }
}