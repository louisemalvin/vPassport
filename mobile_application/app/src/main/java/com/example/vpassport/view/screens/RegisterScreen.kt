package com.example.vpassport.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vpassport.view.theme.spacing
import com.example.vpassport.viewmodel.PassportBuilderViewModel
import kotlinx.coroutines.launch
import org.bouncycastle.math.raw.Mod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    passportBuilderViewModel: PassportBuilderViewModel
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val errorState by passportBuilderViewModel.errorState.collectAsState()
    val errorMessage by passportBuilderViewModel.errorMessage.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        
        if (errorState) {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
                passportBuilderViewModel.resetErrorState()
            }
        }

        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            Text(text = "Register", style = MaterialTheme.typography.displaySmall)
            var name: String by remember { mutableStateOf("") }
            var documentNumber: String by remember { mutableStateOf("") }
            var date: MutableState<String> = remember { mutableStateOf("") }
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            TextField(
                singleLine = true,
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Full name")
                })
            TextField(
                singleLine = true,
                value = documentNumber,
                onValueChange = {
                    documentNumber = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Document Number")
                })
            DatePickerField(label = "Birth Date", selectedDate = date)
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    passportBuilderViewModel.defaultPassport(name, documentNumber, date.value)
                }
            ) {
                Text("Use Default Instance")
            }
        }

    }

}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: MutableState<String>,
    label: String
) {
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

    Box {
        TextField(
            value = selectedDate.value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            readOnly = true,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = { openDialog = true }),
        )
    }

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        val selectedMillis = datePickerState.selectedDateMillis
                        selectedDate.value = selectedMillis?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                        } ?: ""
                        // TODO: Add to builder
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}