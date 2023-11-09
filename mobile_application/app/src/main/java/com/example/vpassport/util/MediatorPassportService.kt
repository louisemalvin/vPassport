package com.example.vpassport.util

import net.sf.scuba.smartcards.CardService
import net.sf.scuba.smartcards.CardServiceException
import org.jmrtd.APDULevelEACTACapable
import org.jmrtd.CardServiceProtocolException
import org.jmrtd.DefaultFileSystem
import org.jmrtd.PassportService
import org.jmrtd.protocol.EACCAAPDUSender
import org.jmrtd.protocol.EACTAAPDUSender
import org.jmrtd.protocol.ReadBinaryAPDUSender
import org.jmrtd.protocol.SecureMessagingWrapper
import java.math.BigInteger
import java.security.KeyPair
import java.security.PublicKey

class MediatorPassportService(
    service: CardService?,
    maxTranceiveLengthForSecureMessaging: Int,
    maxBlockSize: Int,
    isSFIEnabled: Boolean,
    shouldCheckMAC: Boolean
) : PassportService(
    service,
    maxTranceiveLengthForSecureMessaging,
    maxBlockSize,
    isSFIEnabled,
    shouldCheckMAC
) {

    private val maxTranceiveLengthForSecureMessaging = 0
    private val shouldCheckMAC = false
    private val appletFileSystem: DefaultFileSystem =
        DefaultFileSystem(ReadBinaryAPDUSender(service), isSFIEnabled)
    private val eacCASender: EACCAAPDUSender = EACCAAPDUSender(service)
    private val service: APDULevelEACTACapable = EACTAAPDUSender(service)

    @Synchronized
    @Throws(CardServiceException::class)
    fun livelinessTest(
        keyId: BigInteger?,
        oid: String?,
        publicKeyOID: String?,
        publicKey: PublicKey?,
        pcdKeyPair: KeyPair
    ): ByteArray? {
        val caResult = MediatorCAProtocol(
            eacCASender,
            wrapper,
            maxTranceiveLengthForSecureMessaging,
            shouldCheckMAC
        ).doMediatorCA(keyId, oid, publicKeyOID, publicKey, pcdKeyPair)
        val caWrapper = caResult!!.wrapper
        appletFileSystem.wrapper = caWrapper
        try {
            return service.sendGetChallenge(caWrapper)
        } catch (e: Exception) {
            throw CardServiceProtocolException("Exception in Get Challenge", 4, e)
        }
    }
}