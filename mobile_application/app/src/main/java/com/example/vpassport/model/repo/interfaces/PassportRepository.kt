package com.example.vpassport.model.repo.interfaces

import com.example.vpassport.Passport
import kotlinx.coroutines.flow.Flow

interface PassportRepository {
    suspend fun isEmpty(): Boolean
    suspend fun getPassport(): Flow<Passport>
    suspend fun setPassport(passport: Passport)
    suspend fun resetPassport()
}