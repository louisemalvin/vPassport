package com.example.vpassport.util.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.vpassport.Passport
import java.io.InputStream
import java.io.OutputStream

object PassportSerializer : Serializer<Passport> {
    override val defaultValue: Passport = Passport.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Passport {
        try {
            return Passport.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Passport, output: OutputStream) = t.writeTo(output)
}
