@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val passportBuilderViewModel by viewModels<PassportBuilderViewModel>()
            val passportViewModel by viewModels<PassportViewModel>()
            val historyViewModel by viewModels<HistoryViewModel>()
            VPassportTheme {
                navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth") {
                    navigation(
                        startDestination = "register", route = "auth"
                    ) {
                        composable("register") {
                            RegisterScreen(navController = navController, passportBuilderViewModel)
                        }
                    }
                    navigation(
                        startDestination = "home", route = "main"
                    ) {
                        composable("home") {
//                            val historyViewModel = it.sharedViewModel<HistoryViewModel>(navController = navController)
//                            val passportViewModel = it.sharedViewModel<PassportViewModel>(navController = navController)
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

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}