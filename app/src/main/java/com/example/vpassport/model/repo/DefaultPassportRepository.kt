package com.example.vpassport.model.repo

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.interfaces.PassportRepository
import kotlinx.coroutines.flow.Flow

class DefaultPassportRepository(
    private val context: Context,
    private val passportDataStore: DataStore<Passport>
) : PassportRepository {
    override suspend fun getPassport(): Flow<Passport> {
        return passportDataStore.data
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