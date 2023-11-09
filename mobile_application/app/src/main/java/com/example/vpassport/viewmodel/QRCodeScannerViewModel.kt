package com.example.vpassport.viewmodel

import android.content.Context
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.model.data.History
import com.example.vpassport.model.data.websocket.QRData
import com.example.vpassport.model.data.websocket.AuthData
import com.example.vpassport.model.data.websocket.WebSocketMessage
import com.example.vpassport.model.repo.interfaces.HistoryRepository
import com.example.vpassport.model.repo.interfaces.PassportRepository
import com.example.vpassport.util.CryptoManager
import com.example.vpassport.util.Resource
import com.example.vpassport.util.connection.ktor.AuthenticationService
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.net.URI
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class QRCodeScannerViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val passportRepository: PassportRepository,
    private val webSocket: AuthenticationService
) : ViewModel() {

    companion object {
        private const val TAG = "QRCodeScannerViewModel"
    }

    init {
        setInit()
    }

    private lateinit var _qrData: MutableLiveData<QRData?>
    private lateinit var _isDone: MutableStateFlow<Boolean>
    val qrData = _qrData
    val isDone = _isDone.asStateFlow()

    private fun setInit() {
        _qrData = MutableLiveData<QRData?>(null)
        _isDone = MutableStateFlow(false)
    }

    fun resetIsDone() {
        _isDone.value = false
    }

    fun scanQRCode(context: Context) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                _qrData.value = Gson().fromJson(barcode.rawValue, QRData::class.java)
                _isDone.value = true
            }
    }

    fun processQRCode(historyViewModel: HistoryViewModel) {
        Log.i(TAG, "Verifying process started")
        viewModelScope.launch {
            val qr = _qrData.value!!
            val authData = AuthData(qr.wsId)
            Log.i(TAG, "Connecting to ${qr.apiUrl}")
            when (val result = webSocket.connect(authData, qr.apiUrl)) {
                is Resource.Success -> {
                    val history = History(
                        site = getBaseUrl(qr.apiUrl),
                        isAllowed = true,
                        date = LocalDateTime.now()
                    )
                    historyViewModel.addHistory(history)
                    Log.i(TAG, "Websocket connected.")
                    webSocket.getMessage()
                        .onEach { message ->
                            when (message.event) {
                                WebSocketMessage.EVENT_SIGNATURE_CHALLENGE -> {
                                    val signature = CryptoManager().signMessage(message.data)
                                    webSocket.sendMessage(
                                        WebSocketMessage(
                                            WebSocketMessage.EVENT_SIGNATURE_CHALLENGE,
                                            signature
                                        )
                                    )
                                }

                                WebSocketMessage.EVENT_SIGNATURE_RESULT -> {
                                    val history = History(
                                        site = getBaseUrl(qr.apiUrl),
                                        isAllowed = true,
                                        date = LocalDateTime.now()
                                    )
                                    historyRepository.addHistory(history)
                                    webSocket.close()
                                }

                                else -> {}
                            }
                        }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    Log.i(TAG, "Websocket not found.")
                }
            }
        }
    }

    private suspend fun getAuthData(
        qr: QRData,
    ): AuthData {
        val authData = AuthData(qr.wsId)
        val passportFlow = passportRepository.getPassport()
        passportFlow.collect { passport ->
            for (attribute in qr.attributes) {
                when (attribute) {
                    "documentNumber" -> {
                        authData.documentNumber = passport.documentNumber
                        authData.documentNumberSignature = passport.documentNumberSignature
                    }

                    "documentType" -> {
                        authData.documentType = passport.documentType
                        authData.documentTypeSignature = passport.documentTypeSignature
                    }

                    "issuer" -> {
                        authData.issuer = passport.issuer
                        authData.issuerSignature = passport.issuerSignature
                    }

                    "name" -> {
                        authData.name = passport.name
                        authData.nameSignature = passport.nameSignature
                    }

                    "nationality" -> {
                        authData.nationality = passport.nationality
                        authData.nationalitySignature = passport.nationalitySignature
                    }

                    "birthDate" -> {
                        authData.birthDate = passport.birthDate
                        authData.birthDateSignature = passport.birthDateSignature
                    }

                    "sex" -> {
                        authData.sex = passport.sex
                        authData.sexSignature = passport.sexSignature
                    }

                    "issueDate" -> {
                        authData.issueDate = passport.issueDate
                        authData.issueDateSignature = passport.issueDateSignature
                    }

                    "expiryDate" -> {
                        authData.expiryDate = passport.expiryDate
                        authData.expiryDateSignature = passport.expiryDateSignature
                    }
                }
            }
        }
        return authData
    }

    private fun addHistory(history: History) {
        viewModelScope.launch {
            historyRepository.addHistory(history)
        }
    }

    fun getBaseUrl(fullUrl: String): String {
        val uri = URI(fullUrl)
        return uri.host
    }


}