@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import QRCodeScannerViewModel
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.vpassport.view.screens.HomeScreen
import com.example.vpassport.view.screens.RegisterScreen
import com.example.vpassport.view.screens.SettingsScreen
import com.example.vpassport.view.theme.VPassportTheme
import com.example.vpassport.viewmodel.HistoryViewModel
import com.example.vpassport.viewmodel.PassportBuilderViewModel
import com.example.vpassport.viewmodel.PassportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    val passportBuilderViewModel by viewModels<PassportBuilderViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val passportViewModel by viewModels<PassportViewModel>()
            val historyViewModel by viewModels<HistoryViewModel>()
            val qrCodeScannerViewModel by viewModels<QRCodeScannerViewModel>()
            VPassportTheme {
                navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth") {
                    navigation(
                        startDestination = "register", route = "auth"
                    ) {
                        composable("register") {
                            val instanceCreated by passportBuilderViewModel.instanceCreated.collectAsState()
                            if (instanceCreated) {
                                passportBuilderViewModel.resetInstanceCreated()
                                NfcAdapter.getDefaultAdapter(this@MainActivity)
                                    .disableForegroundDispatch(this@MainActivity)
                                navController.navigate("main") {
                                    popUpTo("auth") {
                                        inclusive = true
                                    }
                                }

                            } else {
                                RegisterScreen(
                                    navController = navController,
                                    passportBuilderViewModel = passportBuilderViewModel,
                                    context = this@MainActivity
                                )
                            }


                        }
                    }
                    navigation(
                        startDestination = "home", route = "main"
                    ) {
                        composable("home") {
//                            val historyViewModel = it.sharedViewModel<HistoryViewModel>(navController = navController)
//                            val passportViewModel = it.sharedViewModel<PassportViewModel>(navController = navController)
                            HomeScreen(
                                context = this@MainActivity,
                                navController = navController,
                                historyViewModel = historyViewModel,
                                passportViewModel = passportViewModel,
                                qrCodeScannerViewModel = qrCodeScannerViewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                navController = navController,
                                passportViewModel = passportViewModel,
                                historyViewModel = historyViewModel
                            )
                        }
                    }

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            passportBuilderViewModel.scanPassport(intent)
        }
    }

}