import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpassport.model.data.History
import com.example.vpassport.model.data.QRData
import com.example.vpassport.model.data.UserData
import com.example.vpassport.viewmodel.HistoryViewModel
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.time.LocalDateTime

class QRCodeScannerViewModel : ViewModel() {
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
        viewModelScope.launch {
            try {
                val qr = _qrData.value!!
                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(getBaseUrl(qr.apiUrl))
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                val response = withContext(Dispatchers.IO) {
                    apiService.postUserAttributes(qr.apiUrl, UserData(qr.wsId, "123456"))
                }

                if (response.isSuccessful) {
                    // Handle successful response (200 OK)
                    val history = History(
                        site = getBaseUrl(qr.apiUrl),
                        isAllowed = true,
                        date = LocalDateTime.now()
                    )
                    historyViewModel.addHistory(history)

                    Log.d(
                        "QRCodeScannerViewModel",
                        "API call successful. Response: ${response.body()}"
                    )
                } else if (response.code() == 401) {
                    // Handle unauthorized response (401 Unauthorized)
                    val history = History(
                        site = getBaseUrl(qr.apiUrl),
                        isAllowed = false,
                        date = LocalDateTime.now()
                    )
                    historyViewModel.addHistory(history)

                    Log.d(
                        "QRCodeScannerViewModel",
                        "API call unauthorized. Response: ${response.body()}"
                    )
                } else {
                    // Handle other error responses (if needed)
                    Log.e(
                        "QRCodeScannerViewModel",
                        "API call failed. Error code: ${response.code()}"
                    )
                    // ...
                }
            } catch (e: Exception) {
                // Handle the error (e.g., network error, HTTP exception)
                Log.e("QRCodeScannerViewModel", "Error during API call: ${e.message}")
                // ...
            }
        }
    }

    fun getBaseUrl(fullUrl: String): String {
        val url = URL(fullUrl)
        return "${url.protocol}://${url.host}"
    }


}