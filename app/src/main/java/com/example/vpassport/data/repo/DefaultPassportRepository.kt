package com.example.vpassport.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.vpassport.Passport

class DefaultPassportRepository(
    private val context: Context,
    private val passportDataStore: DataStore<Passport>
) : PassportRepository {
    override suspend fun getPassport(): LiveData<Passport> {
        return passportDataStore.data.asLiveData()
    }

    override suspend fun setPassport(passport: Passport) {
        passportDataStore.updateData { currentPassport ->
            currentPassport.toBuilder()
                .setDocNum(passport.docNum)
                .setDocType(passport.docType)
                .setIssuer(passport.issuer)
                .setName(passport.name)
                .setNationality(passport.nationality)
                .setBirthDate(passport.birthDate)
                .setSex(passport.sex)
                .setIssueDate(passport.issueDate)
                .setExpiryDate(passport.expiryDate)
                .build()
        }
    }

    override suspend fun resetPassport() {
        passportDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}