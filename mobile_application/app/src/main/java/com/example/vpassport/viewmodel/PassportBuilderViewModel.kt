package com.example.vpassport.viewmodel

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.interfaces.PassportRepository
import com.google.protobuf.ByteString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jmrtd.lds.iso19794.FaceImageInfo
import java.io.DataInputStream
import javax.inject.Inject


@HiltViewModel
class PassportBuilderViewModel @Inject constructor(
    private val passportRepository: PassportRepository
) : ViewModel() {

    init {
        setInit()
    }

    private val passportBuilder: Passport.Builder = Passport.newBuilder()
    private lateinit var _errorState: MutableStateFlow<Boolean>
    private lateinit var _instanceCreated: MutableStateFlow<Boolean>
    private lateinit var _errorMessage: MutableStateFlow<String>
    private lateinit var _documentNumber: MutableStateFlow<String>
    private lateinit var _dateOfBirth: MutableStateFlow<String>
    private lateinit var _dateOfExpiry: MutableStateFlow<String>
    private lateinit var _nfcStatus: MutableStateFlow<String>
    private lateinit var _tag: MutableStateFlow<Tag?>

    val errorState = _errorState
    val errorMessage = _errorMessage
    val instanceCreated = _instanceCreated
    val documentNumber = _documentNumber
    val dateOfBirth = _dateOfBirth
    val dateOfExpiry = _dateOfExpiry
    val nfcStatus = _nfcStatus

    private fun setInit() {
        _tag = MutableStateFlow(null)
        _errorState = MutableStateFlow(false)
        _errorMessage = MutableStateFlow("")
        _documentNumber = MutableStateFlow("X609021")
        _dateOfBirth = MutableStateFlow("001012")
        _dateOfExpiry = MutableStateFlow("230713")
        _nfcStatus = MutableStateFlow("Passport not detected")
        runBlocking {
            _instanceCreated = MutableStateFlow(!passportRepository.isEmpty())
        }
    }

    fun setNfcStatus(status: String) {
        _nfcStatus.value = status
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
        _errorState.value = false
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
        if (builder.photo.isEmpty()) {
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
        val passportReader = PassportReader()
        viewModelScope.launch {
            try {
                if (_tag.value == null) {
                    _errorMessage.value = "Passport not ready. Please try again."
                    _errorState.value = true
                    return@launch
                }
                val dataGroup =
                    passportReader.getDataGroup(_tag.value!!, documentNumber.value, dateOfBirth.value, dateOfExpiry.value)
                val mrzInfo = dataGroup.dG1File.mrzInfo

                val allFaceImageInfo: MutableList<FaceImageInfo> = ArrayList()
                dataGroup.dG2File.faceInfos.forEach {
                    allFaceImageInfo.addAll(it.faceImageInfos)
                }
                var buffer: ByteArray? = null
                if (allFaceImageInfo.isNotEmpty()) {
                    val faceImageInfo = allFaceImageInfo.first()
                    val length = faceImageInfo.imageLength
                    val inputStream = DataInputStream(faceImageInfo.imageInputStream)
                    buffer = ByteArray(length)
                    inputStream.readFully(buffer, 0, length)
                }
                passportBuilder.setDocumentNumber(mrzInfo.documentNumber)
                passportBuilder.setDocumentType(mrzInfo.documentCode)
                passportBuilder.setIssuer(mrzInfo.issuingState)
                passportBuilder.setName(mrzInfo.secondaryIdentifier + " " + mrzInfo.primaryIdentifier)
                passportBuilder.setNationality(mrzInfo.nationality)
                passportBuilder.setBirthDate(mrzInfo.dateOfBirth)
                passportBuilder.setSex(mrzInfo.gender.toString())
                passportBuilder.setIssueDate(mrzInfo.issuingState)
                passportBuilder.setExpiryDate(mrzInfo.dateOfExpiry)
                passportBuilder.setPhoto(ByteString.copyFrom(buffer))
                validatePassportData(passportBuilder)

            } catch (e: Exception) {
                _errorMessage.value = "Failed in: " + e.message.toString()
                _errorState.value = true
                return@launch
            }
            passportRepository.setPassport(passportBuilder.build())
            _instanceCreated.value = true

        }

    }

//        fun defaultPassport(name: String, documentNumber: String, dateOfBirth: String) {
//        passportBuilder.setName(name)
//        passportBuilder.setDocumentNumber(documentNumber)
//        passportBuilder.setDocumentType("Very Cool Passport")
//        passportBuilder.setIssuer("Republic of Earth")
//        passportBuilder.setNationality("Earthian")
//        passportBuilder.setBirthDate(dateOfBirth)
//        passportBuilder.setSex("Male")
//        passportBuilder.setExpiryDate("2028-12-31")
//        passportBuilder.setIssueDate("1998-03-22")
//
//        viewModelScope.launch {
//            try {
//                validatePassportData(passportBuilder)
//            } catch (e: IllegalArgumentException) {
//                _errorMessage.value = e.message.toString()
//                _errorState.value = true
//                return@launch
//            }
//            passportRepository.setPassport(passportBuilder.build())
//            _instanceCreated.value = true
//        }
//    }
}