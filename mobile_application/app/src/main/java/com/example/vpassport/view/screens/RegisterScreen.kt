package com.example.vpassport.view.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import com.example.vpassport.view.theme.spacing
import com.example.vpassport.viewmodel.PassportBuilderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    passportBuilderViewModel: PassportBuilderViewModel,
    context: Context
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val errorState by passportBuilderViewModel.statusState.collectAsState()
    val isConnecting by passportBuilderViewModel.isConnecting.collectAsState()
    val errorMessage by passportBuilderViewModel.statusMessage.collectAsState()
    val documentNumber: String by passportBuilderViewModel.documentNumber.collectAsState()
    val dateOfExpiry: String by passportBuilderViewModel.dateOfExpiry.collectAsState()
    val dateOfBirth: String by passportBuilderViewModel.dateOfBirth.collectAsState()
    val nfcState by passportBuilderViewModel.nfcStatus.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {

        val adapter = NfcAdapter.getDefaultAdapter(context)
        val intent = Intent(context, context.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
        adapter.enableForegroundDispatch(context as Activity, pendingIntent, null, filter)

        if (errorState) {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
                passportBuilderViewModel.resetErrorState()
            }
        }
        if (isConnecting) {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    message = "Connecting to mediator",
                    duration = SnackbarDuration.Short
                )
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
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            TextField(
                singleLine = true,
                value = documentNumber,
                onValueChange = passportBuilderViewModel::setDocumentNumber,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Document Number")
                })
            DatePickerField(
                label = "Birth Date",
                passportBuilderViewModel = passportBuilderViewModel,
                date = dateOfBirth
            )
            DatePickerField(
                label = "Expiry Date",
                passportBuilderViewModel = passportBuilderViewModel,
                date = dateOfExpiry
            )
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                   passportBuilderViewModel.createPassport(context)
//                    passportBuilderViewModel.getMediatorAssertion()
                }
            ) {
                Text("Start passport creation")
            }
            Spacer(Modifier.size(MaterialTheme.spacing.medium))
            Text(nfcState)
        }

    }

}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    passportBuilderViewModel: PassportBuilderViewModel,
    label: String,
    date: String
) {
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

    Box {
        TextField(
            value = date,
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
                        if (label.equals("Birth Date")) {
                            passportBuilderViewModel.setDateOfBirth(selectedMillis?.let {
                                SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date(it))
                            } ?: "")
                        } else {
                            passportBuilderViewModel.setDateOfExpiry(selectedMillis?.let {
                                SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date(it))
                            } ?: "")
                        }
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