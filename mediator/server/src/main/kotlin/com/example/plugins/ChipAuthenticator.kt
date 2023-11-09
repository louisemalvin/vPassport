package com.example.plugins

import com.example.data.websocket.CAData
import net.sf.scuba.smartcards.CardFileInputStream
import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base
import org.jmrtd.Util
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.SecurityInfo
import org.jmrtd.lds.icao.DG14File
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.interfaces.DHPublicKey

class ChipAuthenticator(
    private val clientCAData: CAData
) {

    fun getKeyPair(): ByteArray? {

        val dG14File = decodeDG14(clientCAData)

        for (securityInfo in dG14File.securityInfos) {
            if (securityInfo is ChipAuthenticationPublicKeyInfo) {
                return generateKeyPair(securityInfo.objectIdentifier, securityInfo.subjectPublicKey)
            }
        }
        return null
    }

    private fun decodeDG14(clientCAData: CAData): DG14File {
        val dG14FileBytes = Base64.getDecoder().decode(clientCAData.dg14Base64)
        val inputStream = ByteArrayInputStream(dG14FileBytes)
        // Create a DG14File object from the CardFileInputStream.
        return DG14File(inputStream)
    }

    private fun generateKeyPair(oid: String, piccPublicKey: PublicKey): ByteArray {
        var currentOid: String? = oid
        var agreementAlg: String? = null
        try {
            agreementAlg = ChipAuthenticationInfo.toKeyAgreementAlgorithm(currentOid)
        } catch (nfe: NumberFormatException) {
            LoggerFactory.getLogger("ktor.application").warn("Unknown object identifier $currentOid")
            currentOid = inferChipAuthenticationOIDfromPublicKeyOID(currentOid)
            agreementAlg = ChipAuthenticationInfo.toKeyAgreementAlgorithm(currentOid)
        }
        var params: AlgorithmParameterSpec? = null
        if ("DH" == agreementAlg) {
            val piccDHPublicKey = piccPublicKey as DHPublicKey
            params = piccDHPublicKey.params
        } else if ("ECDH" == agreementAlg) {
            val piccECPublicKey = piccPublicKey as ECPublicKey
            params = piccECPublicKey.params
        }

        /* Generate the inspection system's ephemeral key pair. */
        val keyPairGenerator = KeyPairGenerator.getInstance(agreementAlg, Util.getBouncyCastleProvider())
        keyPairGenerator.initialize(params)
        val keyPair = keyPairGenerator.generateKeyPair()
        return serializeKeyPair(keyPair)
    }

    private fun serializeKeyPair(keyPair: KeyPair): ByteArray {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
                objectOutputStream.writeObject(keyPair)
                return byteArrayOutputStream.toByteArray()
            }
        }
    }

    private fun inferChipAuthenticationOIDfromPublicKeyOID(publicKeyOID: String?): String? {
        if (SecurityInfo.ID_PK_ECDH == publicKeyOID || "id-PK-ECDH" == publicKeyOID) {
            /*
            * This seems to work for French passports (generation 2013, 2014),
            * but it is best effort.
            */
            LoggerFactory.getLogger("ktor.application").warn("Could not determine ChipAuthentication algorithm, defaulting to id-CA-ECDH-3DES-CBC-CBC")
            return SecurityInfo.ID_CA_ECDH_3DES_CBC_CBC
        } else if (SecurityInfo.ID_PK_DH == publicKeyOID) {
            /*
       * Not tested. Best effort.
       */
            LoggerFactory.getLogger("ktor.application").warn("Could not determine ChipAuthentication algorithm, defaulting to id-CA-DH-3DES-CBC-CBC")
            return SecurityInfo.ID_CA_DH_3DES_CBC_CBC
        } else {
            LoggerFactory.getLogger("ktor.application").warn("No ChipAuthenticationInfo and unsupported ChipAuthenticationPublicKeyInfo public key OID $publicKeyOID")
        }
        return null
    }
}