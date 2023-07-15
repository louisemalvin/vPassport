@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.vpassport.model.data.TempPass
import com.example.vpassport.view.screens.HomeScreen
import com.example.vpassport.view.screens.RegisterScreen
import com.example.vpassport.view.screens.SettingsScreen
import com.example.vpassport.view.theme.VPassportTheme
import com.example.vpassport.viewmodel.HistoryViewModel
import com.example.vpassport.viewmodel.PassportBuilderViewModel
import com.example.vpassport.viewmodel.PassportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val historyViewModel: HistoryViewModel by viewModels()
    private val passportViewModel: PassportViewModel by viewModels()
    private val passportBuilderViewModel: PassportBuilderViewModel by viewModels()
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VPassportTheme {
                val defaultPass = TempPass(
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
                navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth") {
                    navigation(
                        startDestination = "register",
                        route = "auth"
                    ) {
                        composable("register") {
                            RegisterScreen(navController = navController, passportBuilderViewModel)
                        }
                    }
                    navigation(
                        startDestination = "home",
                        route = "main"
                    ) {
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                tempPass = defaultPass,
                                historyViewModel = historyViewModel,
                                passportViewModel = passportViewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController)
                        }
                    }

                }
//                DestinationsNavHost(navGraph = NavGraphs.root)
                
//                HomeScreen(tempPass = defaultPass, historyViewModel = historyViewModel, passportViewModel = passportViewModel)
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
