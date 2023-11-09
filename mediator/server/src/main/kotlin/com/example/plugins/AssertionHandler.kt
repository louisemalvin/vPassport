package com.example.plugins

import com.example.data.websocket.PassportAssertion
import com.example.vpassport.model.data.websocket.ClientData

import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.Signature
import java.util.Base64

object AssertionHandler {

    fun createAssertion(clientData: ClientData): PassportAssertion {
        val dataHashes = clientData.passportAssertion
        return PassportAssertion(
            documentNumber = createSignature(dataHashes.documentNumber, clientData.publicKey),
            documentType = createSignature(dataHashes.documentType, clientData.publicKey),
            issuer = createSignature(dataHashes.issuer, clientData.publicKey),
            name = createSignature(dataHashes.name, clientData.publicKey),
            nationality = createSignature(dataHashes.nationality, clientData.publicKey),
            birthDate = createSignature(dataHashes.birthDate, clientData.publicKey),
            sex = createSignature(dataHashes.sex, clientData.publicKey),
            issueDate = createSignature(dataHashes.issueDate, clientData.publicKey),
            expiryDate = createSignature(dataHashes.expiryDate, clientData.publicKey)
        )
    }

    /**
     * Generates user attribute and device public key signature.
     *
     * @param userAttribute attribute to be signed.
     * @param clientPublicKey binding key to be signed.
     * @return Base64 representation of the signature.
     */
    private fun createSignature(userAttribute: String, clientPublicKey: String): String {
        val message = "$userAttribute|$clientPublicKey"
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        val keyStore = CryptoManager.getCertificateKeyStore()
        val privateKeyPassword = CryptoManager.getPrivateKeyPassword()
        val keyAlias = CryptoManager.getKeyAlias()
        val privateKey: PrivateKey = keyStore.getKey(keyAlias, privateKeyPassword) as PrivateKey
        val signature = Signature.getInstance("SHA256withRSA")

        signature.initSign(privateKey)
        signature.update(messageBytes)
        val digitalSignatureBytes = signature.sign()
        return Base64.getEncoder().encodeToString(digitalSignatureBytes)
    }

}
