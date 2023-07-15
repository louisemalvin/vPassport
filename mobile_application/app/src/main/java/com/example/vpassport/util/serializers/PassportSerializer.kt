package com.example.vpassport.util.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.vpassport.Passport
import com.example.vpassport.util.CryptoManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

object PassportSerializer : Serializer<Passport> {
    private val cryptoManager = CryptoManager()
    override val defaultValue: Passport = Passport.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Passport {
        try {
            val decryptedBytes = cryptoManager.decrypt(input)
            val decryptedStream = ByteArrayInputStream(decryptedBytes)
            return Passport.parseFrom(decryptedStream)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Passport, output: OutputStream) {
        val byteStream = ByteArrayOutputStream()
        t.writeTo(byteStream)
        cryptoManager.encrypt(byteStream.toByteArray(), output)
    }
}
