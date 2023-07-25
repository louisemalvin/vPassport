package com.example.vpassport.view.dialogs

import QRCodeScannerViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.vpassport.model.data.History
import com.example.vpassport.viewmodel.HistoryViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    context: Context,
    historyViewModel: HistoryViewModel,
    qrCodeScannerViewModel: QRCodeScannerViewModel,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { qrCodeScannerViewModel.resetIsDone() }) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(20.dp)

        ) {

            val text by qrCodeScannerViewModel.qrData.observeAsState()
            Text(text = "Confirm to share data with ${qrCodeScannerViewModel.getBaseUrl(text?.apiUrl!!)}?")
            Button(onClick = {
                qrCodeScannerViewModel.processQRCode(historyViewModel)
                qrCodeScannerViewModel.resetIsDone()
            }) {
                Text(text = "Yes.")
            }
        }
    }
}
