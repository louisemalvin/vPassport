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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
    passportBuilderViewModel: PassportBuilderViewModel
) {
    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = MaterialTheme.spacing.medium)
    ) {
        Spacer(Modifier.size(MaterialTheme.spacing.medium))
        Text(text = "Register", style = MaterialTheme.typography.displaySmall)
        var name: String by remember { mutableStateOf("") }
        var docNum: String by remember { mutableStateOf("") }
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
            value = docNum,
            onValueChange = {
                docNum = it
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
                passportBuilderViewModel.defaultPassport(name, docNum, date.value)
                navController.navigate("main") {
                    popUpTo("auth") {
                        inclusive = true
                    }
                }
            }) {
            Text("Use Default Instance")
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