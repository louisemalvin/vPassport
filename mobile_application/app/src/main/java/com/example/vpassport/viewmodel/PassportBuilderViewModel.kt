package com.example.vpassport.viewmodel

import android.nfc.tech.IsoDep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.interfaces.PassportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.jvm.Throws

@HiltViewModel
class PassportBuilderViewModel @Inject constructor(
    private val passportRepository: PassportRepository
) : ViewModel() {

    init {
        setInit()
    }

    private val passportBuilder: Passport.Builder = Passport.newBuilder()
    private lateinit var _errorState : MutableStateFlow<Boolean>
    private lateinit var _instanceCreated : MutableStateFlow<Boolean>
    private lateinit var _errorMessage : MutableStateFlow<String>
    val errorState = _errorState
    val errorMessage = _errorMessage
    val instanceCreated = _instanceCreated

    private fun setInit() {
        _errorState = MutableStateFlow(false)
        _errorMessage = MutableStateFlow("")
        runBlocking {
            _instanceCreated = MutableStateFlow(!passportRepository.isEmpty())
        }
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
    }


    fun defaultPassport(name: String, documentNumber: String, dateOfBirth: String) {
        passportBuilder.setName(name)
        passportBuilder.setDocumentNumber(documentNumber)
        passportBuilder.setDocumentType("Very Cool Passport")
        passportBuilder.setIssuer("Republic of Earth")
        passportBuilder.setNationality("Earthian")
        passportBuilder.setBirthDate(dateOfBirth)
        passportBuilder.setSex("Male")
        passportBuilder.setExpiryDate("2028-12-31")
        passportBuilder.setIssueDate("1998-03-22")

        viewModelScope.launch {
            try {
                validatePassportData(passportBuilder)
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message.toString()
                _errorState.value = true
                return@launch
            }
            passportRepository.setPassport(passportBuilder.build())
            _instanceCreated.value = true
        }
    }

    fun scanPassport(isoDep: IsoDep, documentNumber: String, dateOfBirth: String, dateOfExpiry: String) {
        val passportReader = PassportReader()
        viewModelScope.launch {
            try {
                val dataGroup =
                    passportReader.getDataGroup(isoDep, documentNumber, dateOfBirth, dateOfExpiry)
                val mrzInfo = dataGroup.dG1File.mrzInfo
                mrzInfo.documentCode
                passportBuilder.setDocumentNumber(mrzInfo.documentNumber)
                passportBuilder.setDocumentType(mrzInfo.documentCode)
                passportBuilder.setIssuer(mrzInfo.issuingState)
                passportBuilder.setName(mrzInfo.secondaryIdentifier + " " + mrzInfo.primaryIdentifier)
                passportBuilder.setNationality(mrzInfo.nationality)
                passportBuilder.setBirthDate(mrzInfo.dateOfBirth)
                passportBuilder.setSex(mrzInfo.gender.toString())
                passportBuilder.setIssueDate(mrzInfo.issuingState)
                passportBuilder.setExpiryDate(mrzInfo.dateOfExpiry)

            } catch (e: Exception) {
                // handle exceptions
            }

        }
    }
}