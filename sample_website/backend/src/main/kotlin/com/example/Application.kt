package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.g0dkar.qrcode.QRCode
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.ByteArrayOutputStream

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("api/qr") {
            val content = "Hello Wosafsafasdfsdfsdfsrld!"
            val image = ByteArrayOutputStream()
            QRCode(content).render().writeImage(image)
            val imageBytes = image.toByteArray()
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "qr_code.png")
                    .toString()
            )
            call.respondBytes(imageBytes)
        }

        post("api/verify") {

        }
    }
}
