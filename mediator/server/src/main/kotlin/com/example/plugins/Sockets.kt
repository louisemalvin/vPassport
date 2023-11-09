package com.example.plugins

import com.example.data.websocket.CAData
import com.example.data.websocket.WebSocketMessage
import com.example.vpassport.model.data.websocket.ClientData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.Identity.encode
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.slf4j.LoggerFactory
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

private val gson = Gson()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/attest") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    LoggerFactory.getLogger("ktor.application").info("Received: $text")
                    try {
                        val message = gson.fromJson(text, WebSocketMessage::class.java)

                        when (message.event) {
                            WebSocketMessage.EVENT_ASSERTION_INIT -> {
                                val clientCAData = gson.fromJson(message.data, CAData::class.java)
                                val pcdKeyPair = ChipAuthenticator(clientCAData).getKeyPair()
                                if (pcdKeyPair == null) {
                                    CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "Error while generating key pair.")
                                    close()
                                }
                                val pcdKeyPairBase64 = Base64.getEncoder().encodeToString(pcdKeyPair)
                                val result = WebSocketMessage(
                                    WebSocketMessage.EVENT_SIGNATURE_CHALLENGE,
                                    pcdKeyPairBase64
                                )
                                LoggerFactory.getLogger("ktor.application").info("Sending back result: ${gson.toJson(result)}")
                                send(Frame.Text(gson.toJson(result)))
                            }

                            WebSocketMessage.EVENT_CHALLENGE_RESULT -> {
                                val clientData = gson.fromJson(message.data, ClientData::class.java)
                                val signature = AssertionHandler.createAssertion(clientData)
                                val result = WebSocketMessage(WebSocketMessage.EVENT_SIGNATURE_RESULT, gson.toJson(signature))
                                send(Frame.Text(gson.toJson(result)))
                                close()
                            }

                            else -> {
                                // TODO: Handle other event types
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        println("Error parsing JSON data: ${e.message}")
                    }
                }
            }
        }
    }
}

