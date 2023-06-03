@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.vpassport.data.model.Passport
import com.example.vpassport.data.model.history.HistoryDatabase
import com.example.vpassport.ui.screens.HomeScreen
import com.example.vpassport.ui.theme.VPassportTheme
import com.example.vpassport.ui.viewmodel.HistoryViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            HistoryDatabase::class.java,
            "history.db"
        ).build()
    }

    private val viewModel by viewModels<HistoryViewModel>(
        factoryProducer = {

            object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return HistoryViewModel(db.dao) as T
            }
        }}
    )

    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VPassportTheme {
                val state by viewModel.state.collectAsState()
                val defaultPass = Passport(
                    docType = "Passport",
                    issuer = "ABC Country",
                    name = "John Smith",
                    docNum = "A1234567",
                    nationality = "Country A",
                    birthDate = "1990-01-01",
                    sex = "Male",
                    issueDate = "2022-01-01",
                    expiryDate = "2025-01-01"
                )
                HomeScreen(passport = defaultPass, state = state, onEvent = viewModel::onEvent)
//                val options = GmsBarcodeScannerOptions.Builder()
//                    .setBarcodeFormats(
//                        Barcode.FORMAT_QR_CODE)
//                    .build()
//                val scanner = GmsBarcodeScanning.getClient(this, options)
//                scanner.startScan()
//                    .addOnSuccessListener { barcode ->
//                        // Task completed successfully
//                    }
//                    .addOnCanceledListener {
//                        // Task canceled
//                    }
//                    .addOnFailureListener { e ->
//                        // Task failed with an exception
//                    }
            }
        }
    }
}
