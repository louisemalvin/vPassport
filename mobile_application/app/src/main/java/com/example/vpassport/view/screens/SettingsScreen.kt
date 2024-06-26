package com.example.vpassport.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.vpassport.view.theme.spacing
import com.example.vpassport.viewmodel.HistoryViewModel
import com.example.vpassport.viewmodel.PassportViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    passportViewModel: PassportViewModel,
    historyViewModel: HistoryViewModel
) {
    Scaffold {
        LazyColumn(
            modifier = Modifier
                .safeDrawingPadding()
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            item {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Reset Histories"
                    )
                    TextButton(onClick = { historyViewModel.clearHistory() }) {
                        Text(text = "Reset")
                    }
                }
            }
            item {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Reset Passport"
                    )
                    TextButton(onClick = {
                        passportViewModel.resetPassport()
                        navController.navigate("auth") {
                            popUpTo("main") {
                                inclusive = true
                            }
                        }
                    }) {
                        Text(text = "Reset")
                    }
                }
            }
        }
    }

}