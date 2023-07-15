package com.example.vpassport.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vpassport.Passport
import com.example.vpassport.model.repo.interfaces.PassportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val passportRepository: PassportRepository
) : ViewModel() {

    init {
        getPassport()
    }

    private lateinit var  _passport: LiveData<Passport>
    val passport = _passport

    private fun getPassport() {
        viewModelScope.launch {
            _passport = passportRepository.getPassport().asLiveData()
        }
    }

    fun resetPassport() = viewModelScope.launch {
        passportRepository.resetPassport()
    }

}