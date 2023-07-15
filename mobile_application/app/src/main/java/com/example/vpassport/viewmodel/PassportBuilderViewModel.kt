package com.example.vpassport.viewmodel

import android.nfc.tech.IsoDep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.interfaces.PassportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassportBuilderViewModel @Inject constructor(
    private val passportRepository: PassportRepository
): ViewModel() {
    private val passportBuilder: Passport.Builder = Passport.newBuilder()

    fun setDocumentNumber(docNum: String) {
        passportBuilder.setDocNum(docNum)
    }

    fun setDocumentType(docType: String) {
        passportBuilder.setDocType(docType)
    }

    fun setIssuer(issuer: String) {
        passportBuilder.setIssuer(issuer)
    }

    fun setName(name: String) {
        passportBuilder.setName(name)
    }

    fun setNationality(nationality: String) {
        passportBuilder.setNationality(nationality)
    }

    fun setBirthDate(birthDate: String) {
        passportBuilder.setBirthDate(birthDate)
    }

    fun setSex(sex: String) {
        passportBuilder.setSex(sex)
    }

    fun setIssueDate(issueDate: String) {
        passportBuilder.setIssueDate(issueDate)
    }

    fun setExpiryDate(expiryDate: String) {
        passportBuilder.setExpiryDate(expiryDate)
    }

    fun buildPassport(): Passport {
        validatePassportData(passportBuilder)
        return passportBuilder.build()
    }

    private fun validatePassportData(builder: Passport.Builder) {
        // Perform validation logic here
        if (builder.docNum.isEmpty()) {
            throw IllegalStateException("DocNum cannot be empty")
        }
        if (builder.docType.isEmpty()) {
            throw IllegalStateException("DocType cannot be empty")
        }
        if (builder.issuer.isEmpty()) {
            throw IllegalStateException("Issuer cannot be empty")
        }
        if (builder.name.isEmpty()) {
            throw IllegalStateException("Name cannot be empty")
        }
        if (builder.nationality.isEmpty()) {
            throw IllegalStateException("Nationality cannot be empty")
        }
        if (builder.birthDate.isEmpty()) {
            throw IllegalStateException("BirthDate cannot be empty")
        }
        if (builder.sex.isEmpty()) {
            throw IllegalStateException("Sex cannot be empty")
        }
        if (builder.issueDate.isEmpty()) {
            throw IllegalStateException("IssueDate cannot be empty")
        }
        if (builder.expiryDate.isEmpty()) {
            throw IllegalStateException("ExpiryDate cannot be empty")
        }
    }

    fun defaultPassport(name: String, docNum: String, dateOfBirth: String) {
        setName(name)
        setDocumentNumber(docNum)
        setDocumentType("Passport")
        setIssuer("Government")
        setNationality("Country")
        setBirthDate(dateOfBirth)
        setSex("Male")
        setIssueDate("2023-01-01")
        setExpiryDate("2028-12-31")

        viewModelScope.launch {
            passportRepository.setPassport(buildPassport())
        }
    }

    fun scanPassport(isoDep: IsoDep, docNum: String, dateOfBirth: String, dateOfExpiry: String) {
        val passportReader = PassportReader()
        viewModelScope.launch {
            try {
                val dataGroup =
                    passportReader.getDataGroup(isoDep, docNum, dateOfBirth, dateOfExpiry)
                val mrzInfo = dataGroup.dG1File.mrzInfo
                mrzInfo.documentCode
                setDocumentNumber(mrzInfo.documentNumber)
                setDocumentType(mrzInfo.documentCode)
                setIssuer(mrzInfo.issuingState)
                setName(mrzInfo.secondaryIdentifier + " " + mrzInfo.primaryIdentifier)
                setNationality(mrzInfo.nationality)
                setBirthDate(mrzInfo.dateOfBirth)
                setSex(mrzInfo.gender.toString())
                setIssueDate(mrzInfo.issuingState)
                setExpiryDate(mrzInfo.dateOfExpiry)

            } catch (e: Exception) {
                // handle exceptions
            }

        }
    }
}