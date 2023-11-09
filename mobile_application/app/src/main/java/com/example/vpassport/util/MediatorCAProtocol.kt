package com.example.vpassport.util

import android.util.Log
import net.sf.scuba.smartcards.CardServiceException
import org.jmrtd.APDULevelEACCACapable
import org.jmrtd.Util
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.SecurityInfo
import org.jmrtd.protocol.EACCAProtocol
import org.jmrtd.protocol.EACCAProtocol.computeSharedSecret
import org.jmrtd.protocol.EACCAProtocol.getKeyHash
import org.jmrtd.protocol.EACCAProtocol.restartSecureMessaging
import org.jmrtd.protocol.EACCAProtocol.sendPublicKey
import org.jmrtd.protocol.EACCAResult
import org.jmrtd.protocol.SecureMessagingWrapper
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.util.logging.Level
import java.util.logging.Logger
import javax.crypto.interfaces.DHPublicKey

class MediatorCAProtocol(
    private val service: APDULevelEACCACapable?,
    private val wrapper: SecureMessagingWrapper?,
    private val maxTranceiveLength: Int,
    private val shouldCheckMAC: Boolean
) {

    companion object {
        private const val TAG = "EACProtocol"
    }

    fun doMediatorCA(
        keyId: BigInteger?,
        oid: String?,
        publicKeyOID: String?,
        piccPublicKey: PublicKey?,
        pcdKeyPair: KeyPair
    ): EACCAResult? {
        var currentOid = oid
        requireNotNull(piccPublicKey) { "PICC public key is null" }
        var agreementAlg: String? = null
        try {
            agreementAlg = ChipAuthenticationInfo.toKeyAgreementAlgorithm(currentOid)
        } catch (nfe: NumberFormatException) {
            Log.w(TAG, "Unknown object identifier $currentOid")
            currentOid = inferChipAuthenticationOIDfromPublicKeyOID(currentOid)
            agreementAlg = ChipAuthenticationInfo.toKeyAgreementAlgorithm(currentOid)
        }
        require("ECDH" == agreementAlg || "DH" == agreementAlg) { "Unsupported agreement algorithm, expected ECDH or DH, found $agreementAlg" }
        if (currentOid == null) {
            currentOid = inferChipAuthenticationOIDfromPublicKeyOID(publicKeyOID)
        }
        return try {
            val pcdPublicKey = pcdKeyPair.public
            val pcdPrivateKey = pcdKeyPair.private
            sendPublicKey(service, wrapper, currentOid, keyId, pcdPublicKey)
            val keyHash = getKeyHash(agreementAlg, pcdPublicKey)
            val sharedSecret = computeSharedSecret(agreementAlg, piccPublicKey, pcdPrivateKey)
            val caWrapper = restartSecureMessaging(currentOid, sharedSecret, maxTranceiveLength, shouldCheckMAC)
            EACCAResult(keyId, piccPublicKey, keyHash, pcdPublicKey, pcdPrivateKey, caWrapper)
        } catch (e: GeneralSecurityException) {
            throw CardServiceException("Security exception during Chip Authentication", e)
        }
    }

    private fun inferChipAuthenticationOIDfromPublicKeyOID(publicKeyOID: String?): String? {
        if (SecurityInfo.ID_PK_ECDH == publicKeyOID) {
       /*
       * This seems to work for French passports (generation 2013, 2014),
       * but it is best effort.
       */
            Log.w(TAG, "Could not determine ChipAuthentication algorithm, defaulting to id-CA-ECDH-3DES-CBC-CBC")
            return SecurityInfo.ID_CA_ECDH_3DES_CBC_CBC
        } else if (SecurityInfo.ID_PK_DH == publicKeyOID) {
            /*
       * Not tested. Best effort.
       */
            Log.w(TAG, "Could not determine ChipAuthentication algorithm, defaulting to id-CA-DH-3DES-CBC-CBC")
            return SecurityInfo.ID_CA_DH_3DES_CBC_CBC
        } else {
            Log.w(TAG, "No ChipAuthenticationInfo and unsupported ChipAuthenticationPublicKeyInfo public key OID $publicKeyOID")
        }
        return null
    }
}