package com.example.vpassport.viewmodel

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.model.data.DataGroupBundle
import com.example.vpassport.model.data.websocket.CAData
import com.example.vpassport.model.data.websocket.ClientData
import com.example.vpassport.model.data.websocket.PassportAssertion
import com.example.vpassport.model.data.websocket.WebSocketMessage
import com.example.vpassport.model.repo.interfaces.PassportRepository
import com.example.vpassport.util.CryptoManager
import com.example.vpassport.util.Resource
import com.example.vpassport.util.connection.ktor.MediatorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject


@HiltViewModel
class PassportBuilderViewModel @Inject constructor(
    private val passportRepository: PassportRepository,
    private val webSocket: MediatorService
) : ViewModel() {

    companion object {
        private const val TAG = "PassportBuilderViewModel"
    }

    init {
        setInit()
    }

    private lateinit var _instanceCreated: MutableStateFlow<Boolean>

    private val passportBuilder: Passport.Builder = Passport.newBuilder()
    private val _dataGroup: MutableStateFlow<DataGroupBundle?> = MutableStateFlow(null)
    private val _statusState = MutableStateFlow(false)
    private val _statusMessage = MutableStateFlow("")
    private val _documentNumber = MutableStateFlow("")
    private val _dateOfBirth = MutableStateFlow("")
    private val _dateOfExpiry = MutableStateFlow("")
    private val _nfcStatus = MutableStateFlow("Passport not detected")
    private val _tag: MutableStateFlow<Tag?> = MutableStateFlow(null)
    private val _isConnecting = MutableStateFlow(false)

    val statusState = _statusState.asStateFlow()
    val statusMessage = _statusMessage.asStateFlow()
    val instanceCreated = _instanceCreated.asStateFlow()
    val documentNumber = _documentNumber.asStateFlow()
    val dateOfBirth = _dateOfBirth.asStateFlow()
    val dateOfExpiry = _dateOfExpiry.asStateFlow()
    val nfcStatus = _nfcStatus.asStateFlow()
    val isConnecting = _isConnecting.asStateFlow()

    private fun setInit() {
        runBlocking {
            _instanceCreated = MutableStateFlow(!passportRepository.isEmpty())
        }
    }

    fun setDocumentNumber(documentNumber: String) {
        _documentNumber.value = documentNumber
    }

    fun setDateOfBirth(name: String) {
        _dateOfBirth.value = name
    }

    fun setDateOfExpiry(date: String) {
        _dateOfExpiry.value = date
    }

    fun resetErrorState() {
        _statusState.value = false
    }

    fun resetInstanceCreated() {
        _instanceCreated.value = false
    }

    @Throws(IllegalArgumentException::class)
    private fun validatePassportData(builder: Passport.Builder) {
        if (builder.documentNumber.isEmpty()) {
            throw IllegalArgumentException("Doument number cannot be empty")
        }
        if (builder.documentType.isEmpty()) {
            throw IllegalArgumentException("Document type cannot be empty")
        }
        if (builder.issuer.isEmpty()) {
            throw IllegalArgumentException("Issuer cannot be empty")
        }
        if (builder.name.isEmpty()) {
            throw IllegalArgumentException("Name cannot be empty")
        }
        if (builder.nationality.isEmpty()) {
            throw IllegalArgumentException("Nationality cannot be empty")
        }
        if (builder.birthDate.isEmpty()) {
            throw IllegalArgumentException("Birth date cannot be empty")
        }
        if (builder.sex.isEmpty()) {
            throw IllegalArgumentException("Sex cannot be empty")
        }
        if (builder.issueDate.isEmpty()) {
            throw IllegalArgumentException("Issue date cannot be empty")
        }
        if (builder.expiryDate.isEmpty()) {
            throw IllegalArgumentException("Expiry date cannot be empty")
        }
    }


    fun scanPassport(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            _nfcStatus.value = "Passport ready for reading"
            _tag.value = tag
        } else {
            _nfcStatus.value = "Please place your passport on the back of your phone"
        }
    }

    fun createPassport(context: Context) {
        viewModelScope.launch {
            try {
                if (_tag.value == null) {
                    _statusMessage.value = "Passport not ready. Please try again."
                    _statusState.value = true
                    return@launch
                }

                val passportReader = PassportReader(_tag.value!!)
                _nfcStatus.value = "Reading passport"
                _dataGroup.value = passportReader.getDataGroups(
                    documentNumber.value,
                    dateOfBirth.value,
                    dateOfExpiry.value
                )
                val dataGroup = _dataGroup.value!!
                val mrzInfo = dataGroup.dG1File.mrzInfo
                passportBuilder.setDocumentNumber(mrzInfo.documentNumber)
                passportBuilder.setDocumentType(mrzInfo.documentCode)
                passportBuilder.setIssuer(mrzInfo.issuingState)
                passportBuilder.setName(mrzInfo.secondaryIdentifier + " " + mrzInfo.primaryIdentifier)
                passportBuilder.setNationality(mrzInfo.nationality)
                passportBuilder.setBirthDate(mrzInfo.dateOfBirth)
                passportBuilder.setSex(mrzInfo.gender.toString())
                passportBuilder.setIssueDate(mrzInfo.issuingState)
                passportBuilder.setExpiryDate(mrzInfo.dateOfExpiry)
                validatePassportData(passportBuilder)

                _nfcStatus.value =
                    "Passport read successfully. Initiating communication to mediator..."
                getMediatorAssertion(passportReader)

            } catch (e: Exception) {
                _statusMessage.value = e.message.toString()
                _statusState.value = true
                return@launch
            }
        }

    }

    private fun getMediatorAssertion(passportReader: PassportReader) {
        viewModelScope.launch {
            _nfcStatus.value = "Connecting to mediator"

            val dG14File = _dataGroup.value!!.dG14File.encoded
            val dG14Base64 = Base64.getEncoder().encodeToString(dG14File)
            val clientCAData = CAData(dG14Base64)

            when (val result = webSocket.connect(clientCAData)) {
                is Resource.Success -> {
                    _nfcStatus.value = "connected to mediator"
                    webSocket.getMessage()
                        .onEach { message ->
                            _nfcStatus.value = "Message Received ${message.event}"
                            when (message.event) {
                                WebSocketMessage.EVENT_SIGNATURE_CHALLENGE -> {
                                    try {
                                        val pcdKeyPairBytes =
                                            Base64.getDecoder().decode(message.data)
                                        val pcdKeyPair = deserializeKeyPair(pcdKeyPairBytes)
                                        val challenge = passportReader.livelinessTest(
                                            pcdKeyPair,
                                            _dataGroup.value!!
                                        )

                                        val clientData = clientData(_dataGroup.value!!, challenge!!)
                                        webSocket.sendMessage(
                                            WebSocketMessage(
                                                WebSocketMessage.EVENT_CHALLENGE_RESULT,
                                                Json.encodeToString<ClientData>(clientData)
                                            )
                                        )
                                    } catch (e: Exception) {
                                        val clientData =
                                            clientData(_dataGroup.value!!, ByteArray(10))
                                        webSocket.sendMessage(
                                            WebSocketMessage(
                                                WebSocketMessage.EVENT_CHALLENGE_RESULT,
                                                Json.encodeToString<ClientData>(clientData)
                                            )
                                        )
                                        Log.e(TAG, e.toString())
                                    }


                                }

                                WebSocketMessage.EVENT_SIGNATURE_RESULT -> {
                                    val signature =
                                        Json.decodeFromString<PassportAssertion>(message.data)
                                    passportBuilder.setDocumentNumberSignature(signature.documentNumber)
                                    passportBuilder.setDocumentTypeSignature(signature.documentType);
                                    passportBuilder.setIssuerSignature(signature.documentType);
                                    passportBuilder.setNameSignature(signature.name);
                                    passportBuilder.setNationalitySignature(signature.nationality);
                                    passportBuilder.setBirthDateSignature(signature.birthDate);
                                    passportBuilder.setSexSignature(signature.sex);
                                    passportBuilder.setIssueDateSignature(signature.issueDate);
                                    passportBuilder.setExpiryDateSignature(signature.expiryDate);
                                    Log.i(TAG, "Signature received from mediator")
                                    _nfcStatus.value =
                                        "Signature received. Generating passport instance."
                                    passportRepository.setPassport(passportBuilder.build())
                                    webSocket.close()
                                    _instanceCreated.value = true
                                }

                                else -> {
                                    webSocket.close()
                                }
                            }
                        }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    _statusMessage.value =
                        result.message ?: "Unknown error when connecting to mediator"
                    _statusState.value = true
                }
            }
        }
    }

    private fun clientData(
        dataGroupBundle: DataGroupBundle,
        challenge: ByteArray
    ): ClientData {
        // Prepare data to send
        val sodFile = dataGroupBundle.sodFile.encoded
        val dG14File = dataGroupBundle.dG14File.encoded
        val publicKey = CryptoManager().getPublicKey().encoded

        // Convert data to Base64
        val sodBase64 = Base64.getEncoder().encodeToString(sodFile)
        val dG14Base64 = Base64.getEncoder().encodeToString(dG14File)
        val publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey)
        val challengeBase64 =
            Base64.getEncoder().encodeToString(challenge)

        val hashedDocumentNumber = hash(passportBuilder.documentNumber)
        val hashedDocumentType = hash(passportBuilder.documentType)
        val hashedIssuer = hash(passportBuilder.issuer)
        val hashedName = hash(passportBuilder.name)
        val hashedNationality = hash(passportBuilder.nationality)
        val hashedBirthDate = hash(passportBuilder.birthDate)
        val hashedSex = hash(passportBuilder.sex)
        val hashedIssueDate = hash(passportBuilder.issueDate)
        val hashedExpiryDate = hash(passportBuilder.expiryDate)

        val dataHashes = PassportAssertion(
            documentNumber = hashedDocumentNumber,
            documentType = hashedDocumentType,
            issuer = hashedIssuer,
            name = hashedName,
            nationality = hashedNationality,
            birthDate = hashedBirthDate,
            sex = hashedSex,
            issueDate = hashedIssueDate,
            expiryDate = hashedExpiryDate
        )

        return ClientData(
            sodBase64,
            dG14Base64,
            publicKeyBase64,
            challengeBase64,
            dataHashes
        )
    }

    private fun hash(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val fieldValueBytes = value.toByteArray(StandardCharsets.UTF_8)
        digest.update(fieldValueBytes)
        val hashBytes = digest.digest()
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    private fun deserializeKeyPair(serializedKeyPair: ByteArray): KeyPair {
        ByteArrayInputStream(serializedKeyPair).use { byteArrayInputStream ->
            ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
                return objectInputStream.readObject() as KeyPair
            }
        }
    }


}