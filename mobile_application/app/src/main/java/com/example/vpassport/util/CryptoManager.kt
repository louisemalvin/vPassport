package com.example.vpassport.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

        private const val KEY_ALIAS_SECRET = "secret"
        private const val KEY_ALIAS_SIGNATURE = "signature"
        private const val KEY_SIZE = 2048
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private lateinit var encryptCipher: Cipher

    /**
     * Initializes decryption cipher with a key and a set of algorithm parameters.
     */
    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    /**
     * Returns secret key for encryption and decryption, calls createKey() if no key has
     * been created before
     */
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS_SECRET, null)
                as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    /**
     * Creates secret key for the first time.
     */
    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS_SECRET, KeyProperties.PURPOSE_ENCRYPT
                            or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(BLOCK_MODE).setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false).setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    /**
     * Creates private-public key pair for signature purposes.
     */
    private fun createKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS_SIGNATURE, KeyProperties.PURPOSE_SIGN
                        or KeyProperties.PURPOSE_VERIFY
            ).setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(KEY_SIZE)
                .build()
        )
        return keyPairGenerator.generateKeyPair()
    }

    /**
     * Returns private key of the KeyPair.
     */
    private fun getPrivateKey(): PrivateKey {
        val existingKey = keyStore
            .getEntry(KEY_ALIAS_SIGNATURE, null) as? KeyStore.PrivateKeyEntry
        return existingKey?.privateKey ?: createKeyPair().private
    }

    /**
     * Returns public key of the KeyPair.
     */
    fun getPublicKey(): PublicKey {
        val existingKey = keyStore
            .getEntry(KEY_ALIAS_SIGNATURE, null) as? KeyStore.PrivateKeyEntry
        return existingKey?.certificate?.publicKey ?: createKeyPair().public
    }

    /**
     * Encrypts data given with the key stored in Android KeyStore.
     *
     * @param bytes data to encrypt.
     * @param outputStream OutputStream instance.
     */
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }
        val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    /**
     * Decrypts data given with the key stored in Android KeyStore.
     *
     * @param inputStream data to decrypt.
     */
    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }


    /**
     * Signs message given and returns in Base64 format.
     *
     * @param message message to be signed.
     */
    fun signMessage(message: String): String {
        val privateKey = getPrivateKey()
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(message.toByteArray())
        val signatureBytes = signature.sign()
        return Base64.getEncoder().encodeToString(signatureBytes)
    }
}