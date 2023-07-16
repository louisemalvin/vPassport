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
                                historyViewModel = historyViewModel,
                                passportViewModel = passportViewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController)
                        }
                    }

                }
            }
        }
    }
}
