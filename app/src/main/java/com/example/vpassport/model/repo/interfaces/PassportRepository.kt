package com.example.vpassport.model.repo.interfaces

import androidx.lifecycle.LiveData
import com.example.vpassport.Passport

interface PassportRepository {
    suspend fun getPassport(): LiveData<Passport>
    suspend fun setPassport(passport: Passport)
    suspend fun resetPassport()
}