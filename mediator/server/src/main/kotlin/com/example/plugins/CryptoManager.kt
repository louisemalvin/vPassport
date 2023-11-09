package com.example.plugins


import java.io.File
import java.security.KeyStore

object CryptoManager {
    private val keyStoreFile: File = File("C:\\Users\\louis\\OneDrive\\Documents\\vPassport\\vPassport\\mediator\\server\\sample_cert\\keystore.jks")
    private val keyStorePassword: CharArray = "123456".toCharArray()
    private val privateKeyPassword: CharArray = "123456".toCharArray()

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance("JKS").apply {
            load(keyStoreFile.inputStream(), keyStorePassword)
        }
    }

    fun getCertificateKeyStoreFile() : File {
        return keyStoreFile
    }

    fun getCertificateKeyStore(): KeyStore {
        return keyStore
    }

    fun getKeyAlias(): String {
        return "mediator"
    }

    fun getKeyStorePassword(): CharArray {
        return keyStorePassword
    }

    fun getPrivateKeyPassword(): CharArray {
        return privateKeyPassword
    }
}