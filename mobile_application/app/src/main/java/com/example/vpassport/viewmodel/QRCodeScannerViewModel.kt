import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QRCodeScannerViewModel : ViewModel() {
    init {
        setInit()
    }

    private lateinit var _scannedData : MutableLiveData<String>
    private lateinit var _isDone : MutableStateFlow<Boolean>
    val scannedQRCodeData = _scannedData
    val isDone = _isDone.asStateFlow()

    private fun setInit() {
        _scannedData = MutableLiveData<String>("")
        _isDone = MutableStateFlow(false)
    }

    fun resetIsDone() {
        _isDone.value = false
    }

    fun scanQRCode(context: Context) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                _scannedData.value = barcode.rawValue
                _isDone.value = true
            }
    }
}