package com.example.vpassport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.data.repo.PassportRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class PassportViewModel @Inject constructor(
    private val passportRepository: PassportRepository
) : ViewModel() {

    private lateinit var _passport: LiveData<Passport>
    val passport: LiveData<Passport> = _passport

    private fun getPassport() = viewModelScope.launch {
        _passport = passportRepository.getPassport()
    }

    fun setPassport(passport: Passport) = viewModelScope.launch {
        passportRepository.setPassport(passport)
    }

    fun resetPassport() = viewModelScope.launch {
        passportRepository.resetPassport()
    }

}