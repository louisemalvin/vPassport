package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

fun main() {
    val keyStoreFile = File("sample_cert/keystore.jks")
    val keyStorePassword = "123456".toCharArray()
    val privateKeyPassword = "123456".toCharArray()

    val keyStore = KeyStore.getInstance("JKS").apply {
        load(keyStoreFile.inputStream(), keyStorePassword)
    }

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8081
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "mediator",
            keyStorePassword = { keyStorePassword },
            privateKeyPassword = { privateKeyPassword }) {
            port = 8082
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }

    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureRouting()
}
